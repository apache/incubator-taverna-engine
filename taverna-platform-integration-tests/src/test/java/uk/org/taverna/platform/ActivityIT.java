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
package uk.org.taverna.platform;

import java.net.URI;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.capability.activity.ActivityConfigurationException;
import uk.org.taverna.platform.capability.activity.ActivityNotFoundException;
import uk.org.taverna.platform.capability.activity.ActivityService;
import org.apache.taverna.scufl2.api.configurations.ConfigurationDefinition;

public class ActivityIT extends PlatformIT {

	public void testGetActivityURIs() {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("uk.org.taverna.platform.activity.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);
		List<URI> activityURIs = activityService.getActivityURIs();
		System.out.println("================= Available Activities ===================");
		for (URI uri : activityURIs) {
			System.out.println(uri);
		}
		System.out.println("==========================================================");
		System.out.println("");
	}

	public void testCreateActivity() throws ActivityNotFoundException, ActivityConfigurationException {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("uk.org.taverna.platform.activity.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);
		List<URI> activityURIs = activityService.getActivityURIs();
		for (URI uri : activityURIs) {
			System.out.println("Creating activity " + uri);
			Activity<?> activity = activityService.createActivity(uri, null);
		}
	}

	public void testGetActivityConfigurationDefinition() throws Exception {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("uk.org.taverna.platform.activity.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);

		List<URI> activityURIs = activityService.getActivityURIs();
		System.out.println("============ Activity Configuration Definitions ==========");
		for (URI uri : activityURIs) {
			ConfigurationDefinition configurationDefinition = activityService.getActivityConfigurationDefinition(uri);
			System.out.println(configurationDefinition);
		}
		System.out.println("==========================================================");
	}

}
