/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.platform;

import java.net.URI;
import java.util.List;

import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.osgi.framework.ServiceReference;


public class ActivityIT extends PlatformIT {

	public void testGetActivityURIs() {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("org.apache.taverna.platform.capability.api.ActivityService");
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
		ServiceReference activityServiceReference = bundleContext.getServiceReference("org.apache.taverna.platform.capability.api.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);
		List<URI> activityURIs = activityService.getActivityURIs();
		for (URI uri : activityURIs) {
			System.out.println("Creating activity " + uri);
			Activity<?> activity = activityService.createActivity(uri, null);
		}
	}

	public void testGetActivityConfigurationDefinition() throws Exception {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("org.apache.taverna.platform.capability.api.ActivityService");
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
