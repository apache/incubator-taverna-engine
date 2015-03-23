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

package org.apache.taverna.workflowmodel;

import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;

/**
 * Interface for workflow items that can be configured from a bean.
 * 
 * @param <ConfigurationType>
 *            the ConfigurationType associated with the workflow item. This is
 *            an arbitrary java class that provides details on how the item is
 *            configured. To allow successful serialisation it's recommended to
 *            keep this configuration as a simple Java bean.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * @see ActivityConfigurationException
 */
public interface Configurable<ConfigurationType> extends WorkflowItem {
	/**
	 * Each item stores configuration within a bean of type ConfigurationType,
	 * this method returns the configuration. This is used by the automatic
	 * serialisation framework to store the item definition in the workflow XML.
	 */
	ConfigurationType getConfiguration();

	/**
	 * When the item is built from the workflow definition XML the object is
	 * first constructed with a default constructor then this method is called,
	 * passing in the configuration bean returned by getConfiguration().
	 * 
	 * @throws ConfigurationException
	 *             if a problem occurs when configuring the item
	 */
	void configure(ConfigurationType conf) throws ConfigurationException;
}