/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.dataflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;

import org.apache.log4j.Logger;

/**
 * <p>
 * An Activity providing nested Dataflow functionality.
 * </p>
 * 
 * @author David Withers
 */
public class DataflowActivity extends
		AbstractAsynchronousActivity<Dataflow> implements NestedDataflow{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(DataflowActivity.class);

	private Edits edits = EditsRegistry.getEdits();

	private Dataflow dataflow;
	
	@Override
	public void configure(Dataflow dataflow)
			throws ActivityConfigurationException {
		this.dataflow=dataflow;
		dataflow.checkValidity();
		buildInputPorts();
		buildOutputPorts();
	}

	@Override
	public Dataflow getConfiguration() {
		return dataflow;
	}

	@Override
	public void executeAsynch(final Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {

			public void run() {

				final WorkflowInstanceFacade facade;
				try {
					facade = edits
							.createWorkflowInstanceFacade(dataflow, new WrappedContext(callback
									.getContext()), callback
									.getParentProcessIdentifier());
				} catch (InvalidDataflowException ex) {
					callback.fail("Invalid workflow", ex);
					return;
				}

				facade.addResultListener(new ResultListener() {
					int outputPortCount = dataflow.getOutputPorts().size();

					Map<String, T2Reference> outputData = new HashMap<String, T2Reference>();

					public void resultTokenProduced(
							WorkflowDataToken dataToken, String port) {
						if (dataToken.getIndex().length == 0) {
							outputData.put(port, dataToken.getData());
							synchronized (this) {
								if (--outputPortCount == 0) {
									// remove listener FIRST as a T2-1137 work around
									// before DataflowOutputPortImpl is made thread safe
									
									// Accept memory leak to avoid non-thread-safe access
									// to DataflowOutputPortImpl
									//facade.removeResultListener(this);
									callback.receiveResult(outputData, dataToken.getIndex());
								}
							}
						}
					}
				});

				facade.fire();

				for (Map.Entry<String, T2Reference> entry : data
						.entrySet()) {
					try {
						WorkflowDataToken token = new WorkflowDataToken(
								callback.getParentProcessIdentifier(),
								new int[] {}, entry.getValue(), callback
										.getContext());
						facade.pushData(token, entry.getKey());
					} catch (TokenOrderException e) {
						callback.fail("Failed to push data into facade", e);
					}
				}

			}

		});
	}

	private void buildInputPorts() throws ActivityConfigurationException {
		inputPorts.clear();
		for (DataflowInputPort dataflowInputPort : dataflow.getInputPorts()) {
			addInput(dataflowInputPort.getName(), dataflowInputPort.getDepth(),
					true, new ArrayList<Class<? extends ExternalReferenceSPI>>(),
					null);
		}
	}

	private void buildOutputPorts() throws ActivityConfigurationException {
		outputPorts.clear();
		//granular depth same as depth - no streaming of results
		for (DataflowOutputPort dataflowOutputPort : dataflow.getOutputPorts()) {
			addOutput(dataflowOutputPort.getName(), dataflowOutputPort
					.getDepth(), dataflowOutputPort
					.getDepth());
		}
	}

	public Dataflow getNestedDataflow() {
		return getConfiguration();
	}

	public class WrappedContext implements InvocationContext {
		private final InvocationContext innerContext;
		public void addEntity(Object entity) {
			// Ignoring addEntity due to T2-1124
		}
		public <T> List<T> getEntities(Class<T> entityType) {
			return innerContext.getEntities(entityType);
		}
		public ProvenanceReporter getProvenanceReporter() {
			return innerContext.getProvenanceReporter();
		}
		public ReferenceService getReferenceService() {
			return innerContext.getReferenceService();
		}
		public WrappedContext(InvocationContext context) {
			this.innerContext = context;
		}
	}

}
