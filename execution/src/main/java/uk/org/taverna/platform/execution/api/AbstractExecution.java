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
package uk.org.taverna.platform.execution.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Abstract implementation of an {@link Execution}.
 * 
 * @author David Withers
 */
public abstract class AbstractExecution implements Execution {

	private final String ID;
	private final WorkflowBundle workflowBundle;
	private final Workflow workflow;
	private final Profile profile;
	private final Map<String, T2Reference> inputs;
	private final ReferenceService referenceService;
	private final WorkflowReport workflowReport;
	private final Scufl2Tools scufl2Tools = new Scufl2Tools();
	private final URITools uriTools = new URITools();

	public AbstractExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Map<String, T2Reference> inputs, ReferenceService referenceService) {
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
		this.referenceService = referenceService;
		ID = UUID.randomUUID().toString();
		workflowReport = generateWorkflowReport(workflow);
	}

	protected abstract WorkflowReport createWorkflowReport(Workflow workflow);
	
	public WorkflowReport generateWorkflowReport(Workflow workflow) {
		WorkflowReport workflowReport = createWorkflowReport(workflow);
		Map<Processor, ProcessorReport> processorReports = new HashMap<Processor, ProcessorReport>();
		for (Processor processor : workflow.getProcessors()) {
			ProcessorReport processorReport = workflowReport.createProcessorReport(processor, workflowReport);
			processorReports.put(processor, processorReport);
			workflowReport.addProcessorReport(processorReport);
		}
		Set<ProcessorBinding> processorBindings = profile.getProcessorBindings();
		for (ProcessorBinding processorBinding : processorBindings) {
			Activity boundActivity = processorBinding.getBoundActivity();
			Processor boundProcessor = processorBinding.getBoundProcessor();
			ProcessorReport processorReport = processorReports.get(boundProcessor);
			ActivityReport activityReport = workflowReport.createActivityReport(boundActivity, processorReport);
			URI activityType = boundActivity.getConfigurableType();
			if (activityType.equals(URI.create("http://ns.taverna.org.uk/2010/activity/dataflow"))) {
				Configuration configuration = scufl2Tools.configurationFor(boundActivity, profile);
				URI dataflowURI = configuration.getPropertyResource().getResourceURI();
				Workflow subWorkflow = (Workflow) uriTools.resolveUri(dataflowURI, workflowBundle);
				activityReport.setNestedWorkflowReport(generateWorkflowReport(subWorkflow));
			}
			processorReport.addActivityReport(activityReport);
		}
		return workflowReport;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}

	@Override
	public Profile getProfile() {
		return profile;
	}

	@Override
	public Map<String, T2Reference> getInputs() {
		return inputs;
	}

	@Override
	public ReferenceService getReferenceService() {
		return referenceService;
	}

	@Override
	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

	@Override
	public abstract void start();

	@Override
	public abstract void pause();

	@Override
	public abstract void resume();

	@Override
	public abstract void cancel();

}