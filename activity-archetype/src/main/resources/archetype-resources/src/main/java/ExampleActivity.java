/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package ${packageName};

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.cloudone.datamanager.DataFacade;
import net.sf.taverna.t2.cloudone.datamanager.DataManagerException;
import net.sf.taverna.t2.cloudone.datamanager.NotFoundException;
import net.sf.taverna.t2.cloudone.identifier.EntityIdentifier;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;

/**
 * An Activity providing ${artifactId} functionality.
 * 
 */
public class ${artifactId}Activity extends
		AbstractAsynchronousActivity<${artifactId}ActivityConfigurationBean> {

	private ${artifactId}ActivityConfigurationBean configurationBean;

	public ${artifactId}Activity() {
	}

	@Override
	public void configure(${artifactId}ActivityConfigurationBean configurationBean)
			throws ActivityConfigurationException {
		this.configurationBean = configurationBean;
		configurePorts(configurationBean);
	}

	@Override
	public ${artifactId}ActivityConfigurationBean getConfiguration() {
		return configurationBean;
	}
	

	@Override
	public void executeAsynch(final Map<String, EntityIdentifier> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {

			public void run() {
				DataFacade dataFacade = new DataFacade(callback.getContext().getDataManager());

				Map<String, EntityIdentifier> outputData = new HashMap<String, EntityIdentifier>();

				try {
					//resolve inputs
					Object exampleInput = dataFacade.resolve(data.get("example_input"), String.class);
					
					//run the activity
					String exampleOutput = exampleInput + "_example";
					
					//register outputs
					outputData.put("example_output", dataFacade.register(exampleOutput));

					//send result to the callback
					callback.receiveResult(outputData, new int[0]);
				} catch (DataManagerException e) {
					callback.fail("Error accessing input/output data", e);
				} catch (NotFoundException e) {
					callback.fail("Error accessing input/output data", e);
				}
			}
			
		});

	}
}
