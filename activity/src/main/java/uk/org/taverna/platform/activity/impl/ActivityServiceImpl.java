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
package uk.org.taverna.platform.activity.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityConfigurationDefinition;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import uk.org.taverna.platform.activity.ActivityNotFoundException;
import uk.org.taverna.platform.activity.ActivityService;

public class ActivityServiceImpl implements ActivityService {

	private List<ActivityFactory<? extends Activity<?>>> activityFactories;

	@Override
	public List<URI> getActivityURIs() {
		List<URI> activityURIs = new ArrayList<URI>();
		for (ActivityFactory<? extends Activity<?>> activityFactory : activityFactories) {
			activityURIs.add(activityFactory.getActivityURI());
		}
		return activityURIs;
	}

	@Override
	public ActivityConfigurationDefinition getActivityConfigurationDefinition(URI uri)
			throws ActivityNotFoundException {
		return getActivityFactory(uri).getActivityConfigurationDefinition();
	}

	@Override
	public Activity<?> createActivity(URI uri, Map<URI, Object> configurationProperties,
			List<ActivityInputPortDefinitionBean> inputs,
			List<ActivityOutputPortDefinitionBean> outputs) throws ActivityNotFoundException,
			ActivityConfigurationException {
		return getActivityFactory(uri).createActivity(configurationProperties, inputs, outputs);
	}

	@Override
	public void setActivityFactories(List<ActivityFactory<? extends Activity<?>>> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private ActivityFactory<? extends Activity<?>> getActivityFactory(URI uri)
			throws ActivityNotFoundException {
		for (ActivityFactory<? extends Activity<?>> activityFactory : activityFactories) {
			if (activityFactory.getActivityURI().equals(uri)) {
				return activityFactory;
			}
		}
		throw new ActivityNotFoundException("Could not find an activity for " + uri);
	}
}
