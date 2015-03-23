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

package org.apache.taverna.workflowmodel.processor.activity;

import org.apache.taverna.workflowmodel.ConfigurationException;

/**
 * Thrown when attempting to configure an Activity instance with an invalid
 * configuration. Causes may include actual configuration errors, unavailable
 * activities etc.
 * 
 * @author Tom Oinn
 */
public class ActivityConfigurationException extends ConfigurationException {
	private static final long serialVersionUID = 6940385954331153900L;

	/**
	 * @param msg
	 *            a message describing the reason for the exception.
	 */
	public ActivityConfigurationException(String msg) {
		super(msg);
	}

	/**
	 * @param cause
	 *            a previous exception that caused this
	 *            ActivityConfigurationException to be thrown.
	 */
	public ActivityConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param msg
	 *            a message describing the reason for the exception.
	 * @param cause
	 *            a previous exception that caused this
	 *            ActivityConfigurationException to be thrown.
	 */
	public ActivityConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
