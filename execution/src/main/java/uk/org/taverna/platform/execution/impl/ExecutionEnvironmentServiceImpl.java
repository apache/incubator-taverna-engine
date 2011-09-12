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
package uk.org.taverna.platform.execution.impl;

import java.util.HashSet;
import java.util.Set;

import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.ExecutionEnvironmentService;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 *
 *
 * @author David Withers
 */
public class ExecutionEnvironmentServiceImpl implements ExecutionEnvironmentService {

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	private Set<ExecutionService> executionServices;

    @Override
    public Set<ExecutionEnvironment> getExecutionEnvironments() {
        Set<ExecutionEnvironment> executionEnvironments = new HashSet<ExecutionEnvironment>();
        for (ExecutionService executionService : executionServices) {
        	executionEnvironments.addAll(executionService.getExecutionEnvivonments());
		}
        return executionEnvironments;
    }

    @Override
    public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile) {
        Set<ExecutionEnvironment> validExecutionEnvironments = new HashSet<ExecutionEnvironment>();
        for (ExecutionEnvironment executionEnvironment : getExecutionEnvironments()) {
			if (isValidExecutionEnvironment(executionEnvironment, profile)) {
				validExecutionEnvironments.add(executionEnvironment);
			}
		}
        return validExecutionEnvironments;
    }

	public void setExecutionServices(Set<ExecutionService> executionServices) {
        this.executionServices = executionServices;
    }

    /**
	 * @param executionEnvironment
	 * @param profile
	 * @return
	 */
	private boolean isValidExecutionEnvironment(ExecutionEnvironment executionEnvironment,
			Profile profile) {
		NamedSet<ProcessorBinding> processorBindings = profile.getProcessorBindings();
		for (ProcessorBinding processorBinding : processorBindings) {
			Activity activity = processorBinding.getBoundActivity();
			if (!executionEnvironment.activityExists(activity.getConfigurableType())) {
				return false;
			}
			Configuration configuration = scufl2Tools.configurationFor(activity, profile);
			Processor processor = processorBinding.getBoundProcessor();
			for (DispatchStackLayer dispatchStackLayer : processor.getDispatchStack()) {
				if (!executionEnvironment.dispatchLayerExists(dispatchStackLayer.getConfigurableType())) {
					return false;
				}
			}
		}
		return true;
	}

}
