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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.provenance.ProvenanceConnectorFactory;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.workflowmodel.Edits;
import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.platform.data.Data;
import uk.org.taverna.platform.data.DataService;
import uk.org.taverna.platform.database.DatabaseConfiguration;
import uk.org.taverna.platform.dispatch.DispatchLayerService;
import uk.org.taverna.platform.execution.api.AbstractExecutionService;
import uk.org.taverna.platform.execution.api.Execution;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Service for executing Taverna workflows on a local Taverna Dataflow Engine.
 *
 * @author David Withers
 */
public class LocalExecutionService extends AbstractExecutionService {

	private Edits edits;

	private ActivityService activityService;

	private DispatchLayerService dispatchLayerService;

	private DataService dataService;

	private ReferenceService referenceService;

	private DatabaseConfiguration databaseConfiguration;

	private Set<ProvenanceConnectorFactory> provenanceConnectorFactories;

	/**
	 * Constructs an execution service that executes workflows using the T2 dataflow engine.
	 */
	public LocalExecutionService() {
		super(LocalExecutionService.class.getName(), "Taverna Local Execution Service",
				"Execution Service for executing Taverna workflows using a local Taverna Dataflow Engine");
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvivonments() {
		Set<ExecutionEnvironment> executionEnvironments = new HashSet<ExecutionEnvironment>();
		executionEnvironments.add(new LocalExecutionEnvironment(this, activityService,
				dispatchLayerService));
		return executionEnvironments;
	}

	@Override
	protected Execution createExecutionImpl(WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Map<String, Data> inputs) throws InvalidWorkflowException {
		return new LocalExecution(workflowBundle, workflow, profile, inputs, dataService,
				referenceService, edits, activityService, dispatchLayerService, databaseConfiguration,
				provenanceConnectorFactories);
	}

	/**
	 * Sets the Edits Service for creating Taverna Dataflows.
	 *
	 * @param edits
	 *            the Edits Service for creating Taverna Dataflows
	 */
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	/**
	 * Sets the service for creating activities.
	 *
	 * @param activityService
	 *            the service for creating activities
	 */
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	/**
	 * Sets the service for creating dispatch layers.
	 *
	 * @param dispatchLayerService
	 *            the service for creating dispatch layers
	 */
	public void setDispatchLayerService(DispatchLayerService dispatchLayerService) {
		this.dispatchLayerService = dispatchLayerService;
	}

	/**
	 * Sets the data service.
	 *
	 * @param dataService
	 *            the data service
	 */
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	/**
	 * Sets the reference service.
	 *
	 * @param referenceService
	 *            the reference service
	 */
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	/**
	 * Sets the ProvenanceConnector factories.
	 *
	 * @param factories
	 *            the ProvenanceConnector factories
	 */
	public void setProvenanceConnectorFactories(
			Set<ProvenanceConnectorFactory> provenanceConnectorFactories) {
		this.provenanceConnectorFactories = provenanceConnectorFactories;
	}

	/**
	 * Sets the databaseConfiguration.
	 *
	 * @param databaseConfiguration the new value of databaseConfiguration
	 */
	public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

}
