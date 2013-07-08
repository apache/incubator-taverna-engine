/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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

import java.net.URI;
import java.util.Set;

import uk.org.taverna.platform.capability.api.ActivityConfigurationException;
import uk.org.taverna.platform.capability.api.ActivityNotFoundException;
import uk.org.taverna.platform.capability.api.ActivityService;
import uk.org.taverna.platform.capability.api.DispatchLayerConfigurationException;
import uk.org.taverna.platform.capability.api.DispatchLayerNotFoundException;
import uk.org.taverna.platform.capability.api.DispatchLayerService;
import uk.org.taverna.platform.execution.api.AbstractExecutionEnvironment;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Execution Environment for a local Taverna Dataflow Engine
 *
 * @author David Withers
 */
public class LocalExecutionEnvironment extends AbstractExecutionEnvironment {

	private final ActivityService activityService;
	private final DispatchLayerService dispatchLayerService;

	public LocalExecutionEnvironment(LocalExecutionService localExecutionService,
			ActivityService activityService, DispatchLayerService dispatchLayerService) {
		super(LocalExecutionEnvironment.class.getName(), "Taverna Local Execution Environment",
				"Execution Environment for a local Taverna Dataflow Engine", localExecutionService);
		this.activityService = activityService;
		this.dispatchLayerService = dispatchLayerService;
	}

	@Override
	public Set<URI> getActivityTypes() {
		return activityService.getActivityTypes();
	}

	@Override
	public boolean activityExists(URI uri) {
		return activityService.activityExists(uri);
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException {
		return activityService.getActivityConfigurationSchema(uri);
	}

	@Override
	public Set<URI> getDispatchLayerTypes() {
		return dispatchLayerService.getDispatchLayerTypes();
	}

	@Override
	public boolean dispatchLayerExists(URI uri) {
		return dispatchLayerService.dispatchLayerExists(uri);
	}

	@Override
	public JsonNode getDispatchLayerConfigurationSchema(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		return dispatchLayerService.getDispatchLayerConfigurationSchema(uri);
	}

}
