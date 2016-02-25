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

import org.osgi.framework.ServiceReference;

import org.apache.taverna.platform.run.api.RunService;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;

public class CommandLineToolIT extends PlatformIT {

	private RunService runService;
	private WorkflowBundleIO workflowBundleIO;
	private WorkflowBundleReader workflowReader;

	protected void setup() throws Exception {
		super.setup();
		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("org.apache.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}

		if (workflowReader == null) {
			ServiceReference workflowBundleReaderServiceReference = bundleContext
					.getServiceReferences("org.apache.taverna.scufl2.api.io.WorkflowBundleReader",
							"(mediaType=application/vnd.taverna.t2flow+xml)")[0];
			workflowBundleReader = (WorkflowBundleReader) bundleContext
					.getService(workflowBundleReaderServiceReference);
		}

		if (workflowBundleIO == null) {
			ServiceReference workflowBundleIOServiceReference = bundleContext
					.getServiceReference("org.apache.taverna.scufl2.api.io.WorkflowBundleIO");
			workflowBundleIO = (WorkflowBundleIO) bundleContext
					.getService(workflowBundleIOServiceReference);
		}
	}

	public void testCommandLineTool() throws Exception {

		setup();

		CommandLineTool cmdTool = new CommandLineTool();
		cmdTool.setRunService(runService);
		cmdTool.setCredentialManager(credentialManager);
		cmdTool.setWorkflowBundleIO(workflowBundleIO);
		cmdTool.setCommandLineArgumentsService(new CommandLineArguments() {
			public String[] getCommandLineArguments() {
				return new String[] { "-outputdoc", "/tmp/taverna3/baclava-output1.xml",
						"/Users/alex/Desktop/t2flows/simple-wf-no-inputs-one-output.t2flow" };
			}

		});
		cmdTool.run();

		cmdTool.setCommandLineArgumentsService(new CommandLineArguments() {
			public String[] getCommandLineArguments() {
				return new String[] { "-inputvalue", "in", "Taverna 3 Platform rules",
						"-outputdoc", "/tmp/taverna3/baclava-output2.xml",
						"/Users/alex/Desktop/t2flows/simple-wf-one-input-one-output.t2flow" };
			}

		});
		cmdTool.run();

	}

}
