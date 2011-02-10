/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.execution.impl.local;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.invocation.impl.InvocationContextImpl;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.platform.execution.api.AbstractExecution;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * An {@link uk.org.taverna.platform.execution.api.Execution Execution} for executing Taverna workflows on a local Taverna Dataflow Engine.
 * 
 * @author David Withers
 */
public class LocalExecution extends AbstractExecution implements ResultListener {

	private static Logger logger = Logger.getLogger(LocalExecution.class);

	private WorkflowToDataflowMapper mapping;

	private WorkflowInstanceFacade facade;

	private LocalExecutionMonitor executionMonitor;

	public LocalExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Map<String, T2Reference> inputs, ReferenceService referenceService, Edits edits, ActivityService activityService)
			throws InvalidWorkflowException {
		super(workflowBundle, workflow, profile, inputs, referenceService);
		try {
			mapping = new WorkflowToDataflowMapper(workflowBundle, workflow, profile, edits, activityService);
			Dataflow dataflow = mapping.getDataflow();
			printDataflow(dataflow);
			facade = edits.createWorkflowInstanceFacade(dataflow, createContext(), "");
			executionMonitor = new LocalExecutionMonitor((LocalWorkflowReport) getWorkflowReport(),
					mapping, facade.getIdentifier());
		} catch (InvalidDataflowException e) {
			// TODO do something with the validation report
			DataflowValidationReport report = e.getDataflowValidationReport();
			System.out.println("Workflow incomplete = " + report.isWorkflowIncomplete());
			throw new InvalidWorkflowException(e);
		}
	}

	public void start() {
		MonitorManager.getInstance().addObserver(executionMonitor);
		facade.addResultListener(this);
		facade.fire();
		if (getInputs() != null) {
			for (Entry<String, T2Reference> entry : getInputs().entrySet()) {
				String portName = entry.getKey();
				T2Reference identifier = entry.getValue();
				int[] index = new int[] {};
				WorkflowDataToken token = new WorkflowDataToken("", index, identifier,
						facade.getContext());
				try {
					facade.pushData(token, portName);
				} catch (TokenOrderException e) {
					logger.error("Unable to push data for input " + portName, e);
				}
			}
		}
	}

	public void pause() {
		facade.pauseWorkflowRun();
	}

	public void resume() {
		facade.resumeWorkflowRun();
	}

	public void cancel() {
		facade.cancelWorkflowRun();
		facade.removeResultListener(this);
		MonitorManager.getInstance().removeObserver(executionMonitor);
	}

	@Override
	protected WorkflowReport createWorkflowReport(Workflow workflow) {
		return new LocalWorkflowReport(workflow);
	}

	private InvocationContext createContext() {
		ProvenanceReporter provenanceConnector = null;

		// if (ConfigurationManager.isProvenanceEnabled()) {
		// String connectorType = ConfigurationManager.getProvenanceConnectorType();
		//
		// for (ProvenanceConnectorFactory factory : ProvenanceConnectorFactoryRegistry
		// .getInstance().getInstances()) {
		// if (connectorType.equalsIgnoreCase(factory.getConnectorType())) {
		// provenanceConnector = factory.getProvenanceConnector();
		// }
		// break;
		// }
		//
		// try {
		// if (provenanceConnector != null) {
		// provenanceConnector.init();
		// provenanceConnector.setReferenceService(getReferenceService());
		// }
		// } catch (Exception exception) {
		// logger.error("Error initializing provenance connector", exception);
		// }
		// }
		InvocationContext context = new InvocationContextImpl(getReferenceService(),
				provenanceConnector);
		if (provenanceConnector != null) {
			provenanceConnector.setInvocationContext(context);
		}
		return context;
	}

	public void resultTokenProduced(WorkflowDataToken token, String portName) {
		if (token.getIndex().length == 0) {
			getWorkflowReport().getOutputs().put(portName, token.getData());
		}
	}

	private void printDataflow(Dataflow dataflow) {
		System.out.println(dataflow.getInputPorts());
		System.out.println(dataflow.getOutputPorts());
		for (Processor processor : dataflow.getProcessors()) {
			System.out.println("  " + processor);
			System.out.println("    " + processor.getInputPorts());
			System.out.println("    " + processor.getOutputPorts());
			for (Activity<?> activity : processor.getActivityList()) {
				System.out.println("    " + activity);
				System.out.println("    " + activity.getInputPorts());
				System.out.println("    " + activity.getInputPortMapping());
				System.out.println("    " + activity.getOutputPorts());
				System.out.println("    " + activity.getOutputPortMapping());
			}
			System.out.println("    " + processor.getActivityList().get(0));
		}
		System.out.println(dataflow.getLinks());
	}

}
