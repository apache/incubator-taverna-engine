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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.workflowmodel.Configurable;
import org.apache.taverna.workflowmodel.ConfigurationException;
import org.apache.taverna.workflowmodel.EditException;

import org.apache.log4j.Logger;

/**
 * An Edit that is responsible for configuring a {@link Configurable} with a
 * given configuration bean.
 * 
 * @author Stuart Owen
 * @author Stian Soiland-Reyes
 * @author Donal Fellows
 */
class ConfigureEdit<T> extends EditSupport<Configurable<T>> {
	private static Logger logger = Logger.getLogger(ConfigureEdit.class);

	private final Configurable<T> configurable;
	private final Class<? extends Configurable<T>> configurableType;
	private final T configurationBean;

	ConfigureEdit(Class<? extends Configurable<T>> subjectType,
			Configurable<T> configurable, T configurationBean) {
		if (configurable == null)
			throw new RuntimeException(
					"Cannot construct an edit with null subject");
		this.configurableType = subjectType;
		this.configurable = configurable;
		this.configurationBean = configurationBean;
		if (!configurableType.isInstance(configurable))
			throw new RuntimeException(
					"Edit cannot be applied to an object which isn't an instance of "
							+ configurableType);
	}

	@Override
	public final Configurable<T> applyEdit() throws EditException {
		try {
			// FIXME: Should clone bean on configuration to prevent caller from
			// modifying bean afterwards
			synchronized (configurable) {
				configurable.configure(configurationBean);
			}
			return configurable;
		} catch (ConfigurationException e) {
			logger.error("Error configuring :"
					+ configurable.getClass().getSimpleName(), e);
			throw new EditException(e);
		}
	}

	@Override
	public final Configurable<T> getSubject() {
		return configurable;
	}
}
