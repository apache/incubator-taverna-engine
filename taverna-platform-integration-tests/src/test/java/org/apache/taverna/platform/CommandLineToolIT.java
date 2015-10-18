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
