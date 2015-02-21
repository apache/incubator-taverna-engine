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
package uk.org.taverna.platform;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public class RunParallelIT extends PlatformIT {

	private RunService runService;

	private ExecutionEnvironment executionEnvironment;

	protected void setup() throws Exception {
		super.setup();
		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}
		if (executionEnvironment == null) {
			executionEnvironment = runService.getExecutionEnvironments().iterator().next();
		}
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));
	}

	public void testRun() throws Exception {
		setup();

		String[] workflows = { "biomart", "rest", "secure-basic-authentication",
				"spreadsheetimport", "stringconstant", "wsdl", "wsdl-secure", "xpath" };
		Set<String> runIDs= new HashSet<String>();

		for (int i = 0; i < workflows.length; i++) {
			int runs = Math.max(1, (int) Math.ceil(Math.random() * 10));
			System.out.println("Creating " + runs + " runs for workflow " + workflows[i]);
			for (int j = 0; j < runs; j++) {
				WorkflowBundle workflowBundle = loadWorkflow("/t2flow/" + workflows[i] + ".t2flow");
				String run = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
				runService.getWorkflowReport(run).getSubject().setName(workflows[i] + j);
				runIDs.add(run);
			}
		}

		for (String runId : runIDs) {
			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println("Starting " + report.getSubject().getName());
			runService.start(runId);
		}

		for (String runId : runIDs) {
			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println("Waiting for " + report.getSubject().getName() + " to complete");
			assertTrue(waitForState(report, State.COMPLETED, false));
		}

	}

}
