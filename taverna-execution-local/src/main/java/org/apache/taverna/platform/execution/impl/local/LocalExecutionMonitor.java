/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.platform.execution.impl.local;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.taverna.facade.ResultListener;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorManager.AddPropertiesMessage;
import org.apache.taverna.monitor.MonitorManager.DeregisterNodeMessage;
import org.apache.taverna.monitor.MonitorManager.MonitorMessage;
import org.apache.taverna.monitor.MonitorManager.RegisterNodeMessage;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.StackTraceElementBean;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceType;
import org.apache.taverna.reference.impl.external.file.FileReference;
import org.apache.taverna.reference.impl.external.http.HttpReference;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.Invocation;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.StatusReport;
import org.apache.taverna.platform.report.WorkflowReport;

/**
 * A workflow monitor for local executions.
 * 
 * @author David Withers
 */
public class LocalExecutionMonitor implements Observer<MonitorMessage> {
	private static final Logger logger = Logger
			.getLogger(LocalExecutionMonitor.class.getName());
	private static final String ID_SEPARATOR = "/";

	private Map<String, StatusReport<?, ?>> reports;
	private Map<String, Invocation> invocations;
	private Map<String, String> invocationToActivity;
	private Map<T2Reference, Path> referenceToPath;
	private final String facadeId;
	private final Bundle dataBundle;

	public LocalExecutionMonitor(WorkflowReport workflowReport,
			Bundle dataBundle, WorkflowToDataflowMapper mapping, String facadeId)
			throws InvalidWorkflowException {
		this.dataBundle = dataBundle;
		this.facadeId = facadeId;
		reports = new HashMap<>();
		invocations = new HashMap<>();
		invocationToActivity = new HashMap<>();
		referenceToPath = new HashMap<>();
		mapReports("", workflowReport, mapping);
	}

	private void mapReports(String id, WorkflowReport workflowReport,
			WorkflowToDataflowMapper mapping) throws InvalidWorkflowException {
		Dataflow dataflow = mapping.getDataflow(workflowReport.getSubject());
		String dataflowId = null;
		if (id.isEmpty()) {
			dataflowId = dataflow.getLocalName();
		} else {
			dataflowId = id + ID_SEPARATOR + dataflow.getLocalName();
		}
		reports.put(dataflowId, workflowReport);
		for (ProcessorReport processorReport : workflowReport
				.getProcessorReports()) {
			Processor processor = mapping.getDataflowProcessor(processorReport
					.getSubject());
			String processorId = dataflowId + ID_SEPARATOR
					+ processor.getLocalName();
			reports.put(processorId, (LocalProcessorReport) processorReport);
			for (ActivityReport activityReport : processorReport
					.getActivityReports()) {
				Activity<?> activity = mapping
						.getDataflowActivity(activityReport.getSubject());
				String activityId = processorId + ID_SEPARATOR
						+ activity.hashCode();
				reports.put(activityId, activityReport);
				WorkflowReport nestedWorkflowReport = activityReport
						.getNestedWorkflowReport();
				if (nestedWorkflowReport != null)
					mapReports(activityId, nestedWorkflowReport, mapping);
			}
		}
	}

	@Override
	public void notify(Observable<MonitorMessage> sender, MonitorMessage message)
			throws Exception {
		String[] owningProcess = message.getOwningProcess();
		if (owningProcess.length > 0 && owningProcess[0].equals(facadeId)) {
			if (message instanceof RegisterNodeMessage) {
				RegisterNodeMessage regMessage = (RegisterNodeMessage) message;
				registerNode(regMessage.getWorkflowObject(), owningProcess,
						regMessage.getProperties());
			} else if (message instanceof DeregisterNodeMessage) {
				deregisterNode(owningProcess);
			} else if (message instanceof AddPropertiesMessage) {
				AddPropertiesMessage addMessage = (AddPropertiesMessage) message;
				addPropertiesToNode(owningProcess,
						addMessage.getNewProperties());
			} else {
				logger.warning("Unknown message " + message + " from " + sender);
			}
		}
	}

	public void registerNode(Object dataflowObject, String[] owningProcess,
			Set<MonitorableProperty<?>> properties) {
		if (dataflowObject instanceof Dataflow) {
			Dataflow dataflow = (Dataflow) dataflowObject;
			Invocation parentInvocation = invocations
					.get(getParentInvocationId(owningProcess));
			WorkflowReport report = (WorkflowReport) reports
					.get(getReportId(owningProcess));
			report.setStartedDate(new Date());
			Invocation invocation = new Invocation(
					getInvocationName(owningProcess), parentInvocation, report);
			if (parentInvocation == null) {
				if (DataBundles.hasInputs(dataBundle)) {
					try {
						invocation.setInputs(DataBundles.getPorts(DataBundles
								.getInputs(dataBundle)));
					} catch (IOException e) {
						logger.log(WARNING, "Error setting input ports", e);
					}
				}
				try {
					Path outputs = DataBundles.getOutputs(dataBundle);
					DataflowResultListener dataflowResultListener = new DataflowResultListener(
							outputs);
					for (DataflowOutputPort dataflowOutputPort : dataflow
							.getOutputPorts()) {
						String portName = dataflowOutputPort.getName();
						Path portPath = DataBundles.getPort(outputs, portName);
						invocation.setOutput(portName, portPath);
						dataflowOutputPort
								.addResultListener(dataflowResultListener);
					}
				} catch (IOException e) {
					logger.log(WARNING, "Error setting output ports", e);
				}
				invocations.put(getInvocationId(owningProcess), invocation);
			} else {
				invocation.setInputs(parentInvocation.getInputs());
				NestedDataflowResultListener resultListener = new NestedDataflowResultListener(
						invocation);
				for (DataflowOutputPort dataflowOutputPort : dataflow
						.getOutputPorts()) {
					dataflowOutputPort.addResultListener(resultListener);
				}
				invocations.put(getInvocationId(owningProcess), invocation);
			}
		} else if (dataflowObject instanceof Processor) {
			StatusReport<?, ?> report = reports.get(getReportId(owningProcess));
			report.setStartedDate(new Date());
			if (report instanceof LocalProcessorReport)
				((LocalProcessorReport) report).addProperties(properties);
		} else if (dataflowObject instanceof Activity) {
			Activity<?> activity = (Activity<?>) dataflowObject;
			invocationToActivity.put(owningProcess[owningProcess.length - 1],
					String.valueOf(activity.hashCode()));
		} else if (dataflowObject instanceof DispatchJobEvent) {
			DispatchJobEvent jobEvent = (DispatchJobEvent) dataflowObject;
			StatusReport<?, ?> report = reports.get(getReportId(owningProcess));
			// create a new invocation
			Invocation parentInvocation;
			Invocation invocation;

			if (report instanceof ActivityReport) {
				parentInvocation = invocations
						.get(getParentInvocationId(owningProcess)
								+ indexToString(jobEvent.getIndex()));
				invocation = new Invocation(getInvocationName(owningProcess),
						jobEvent.getIndex(), parentInvocation, report);
				invocations.put(getInvocationId(owningProcess), invocation);
			} else {
				parentInvocation = invocations
						.get(getParentInvocationId(owningProcess));
				invocation = new Invocation(getInvocationName(owningProcess)
						+ indexToString(jobEvent.getIndex()),
						jobEvent.getIndex(), parentInvocation, report);
				invocations.put(getInvocationId(owningProcess)
						+ indexToString(jobEvent.getIndex()), invocation);
			}
			// set the invocation inputs
			try {
				for (Entry<String, T2Reference> inputInfo : jobEvent.getData()
						.entrySet()) {
					invocation.setInput(
							inputInfo.getKey(),
							getIntermediate(inputInfo.getValue(),
									jobEvent.getContext()));
				}
			} catch (IOException | URISyntaxException e) {
				logger.log(WARNING, "Error saving intermediate inputs for "
						+ jobEvent.getOwningProcess(), e);
			}

		} else if (dataflowObject instanceof DispatchResultEvent) {
			DispatchResultEvent resultEvent = (DispatchResultEvent) dataflowObject;
			StatusReport<?, ?> report = reports.get(getReportId(owningProcess));
			// find the invocation
			Invocation invocation;
			if (report instanceof ActivityReport)
				invocation = invocations.remove(getInvocationId(owningProcess));
			else
				invocation = invocations.remove(getInvocationId(owningProcess)
						+ indexToString(resultEvent.getIndex()));

			if (invocation == null) {
				logger.log(SEVERE, "Can't find invocation for owning process "
						+ owningProcess);
				return;
			}

			// set the invocation outputs
			try {
				for (Entry<String, T2Reference> outputInfo : resultEvent.getData()
						.entrySet()) {
					invocation.setOutput(
							outputInfo.getKey(),
							getIntermediate(outputInfo.getValue(),
									resultEvent.getContext()));
				}
			} catch (IOException | URISyntaxException e) {
				logger.log(WARNING, "Error saving intermediate outputs for "
						+ resultEvent.getOwningProcess(), e);
			}
			invocation.setCompletedDate(new Date());
		}
	}

	public void deregisterNode(String[] owningProcess) {
		StatusReport<?, ?> report = reports.get(getReportId(owningProcess));
		if (report == null) {
			return;
		} else if (report instanceof WorkflowReport) {
			Invocation invocation = invocations
					.remove(getInvocationId(owningProcess));
			invocation.setCompletedDate(new Date());
			report.setCompletedDate(new Date());
		} else if (report instanceof LocalProcessorReport) {
			((LocalProcessorReport) report).saveProperties();
			report.setCompletedDate(new Date());
		} else if (report instanceof ActivityReport) {
			// Invocation may still exist if the activity failed
			Invocation invocation = invocations
					.remove(getInvocationId(owningProcess));
			if (invocation != null) {
				invocation.setCompletedDate(new Date());
				report.setFailedDate(new Date());
			} else
				report.setCompletedDate(new Date());
			invocationToActivity
					.remove(owningProcess[owningProcess.length - 1]);
		}
	}

	public void addPropertiesToNode(String[] owningProcess,
			Set<MonitorableProperty<?>> newProperties) {
		StatusReport<?, ?> report = reports.get(getReportId(owningProcess));
		if (report instanceof LocalProcessorReport) {
			LocalProcessorReport processorReport = (LocalProcessorReport) report;
			processorReport.addProperties(newProperties);
		}
	}

	private String getParentInvocationId(String[] owningProcess) {
		List<String> id = new ArrayList<>();
		for (int i = 1; i < owningProcess.length - 1; i++)
			if (i % 4 != 0)
				id.add(owningProcess[i]);
		return toPath(id);
	}

	private String getInvocationId(String[] owningProcess) {
		List<String> id = new ArrayList<>();
		for (int i = 1; i < owningProcess.length; i++)
			if (i % 4 != 0)
				id.add(owningProcess[i]);
		return toPath(id);
	}

	private String getInvocationName(String[] owningProcess) {
		return owningProcess[owningProcess.length - 1];
	}

	private String toPath(List<String> id) {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String string : id) {
			sb.append(sep).append(string);
			sep = ID_SEPARATOR;
		}
		return sb.toString();
	}

	private String getReportId(String[] owningProcess) {
		List<String> id = new ArrayList<>();
		for (int i = 1, position = 0; i < owningProcess.length; i++) {
			if (i % 4 == 0)
				continue;
			if (position == 2) {
				id.add(invocationToActivity.get(owningProcess[i]));
				position = 0;
			} else {
				id.add(owningProcess[i]);
				position++;
			}
		}
		return toPath(id);
	}

	public String getProcessorId(String[] owningProcess) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1, skip = 0; i < owningProcess.length; i++, skip--)
			if (i <= 2 || skip < 0) {
				sb.append(owningProcess[i]);
				skip = 3;
			}
		return sb.toString();
	}

	private String indexToString(int[] index) {
		StringBuilder indexString = new StringBuilder();
		for (int i = 0; i < index.length; i++) {
			if (i != 0)
				indexString.append(":");
			indexString.append(index[i] + 1);
		}
		return indexString.toString();
	}

	private Path getIntermediate(T2Reference t2Reference,
			InvocationContext context) throws IOException, URISyntaxException {
		if (referenceToPath.containsKey(t2Reference))
			return referenceToPath.get(t2Reference);

		Path path = referencePath(t2Reference);
		convertReferenceToPath(path, t2Reference, context);
		referenceToPath.put(t2Reference, path);
		return path;
	}

	private Path referencePath(T2Reference t2Reference) throws IOException {
		String local = t2Reference.getLocalPart();
		try {
			return DataBundles.getIntermediate(dataBundle,
					UUID.fromString(local));
		} catch (IllegalArgumentException ex) {
			return DataBundles.getIntermediates(dataBundle)
					.resolve(t2Reference.getNamespacePart())
					.resolve(t2Reference.getLocalPart());
		}
	}

	public static String getStackTraceElementString(
			StackTraceElementBean stackTraceElement) {
		StringBuilder sb = new StringBuilder();
		sb.append(stackTraceElement.getClassName()).append('.')
				.append(stackTraceElement.getMethodName());
		if (stackTraceElement.getFileName() == null) {
			sb.append("(unknown file)");
		} else {
			sb.append('(').append(stackTraceElement.getFileName()).append(':')
					.append(stackTraceElement.getLineNumber()).append(')');
		}
		return sb.toString();
	}

	public void convertReferenceToPath(Path path, T2Reference reference,
			InvocationContext context) throws IOException, URISyntaxException {
		ReferenceService referenceService = context.getReferenceService();
		if (reference.getReferenceType() == T2ReferenceType.ReferenceSet) {
			if (DataBundles.isMissing(path)) {
				ReferenceSet rs = referenceService.getReferenceSetService()
						.getReferenceSet(reference);
				if (rs == null)
					throw new ReferenceServiceException(
							"Could not find ReferenceSet " + reference);
				// Check that there are references in the set
				if (rs.getExternalReferences().isEmpty())
					throw new ReferenceServiceException("ReferenceSet "
							+ reference + " is empty");

				for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
					if (ers instanceof FileReference) {
						URI uri = ((FileReference) ers).getFile().toURI();
						DataBundles.setReference(path, uri);
					} else if (ers instanceof HttpReference) {
						URI uri = ((HttpReference) ers).getHttpUrl().toURI();
						DataBundles.setReference(path, uri);
					} else {
						try (InputStream in = ers.openStream(context)) {
							Files.copy(in, path);
						}
					}
				}
			}
		} else if (reference.getReferenceType() == T2ReferenceType.ErrorDocument) {
			if (DataBundles.isMissing(path)) {
				ErrorDocument errorDocument = referenceService
						.getErrorDocumentService().getError(reference);
				String message = errorDocument.getMessage();
				StringBuilder trace = new StringBuilder();
				if (errorDocument.getExceptionMessage() != null
						&& !errorDocument.getExceptionMessage().isEmpty()) {
					trace.append(errorDocument.getExceptionMessage());
					trace.append("\n");
				}
				List<StackTraceElementBean> stackTraceStrings = errorDocument
						.getStackTraceStrings();
				for (StackTraceElementBean stackTraceElement : stackTraceStrings) {
					trace.append(getStackTraceElementString(stackTraceElement));
					trace.append("\n");
				}
				List<Path> causes = new ArrayList<>();
				for (T2Reference errorReference : errorDocument
						.getErrorReferences())
					causes.add(getIntermediate(errorReference, context));
				DataBundles.setError(path, message, trace.toString(),
						causes.toArray(new Path[causes.size()]));
			}
		} else { // it is an IdentifiedList<T2Reference>
			IdentifiedList<T2Reference> identifiedList = referenceService
					.getListService().getList(reference);
			if (!DataBundles.isList(path))
				DataBundles.createList(path);
			for (T2Reference ref : identifiedList)
				convertReferenceToPath(DataBundles.newListItem(path), ref,
						context);
		}
	}

	private class NestedDataflowResultListener implements ResultListener {
		private final Invocation invocation;

		public NestedDataflowResultListener(Invocation invocation) {
			this.invocation = invocation;
		}

		@Override
		public void resultTokenProduced(WorkflowDataToken token, String portName) {
			try {
				if (token.isFinal())
					invocation
							.setOutput(
									portName,
									getIntermediate(token.getData(),
											token.getContext()));
			} catch (IOException | URISyntaxException e) {
				logger.log(SEVERE, "Unable to convert T2Reference", e);
			}
		}

	}

	private class DataflowResultListener implements ResultListener {
		private Path outputs;
		private Map<String, Integer> depthSeen = new HashMap<>();

		public DataflowResultListener(Path outputs) {
			this.outputs = outputs;
		}

		@Override
		public void resultTokenProduced(WorkflowDataToken token, String portName) {
			Integer depth = depthSeen.get(portName);
			if (depth == null || depth.equals(token.getIndex().length)) {
				if (depth == null)
					depthSeen.put(portName, token.getIndex().length);
				try {
					Path port = DataBundles.getPort(outputs, portName);
					Path path = getPath(port, 0, token.getIndex());
					convertReferenceToPath(path, token.getData(),
							token.getContext());
				} catch (IOException | URISyntaxException e) {
					logger.log(SEVERE, "Unable to convert T2Reference", e);
				}
			}
		}

		private Path getPath(Path path, int depth, int[] index)
				throws IOException {
			if (depth == index.length)
				return path;
			if (!DataBundles.isList(path))
				DataBundles.createList(path);
			return getPath(DataBundles.getListItem(path, index[depth]),
					depth + 1, index);
		}
	}
}
