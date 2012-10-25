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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.provenance.ProvenanceConnectorFactory;
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.iteration.CrossProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.DotProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.TerminalNode;

import uk.org.taverna.configuration.database.DatabaseConfiguration;
import uk.org.taverna.platform.capability.api.ActivityService;
import uk.org.taverna.platform.capability.api.DispatchLayerService;
import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataNature;
import uk.org.taverna.platform.data.api.DataService;
import uk.org.taverna.platform.data.api.DataTools;
import uk.org.taverna.platform.data.api.ErrorValue;
import uk.org.taverna.platform.execution.api.AbstractExecution;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * An {@link uk.org.taverna.platform.execution.api.Execution Execution} for executing Taverna
 * workflows on a local Taverna Dataflow Engine.
 *
 * @author David Withers
 */
public class LocalExecution extends AbstractExecution implements ResultListener {

	private static Logger logger = Logger.getLogger(LocalExecution.class.getName());

	private final WorkflowToDataflowMapper mapping;

	private final WorkflowInstanceFacade facade;

	private final LocalExecutionMonitor executionMonitor;

	private final DataService dataService;

	private final ReferenceService referenceService;

	private final DatabaseConfiguration databaseConfiguration;

	private final Set<ProvenanceConnectorFactory> provenanceConnectorFactories;

	private final Map<String, DataflowInputPort> inputPorts = new HashMap<String, DataflowInputPort>();

	private ProvenanceConnector provenanceConnector;

	/**
	 * Constructs an Execution for executing Taverna workflows on a local Taverna Dataflow Engine.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for
	 *            execution
	 * @param workflow
	 *            the <code>Workflow</code> to execute
	 * @param profile
	 *            the <code>Profile</code> to use when executing the <code>Workflow</code>
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>. May be <code>null</code> if the
	 *            <code>Workflow</code> doesn't require any inputs
	 * @param referenceService
	 *            the <code>ReferenceService</code> used to register inputs, outputs and
	 *            intermediate values
	 * @throws InvalidWorkflowException
	 *             if the specified workflow is invalid
	 */
	public LocalExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Map<String, Data> inputs, DataService dataService, ReferenceService referenceService,
			Edits edits, ActivityService activityService,
			DispatchLayerService dispatchLayerService, DatabaseConfiguration databaseConfiguration,
			Set<ProvenanceConnectorFactory> provenanceConnectorFactories)
			throws InvalidWorkflowException {
		super(workflowBundle, workflow, profile, inputs);
		this.dataService = dataService;
		this.referenceService = referenceService;
		this.databaseConfiguration = databaseConfiguration;
		this.provenanceConnectorFactories = provenanceConnectorFactories;
		try {
			mapping = new WorkflowToDataflowMapper(workflowBundle, profile, edits, activityService,
					dispatchLayerService);
			Dataflow dataflow = mapping.getDataflow(workflow);
			for (DataflowInputPort dataflowInputPort : dataflow.getInputPorts()) {
				inputPorts.put(dataflowInputPort.getName(), dataflowInputPort);
			}
			facade = edits.createWorkflowInstanceFacade(dataflow, createContext(), "");
			executionMonitor = new LocalExecutionMonitor(getWorkflowReport(), mapping,
					facade.getIdentifier());
		} catch (InvalidDataflowException e) {
			throw new InvalidWorkflowException(e);
		}
	}

	@Override
	public void delete() {
		cancel();
	}

	@Override
	public void start() {
		MonitorManager.getInstance().addObserver(executionMonitor);
		facade.addResultListener(this);
		facade.fire();
		if (getInputs() != null) {
			for (Entry<String, Data> entry : getInputs().entrySet()) {
				String portName = entry.getKey();
				Data data = entry.getValue();
				T2Reference identifier = registerData(data, inputPorts.get(portName).getDepth());
				int[] index = new int[] {};
				WorkflowDataToken token = new WorkflowDataToken("", index, identifier,
						facade.getContext());
				try {
					facade.pushData(token, portName);
				} catch (TokenOrderException e) {
					logger.log(Level.SEVERE, "Unable to push data for input " + portName, e);
				}
			}
		}
	}

	private T2Reference registerData(Data data, int depth) {
		Object conversion = DataTools.convertToObject(data);
		if (conversion == null) {
			return null;
		}
		return referenceService.register(conversion, depth, true, null);
	}

	@Override
	public void pause() {
		facade.pauseWorkflowRun();
	}

	@Override
	public void resume() {
		facade.resumeWorkflowRun();
	}

	@Override
	public void cancel() {
		facade.cancelWorkflowRun();
		facade.removeResultListener(this);
		MonitorManager.getInstance().removeObserver(executionMonitor);
	}

	@Override
	protected WorkflowReport createWorkflowReport(Workflow workflow) {
		return new WorkflowReport(workflow);
	}

	@Override
	public ProcessorReport createProcessorReport(uk.org.taverna.scufl2.api.core.Processor processor) {
		return new LocalProcessorReport(processor);
	}

	@Override
	public ActivityReport createActivityReport(uk.org.taverna.scufl2.api.activity.Activity activity) {
		return new ActivityReport(activity);
	}

	private InvocationContext createContext() {
		if (databaseConfiguration.isProvenanceEnabled()) {
			String connectorType = databaseConfiguration.getConnectorType();

			for (ProvenanceConnectorFactory factory : provenanceConnectorFactories) {
				if (connectorType.equalsIgnoreCase(factory.getConnectorType())) {
					provenanceConnector = factory.getProvenanceConnector();
				}
				break;
			}

			try {
				if (provenanceConnector != null) {
					provenanceConnector.init();
					provenanceConnector.setReferenceService(referenceService);
				}
			} catch (Exception exception) {
				logger.log(Level.SEVERE, "Error initializing provenance connector", exception);
			}
		}
		InvocationContext context = new InvocationContext() {
			private List<Object> entities = Collections
					.synchronizedList(new ArrayList<Object>());

			@Override
			public <T> List<T> getEntities(Class<T> entityType) {
				List<T> entitiesOfType = new ArrayList<T>();
				synchronized (entities) {
					for (Object entity : entities) {
						if (entityType.isInstance(entity)) {
							entitiesOfType.add(entityType.cast(entity));
						}
					}
				}
				return entitiesOfType;
			}

			@Override
			public void addEntity(Object entity) {
				entities.add(entity);
			}

			@Override
			public ReferenceService getReferenceService() {
				return referenceService;
			}

			@Override
			public ProvenanceReporter getProvenanceReporter() {
				return provenanceConnector;
			}

		};
		if (provenanceConnector != null) {
			provenanceConnector.setInvocationContext(context);
		}
		return context;
	}

	@Override
	public void resultTokenProduced(WorkflowDataToken token, String portName) {
		if (token.getIndex().length == 0) {
			Object object = convertReferenceToObject(token.getData(),
					referenceService, token.getContext());
			getWorkflowReport().getOutputs().put(portName, dataService.create(object));

		}
	}

	private void printDataflow(Dataflow dataflow) {
		System.out.println(dataflow.getInputPorts());
		System.out.println(dataflow.getOutputPorts());
		for (Processor processor : dataflow.getProcessors()) {
			System.out.println("  " + processor);
			System.out.println("    " + processor.getInputPorts());
			System.out.println("    " + processor.getOutputPorts());
			for (IterationStrategy iterationStrategy : processor.getIterationStrategy()
					.getStrategies()) {
				printNode("    ", iterationStrategy.getTerminalNode());
			}
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

	private void printNode(String indent, IterationStrategyNode node) {
		if (node instanceof TerminalNode) {
			System.out.println(indent + "Terminal");
			Enumeration<IterationStrategyNode> children = node.children();
			while (children.hasMoreElements()) {
				printNode(indent + "  ", children.nextElement());
			}
		} else if (node instanceof CrossProduct) {
			System.out.println(indent + "Cross Product");
			Enumeration<IterationStrategyNode> children = node.children();
			while (children.hasMoreElements()) {
				printNode(indent + "  ", children.nextElement());
			}
		} else if (node instanceof DotProduct) {
			System.out.println(indent + "Dot Product");
			Enumeration<IterationStrategyNode> children = node.children();
			while (children.hasMoreElements()) {
				printNode(indent + "  ", children.nextElement());
			}
		} else if (node instanceof NamedInputPortNode) {
			NamedInputPortNode inputPortNode = (NamedInputPortNode) node;
			System.out.println(indent + inputPortNode.getPortName() + "("
					+ inputPortNode.getCardinality() + ")");
		}
	}

	private Object convertReferenceToObject(T2Reference reference,
			ReferenceService referenceService, InvocationContext context) {

		if (reference.getReferenceType() == T2ReferenceType.ReferenceSet) {

			ReferenceSet rs = referenceService.getReferenceSetService().getReferenceSet(reference);
			if (rs == null) {
				throw new ReferenceServiceException("Could not find ReferenceSet " + reference);
			}
			// Check that there are references in the set
			if (rs.getExternalReferences().isEmpty()) {
				throw new ReferenceServiceException("ReferenceSet " + reference + " is empty");
			}

			ReferencedDataNature dataNature = ReferencedDataNature.UNKNOWN;
			for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
				ReferencedDataNature erDataNature = ers.getDataNature();
				if (!erDataNature.equals(ReferencedDataNature.UNKNOWN)) {
					dataNature = erDataNature;
					break;
				}
			}

			// Dereference the object
			Object dataValue;
			try {
				if (dataNature.equals(ReferencedDataNature.TEXT)) {
					dataValue = referenceService.renderIdentifier(reference, String.class, context);
				} else {
					dataValue = referenceService.renderIdentifier(reference, byte[].class, context);
				}
			} catch (ReferenceServiceException rse) {
				logger.log(Level.SEVERE, "Problem rendering T2Reference", rse);
				throw rse;
			}
			return dataValue;
		} else if (reference.getReferenceType() == T2ReferenceType.ErrorDocument) {
			return createErrorValue(reference);
		} else { // it is an IdentifiedList<T2Reference>
			IdentifiedList<T2Reference> identifiedList = referenceService.getListService().getList(
					reference);
			List<Object> list = new ArrayList<Object>();

			for (int j = 0; j < identifiedList.size(); j++) {
				T2Reference ref = identifiedList.get(j);
				list.add(convertReferenceToObject(ref, referenceService, context));
			}
			return list;
		}
	}

	private ErrorValue createErrorValue(T2Reference reference) {
		// Dereference the ErrorDocument
		ErrorDocument errorDocument = referenceService.getErrorDocumentService().getError(reference);
		List<StackTraceElement> stackTrace = getStackTrace(errorDocument.getStackTraceStrings());
		Set<ErrorValue> errorValues = new HashSet<ErrorValue>();
		// dereference error references
		Set<T2Reference> errorReferences = errorDocument.getErrorReferences();
		for (T2Reference errorReference : errorReferences) {
			if (errorReference.getReferenceType() == T2ReferenceType.ErrorDocument) {
				errorValues.add(createErrorValue(errorReference));
			} else if (errorReference.getReferenceType() == T2ReferenceType.IdentifiedList) {
				errorValues.addAll(createErrorValues(errorReference));
			}
		}
		return dataService.createErrorValue(errorDocument.getMessage(), errorDocument.getExceptionMessage(),
				stackTrace, errorValues);
	}

	private List<ErrorValue> createErrorValues(T2Reference reference) {
		List<ErrorValue> errorValues = new ArrayList<ErrorValue>();
		IdentifiedList<T2Reference> identifiedList = referenceService.getListService().getList(
				reference);
		for (T2Reference t2Reference : identifiedList) {
			if (t2Reference.getReferenceType() == T2ReferenceType.ErrorDocument) {
				errorValues.add(createErrorValue(t2Reference));
			} else if (t2Reference.getReferenceType() == T2ReferenceType.IdentifiedList) {
				errorValues.addAll(createErrorValues(t2Reference));
			}
		}
		return errorValues;
	}

	private List<StackTraceElement> getStackTrace(List<StackTraceElementBean> stackTraceBeans) {
		List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
		for (StackTraceElementBean stackTraceBean : stackTraceBeans) {
			stackTrace.add(new StackTraceElement(stackTraceBean.getClassName(), stackTraceBean.getMethodName(),
					stackTraceBean.getFileName(), stackTraceBean.getLineNumber()));
		}
		return stackTrace;
	}

}
