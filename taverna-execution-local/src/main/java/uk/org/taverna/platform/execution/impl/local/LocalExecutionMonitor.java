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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorManager.AddPropertiesMessage;
import net.sf.taverna.t2.monitor.MonitorManager.DeregisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorManager.MonitorMessage;
import net.sf.taverna.t2.monitor.MonitorManager.RegisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;

/**
 * A workflow monitor for local executions.
 *
 * @author David Withers
 */
public class LocalExecutionMonitor implements Observer<MonitorMessage> {

	private static final Logger logger = Logger.getLogger(LocalExecutionMonitor.class.getName());

	private Map<String, Object> dataflowObjects;

	private Map<Dataflow, WorkflowReport> workflowReports;

	private Map<Processor, LocalProcessorReport> processorReports;

	private Map<Activity<?>, ActivityReport> activityReports;

	private Map<Processor, AtomicInteger> processorInvocations;

	private Map<Activity<?>, AtomicInteger> activityInvocations;

	private final String facadeId;

	public LocalExecutionMonitor(WorkflowReport workflowReport, WorkflowToDataflowMapper mapping, String facadeId) throws InvalidWorkflowException {
		this.facadeId = facadeId;
		dataflowObjects = new HashMap<String, Object>();
		workflowReports = new HashMap<Dataflow, WorkflowReport>();
		processorReports = new HashMap<Processor, LocalProcessorReport>();
		processorInvocations = new HashMap<Processor, AtomicInteger>();
		activityReports = new HashMap<Activity<?>, ActivityReport>();
		activityInvocations = new HashMap<Activity<?>, AtomicInteger>();
		mapReports(workflowReport, mapping);
	}

	private void mapReports(WorkflowReport workflowReport, WorkflowToDataflowMapper mapping) throws InvalidWorkflowException {
		workflowReports.put(mapping.getDataflow(workflowReport.getSubject()), workflowReport);
		for (ProcessorReport processorReport : workflowReport.getChildReports()) {
			Processor processor = mapping.getDataflowProcessor(processorReport.getSubject());
			processorReports.put(processor, (LocalProcessorReport) processorReport);
			processorInvocations.put(processor, new AtomicInteger());
			for (ActivityReport activityReport : processorReport.getChildReports()) {
				Activity<?> activity = mapping.getDataflowActivity(activityReport.getSubject());
				activityReports.put(activity, activityReport);
				activityInvocations.put(activity, new AtomicInteger());
				for (WorkflowReport nestedWorkflowReport : activityReport.getChildReports()) {
					mapReports(nestedWorkflowReport, mapping);
				}
			}
		}
	}

	public void notify(Observable<MonitorMessage> sender, MonitorMessage message) throws Exception {
		String[] owningProcess = message.getOwningProcess();
		if (owningProcess.length > 0 && owningProcess[0].equals(facadeId)) {
			if (message instanceof RegisterNodeMessage) {
				RegisterNodeMessage regMessage = (RegisterNodeMessage) message;
				registerNode(regMessage.getWorkflowObject(), owningProcess, regMessage.getProperties());
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
		dataflowObjects.put(getOwningProcessId(owningProcess), dataflowObject);
		if (dataflowObject instanceof Dataflow) {
			Dataflow dataflow = (Dataflow) dataflowObject;
			workflowReports.get(dataflow).setStartedDate(new Date());
		} else if (dataflowObject instanceof Processor) {
			Processor dataflowProcessor = (Processor) dataflowObject;
			if (processorInvocations.get(dataflowProcessor).getAndIncrement() == 0) {
				LocalProcessorReport processorReport = processorReports.get(dataflowProcessor);
				processorReport.addProperties(properties);
			}
		} else if (dataflowObject instanceof Activity) {
			Activity<?> activity = (Activity<?>) dataflowObject;
			if (activityInvocations.get(activity).getAndIncrement() == 0) {
				ActivityReport activityReport = activityReports.get(activity);
				ProcessorReport parentReport = activityReport.getParentReport();
				parentReport.setStartedDate(new Date());
				activityReport.setStartedDate(new Date());
			}
		}
	}

	public void deregisterNode(String[] owningProcess) {
		Object dataflowObject = dataflowObjects.remove(getOwningProcessId(owningProcess));
		if (dataflowObject instanceof Dataflow) {
			Dataflow dataflow = (Dataflow) dataflowObject;
			workflowReports.get(dataflow).setCompletedDate(new Date());
		} else if (dataflowObject instanceof Processor) {
			Processor dataflowProcessor = (Processor) dataflowObject;
			if (processorInvocations.get(dataflowProcessor).decrementAndGet() == 0) {
				LocalProcessorReport processorReport = processorReports.get(dataflowProcessor);
				processorReport.saveProperties();
				processorReport.setCompletedDate(new Date());
			}
		} else if (dataflowObject instanceof Activity) {
			Activity<?> activity = (Activity<?>) dataflowObject;
			if (activityInvocations.get(activity).decrementAndGet() == 0) {
				ActivityReport activityReport = activityReports.get(activity);
				activityReport.setCompletedDate(new Date());
			}
		}
	}

	public void addPropertiesToNode(String[] owningProcess,
			Set<MonitorableProperty<?>> newProperties) {
		Object dataflowObject = dataflowObjects.get(owningProcess);
		if (dataflowObject instanceof Processor) {
			Processor dataflowProcessor = (Processor) dataflowObject;
			LocalProcessorReport processorReport = processorReports.get(dataflowProcessor);
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
	private static String getOwningProcessId(String[] owningProcess) {
		StringBuilder sb = new StringBuilder();
		for (String string : owningProcess) {
			if (sb.length() > 0) {
				sb.append(':');
			}
			sb.append(string);
		}
		return sb.toString();
	}

}
