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
package uk.org.taverna.platform.capability.activity.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;
import uk.org.taverna.platform.capability.api.ActivityConfigurationException;
import uk.org.taverna.platform.capability.api.ActivityNotFoundException;
import uk.org.taverna.platform.capability.api.ActivityService;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

public class ActivityServiceImpl implements ActivityService {

	private List<ActivityFactory> activityFactories;

	@Override
	public Set<URI> getActivityTypes() {
		Set<URI> activityTypes = new HashSet<URI>();
		for (ActivityFactory activityFactory : activityFactories) {
			activityTypes.add(activityFactory.getActivityType());
		}
		return activityTypes;
	}

	@Override
	public boolean activityExists(URI uri) {
		for (ActivityFactory activityFactory : activityFactories) {
			if (activityFactory.getActivityType().equals(uri)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws ActivityNotFoundException {
		ActivityFactory factory = getActivityFactory(activityType);
		return factory.getActivityConfigurationSchema();
	}

	@Override
	public Activity<?> createActivity(URI activityType, JsonNode configuration)
			throws ActivityNotFoundException, ActivityConfigurationException {
		ActivityFactory factory = getActivityFactory(activityType);
		Activity<JsonNode> activity = (Activity<JsonNode>) factory.createActivity();
		if (configuration != null) {
			try {
				activity.configure(configuration);
			} catch (net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException e) {
				throw new ActivityConfigurationException(e);
			}
		}
		return activity;
	}

	@Override
	public Set<InputActivityPort> getActivityInputPorts(URI activityType, JsonNode configuration)
			throws ActivityNotFoundException, ActivityConfigurationException {
		Set<InputActivityPort> inputPorts = new HashSet<>();
		try {
			for (ActivityInputPort port : getActivityFactory(activityType).getInputPorts(configuration)) {
				InputActivityPort inputActivityPort = new InputActivityPort();
				inputActivityPort.setName(port.getName());
				inputActivityPort.setDepth(port.getDepth());
				inputPorts.add(inputActivityPort);
			}
		} catch (net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}
		return inputPorts;
	}

	@Override
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType, JsonNode configuration)
			throws ActivityNotFoundException, ActivityConfigurationException {
		Set<OutputActivityPort> outputPorts = new HashSet<>();
		try {
			for (ActivityOutputPort port : getActivityFactory(activityType).getOutputPorts(
					configuration)) {
				OutputActivityPort outputActivityPort = new OutputActivityPort();
				outputActivityPort.setName(port.getName());
				outputActivityPort.setDepth(port.getDepth());
				outputActivityPort.setGranularDepth(port.getGranularDepth());
				outputPorts.add(outputActivityPort);
			}
		} catch (net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}
		return outputPorts;
	}

	/**
	 * Sets the list of available <code>ActivityFactory</code>s.
	 * In a production environment this should be set by Spring DM.
	 *
	 * @param activityFactories
	 *            the list of available <code>ActivityFactory</code>s
	 */
	public void setActivityFactories(List<ActivityFactory> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private ActivityFactory getActivityFactory(URI activityType)
			throws ActivityNotFoundException {
		for (ActivityFactory activityFactory : activityFactories) {
			if (activityFactory.getActivityType().equals(activityType)) {
				return activityFactory;
			}
		}
		throw new ActivityNotFoundException("Could not find an activity for " + activityType);
	}

}
