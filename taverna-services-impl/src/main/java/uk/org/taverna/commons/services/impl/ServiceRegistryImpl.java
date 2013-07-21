/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package uk.org.taverna.commons.services.impl;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.commons.services.ActivityTypeNotFoundException;
import uk.org.taverna.commons.services.InvalidConfigurationException;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.platform.capability.api.ActivityConfigurationException;
import uk.org.taverna.platform.capability.api.ActivityNotFoundException;
import uk.org.taverna.platform.capability.api.ActivityService;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Simple implementation of a ServiceRegistry that discovers available services from the
 * ActivityService.
 *
 * @author David Withers
 */
public class ServiceRegistryImpl implements ServiceRegistry {

	private ActivityService activityService;

	@Override
	public Set<URI> getActivityTypes() {
		return activityService.getActivityTypes();
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityConfigurationSchema(activityType);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	@Override
	public Set<InputActivityPort> getActivityInputPorts(URI activityType, JsonNode configuration)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityInputPorts(activityType, configuration);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	@Override
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType, JsonNode configuration)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityOutputPorts(activityType, configuration);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

}
