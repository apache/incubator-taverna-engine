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

import net.sf.taverna.t2.commandline.CommandLineTool;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class CommandLineToolIT extends PlatformIT {

	
	private RunService runService;
	private WorkflowBundleIO workflowBundleIO;

	protected void setup() throws InvalidSyntaxException {
		super.setup();
		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}
		
		if (workflowBundleIO == null) {
			ServiceReference workflowBundleIOServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.scufl2.api.io.WorkflowBundleIO");
			workflowBundleIO = (WorkflowBundleIO) bundleContext.getService(workflowBundleIOServiceReference);
		}
	}
	
	public void testCommandLineTool() throws InvalidSyntaxException{
		
		setup();
		
		CommandLineTool cmdTool = new CommandLineTool();
		cmdTool.setRunService(runService);
		cmdTool.setCredentialManager(credentialManager);
		cmdTool.setWorkflowBundleIO(workflowBundleIO);
		cmdTool.run(new String[]{"-outputdoc", "/tmp/taverna3/baclava-output.xml", "/tmp/taverna3/simple-wf-no-inputs-one-output.t2flow"});
	}

	
}
