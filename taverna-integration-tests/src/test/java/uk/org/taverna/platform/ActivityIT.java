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

import java.io.File;
import java.net.URI;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import uk.org.taverna.osgi.starter.TavernaStarter;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.run.api.RunService;

public class ActivityIT extends PlatformIT {

	private static TavernaStarter tavernaStarter;
	private static BundleContext bundleContext;
	private static RunService runService;

	@BeforeClass
	public static void setup() throws Exception {
		tavernaStarter = new TavernaStarter(new File("/tmp"));
		tavernaStarter.start();
		runService = tavernaStarter.getRunService();
	}

	@AfterClass
	public static void shutdown() throws Exception {
		tavernaStarter.stop();
	}

	@Test
	public void testGetActivityURIs() {
		System.out.println("================= Available Activities ===================");
		for (ExecutionEnvironment executionEnvironment : runService.getExecutionEnvironments()) {
			for (URI uri : executionEnvironment.getActivityTypes()) {
				System.out.println(uri);
			}
		}
		System.out.println("==========================================================");
		System.out.println("");
	}

	public void testCreateActivity() throws Exception {
//		for (URI uri : activityService.getActivityTypes()) {
//			System.out.println("Creating activity " + uri);
//			Activity<?> activity = activityService.createActivity(uri, null);
//		}
	}

	@Test
	public void testGetActivityConfigurationDefinition() throws Exception {
		System.out.println("============ Activity Configuration Definitions ==========");
		for (ExecutionEnvironment executionEnvironment : runService.getExecutionEnvironments()) {
			for (URI uri : executionEnvironment.getActivityTypes()) {
				System.out.println(executionEnvironment.getActivityConfigurationSchema(uri));
			}
		}
		System.out.println("==========================================================");
	}

}
