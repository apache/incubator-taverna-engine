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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorManager.AddPropertiesMessage;
import net.sf.taverna.t2.monitor.MonitorManager.DeregisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorManager.MonitorMessage;
import net.sf.taverna.t2.monitor.MonitorManager.RegisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchResultEvent;

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.Invocation;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.StatusReport;
import uk.org.taverna.platform.report.WorkflowReport;

/**
 * A workflow monitor for local executions.
 *
 * @author David Withers
 */
public class LocalExecutionMonitor implements Observer<MonitorMessage> {

	private static final Logger logger = Logger.getLogger(LocalExecutionMonitor.class.getName());

	private Map<String, StatusReport> reports;

	private Map<String, Invocation> invocations;

	private Map<String, String> invocationToActivity;

	private Map<T2Reference, Path> referenceToPath;

	private final String facadeId;

	private final Bundle dataBundle;

	private final WorkflowReport workflowReport;

	public LocalExecutionMonitor(WorkflowReport workflowReport, Bundle dataBundle, WorkflowToDataflowMapper mapping,
			String facadeId) throws InvalidWorkflowException {
		this.workflowReport = workflowReport;
		this.dataBundle = dataBundle;
		this.facadeId = facadeId;
		reports = new HashMap<>();
		invocations = new HashMap<>();
		invocationToActivity = new HashMap<>();
		referenceToPath = new HashMap<>();
		mapReports("", workflowReport, mapping);
	}

	private void mapReports(String id, WorkflowReport workflowReport, WorkflowToDataflowMapper mapping)
			throws InvalidWorkflowException {
		Dataflow dataflow = mapping.getDataflow(workflowReport.getSubject());
		String dataflowId = id + dataflow.getLocalName();
		reports.put(dataflowId, workflowReport);
		for (ProcessorReport processorReport : workflowReport.getProcessorReports()) {
			Processor processor = mapping.getDataflowProcessor(processorReport.getSubject());
			String processorId = dataflowId + processor.getLocalName();
			reports.put(processorId, (LocalProcessorReport) processorReport);
			for (ActivityReport activityReport : processorReport.getActivityReports()) {
				Activity<?> activity = mapping.getDataflowActivity(activityReport.getSubject());
				String activityId = processorId + String.valueOf(activity.hashCode());
				reports.put(activityId, activityReport);
				WorkflowReport nestedWorkflowReport = activityReport.getNestedWorkflowReport();
				if (nestedWorkflowReport != null) {
					mapReports(activityId, nestedWorkflowReport, mapping);
				}
			}
		}
	}

	public void notify(Observable<MonitorMessage> sender, MonitorMessage message) throws Exception {
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
				addPropertiesToNode(owningProcess, addMessage.getNewProperties());
			} else {
				logger.warning("Unknown message " + message + " from " + sender);
			}
		}
	}

	public void registerNode(Object dataflowObject, String[] owningProcess,
			Set<MonitorableProperty<?>> properties) {
		if (dataflowObject instanceof Dataflow) {
			WorkflowReport report = (WorkflowReport) reports.get(getReportId(owningProcess));
			Invocation invocation = new Invocation("", getParentInvocation(owningProcess), report);
			invocations.put(getInvocationId(owningProcess, new int[0]), invocation);
			if (report.equals(workflowReport)) {
				if (DataBundles.hasInputs(dataBundle)) {
					try {
						invocation.setInputs(DataBundles.getPorts(DataBundles.getInputs(dataBundle)));
					} catch (IOException e) {
						logger.log(Level.WARNING, "Error setting input ports", e);
					}
				}
			}
		} else if (dataflowObject instanceof Processor) {
			StatusReport report = reports.get(getReportId(owningProcess));
			if (report instanceof LocalProcessorReport) {
				LocalProcessorReport localProcessorReport = (LocalProcessorReport) report;
				localProcessorReport.addProperties(properties);
			}
		} else if (dataflowObject instanceof Activity) {
			Activity<?> activity = (Activity<?>) dataflowObject;
			invocationToActivity.put(owningProcess[owningProcess.length - 1], String.valueOf(activity.hashCode()));
			ActivityReport activityReport = (ActivityReport) reports.get(getReportId(owningProcess));
			StatusReport parentReport = activityReport.getParentReport();
		} else if (dataflowObject instanceof DispatchJobEvent) {
			DispatchJobEvent jobEvent = (DispatchJobEvent) dataflowObject;
			StatusReport report = reports.get(getReportId(owningProcess));
			Invocation invocation = new Invocation(indexToString(jobEvent.getIndex()), getParentInvocation(owningProcess), report);
			invocations.put(getInvocationId(owningProcess, jobEvent.getIndex()), invocation);
			try {
				invocation.setInputs(convert(dataBundle, jobEvent.getData(), jobEvent.getContext()));
				report.addInvocation(invocation);
			} catch (IOException | URISyntaxException e) {
				logger.log(Level.WARNING,
						"Error saving intermediate inputs for " + jobEvent.getOwningProcess(), e);
			}
		} else if (dataflowObject instanceof DispatchResultEvent) {
			DispatchResultEvent resultEvent = (DispatchResultEvent) dataflowObject;
			Invocation invocation = invocations.get(getInvocationId(owningProcess, resultEvent.getIndex()));
			try {
				invocation.setOutputs(convert(dataBundle, resultEvent.getData(), resultEvent.getContext()));
			} catch (IOException | URISyntaxException e) {
				logger.log(
						Level.WARNING,
						"Error saving intermediate outputs for " + resultEvent.getOwningProcess(),
						e);
			}
		}
	}

	public void deregisterNode(String[] owningProcess) {
		StatusReport report = reports.get(getReportId(owningProcess));
		if (report != null) {
			report.setCompletedDate(new Date());
			if (report instanceof LocalProcessorReport) {
				LocalProcessorReport processorReport = (LocalProcessorReport) report;
				processorReport.saveProperties();
			}
		}
	}

	public void addPropertiesToNode(String[] owningProcess,
			Set<MonitorableProperty<?>> newProperties) {
		StatusReport report = reports.get(getReportId(owningProcess));
		if (report instanceof LocalProcessorReport) {
			LocalProcessorReport processorReport = (LocalProcessorReport) report;
			processorReport.addProperties(newProperties);
		}
	}

	/**
	 * Converts the owning process array to a string.
	 *
	 * @param owningProcess
	 *            the owning process id
	 * @return the owning process as a string
	 */
	private String getOwningProcessId(String[] owningProcess) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < owningProcess.length; i++) {
			if (i % 4 != 0) {
				sb.append(owningProcess[i]);
			}
		}
		return sb.toString();
	}

	private Invocation getParentInvocation(String[] owningProcess) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < owningProcess.length - 1; i++) {
			if (i % 4 != 0) {
				sb.append(owningProcess[i]);
			}
		}
		return invocations.get(sb.toString());
	}

	private String getInvocationId(String[] owningProcess, int[] index) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < owningProcess.length; i++) {
			if (i % 4 != 0) {
				sb.append(owningProcess[i]);
			}
		}
		return sb.toString() + indexToString(index);
	}

	private String getReportId(String[] owningProcess) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1, position = 0; i < owningProcess.length; i++) {
			if (i % 4 != 0) {
				if (position == 2) {
					sb.append(invocationToActivity.get(owningProcess[i]));
					position = 0;
				} else {
					sb.append(owningProcess[i]);
					position++;
				}
			}
		}
		return sb.toString();
	}

	public String getProcessorId(String[] owningProcess) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1, skip = 0; i < owningProcess.length; i++, skip--) {
			if (i <= 2 || skip < 0) {
				sb.append(owningProcess[i]);
				skip = 3;
			}
		}
		return sb.toString();
	}

	private String indexToString(int[] index) {
		StringBuilder indexString = new StringBuilder();
		for (int i = 0; i < index.length; i++) {
			if (i != 0) {
				indexString.append(":");
			}
			indexString.append(index[i] + 1);
		}
		return indexString.toString();
	}

	private Map<String, Path> convert(Bundle dataBundle, Map<String, T2Reference> data,
			InvocationContext context) throws IOException, URISyntaxException {
		Map<String, Path> result = new HashMap<>();
		for (Entry<String, T2Reference> entry : data.entrySet()) {
			result.put(entry.getKey(), getIntermediate(dataBundle, entry.getValue(), context));
		}
		return result;
	}

	private Path getIntermediate(Bundle dataBundle, T2Reference t2Reference,
			InvocationContext context) throws IOException, URISyntaxException {
		if (referenceToPath.containsKey(t2Reference)) {
			return referenceToPath.get(t2Reference);
		}
		Path path = referencePath(dataBundle, t2Reference);
		T2ReferenceConverter.convertReferenceToPath(path, t2Reference,
				context.getReferenceService(), context);
		referenceToPath.put(t2Reference, path);
		return path;
	}

	private Path referencePath(Bundle dataBundle, T2Reference t2Ref) throws IOException {
		String local = t2Ref.getLocalPart();
		try {
			return DataBundles.getIntermediate(dataBundle, UUID.fromString(local));
		} catch (IllegalArgumentException ex) {
			return DataBundles.getIntermediates(dataBundle).resolve(t2Ref.getNamespacePart())
					.resolve(t2Ref.getLocalPart());
		}
	}

}
