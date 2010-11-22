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
package uk.org.taverna.platform.activity;

import java.net.URI;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityConfigurationDefinition;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

/**
 * Service for discovering available activities and the properties required to configure the
 * acitivities.
 * 
 * @author David Withers
 */
public interface ActivityService {

	/**
	 * Returns a list URI's that identify available activities.
	 * 
	 * @return
	 */
	public List<URI> getActivityURIs();

	/**
	 * Returns a definition of the configuration required by an activity.
	 * 
	 * @param uri
	 *            a URI that identifies an activity
	 * @return a definition of the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 */
	public ActivityConfigurationDefinition getActivityConfigurationDefinition(URI uri)
			throws ActivityNotFoundException;

	public Activity<?> createActivity(URI uri, Map<URI, Object> configurationProperties,
			List<ActivityInputPortDefinitionBean> inputPortDefinitions,
			List<ActivityOutputPortDefinitionBean> outputPortDefinitions)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Sets the list of available <code>ActivityFactory</code>s.
	 * 
	 * @param activityFactories
	 *            the list of available <code>ActivityFactory</code>s
	 */
	public void setActivityFactories(List<ActivityFactory<? extends Activity<?>>> activityFactories);

}
