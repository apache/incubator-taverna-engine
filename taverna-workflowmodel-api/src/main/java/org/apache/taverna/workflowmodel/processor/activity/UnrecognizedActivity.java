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

import org.jdom2.Element;

/**
 * An unrecognized activity is an activity that was not recognized when the
 * workflow was opened.
 * 
 */
public final class UnrecognizedActivity extends NonExecutableActivity<Element> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/unrecognized";

	private Element conf;

	/**
	 * It is not possible to create a "naked" UnrecognizedActivity.
	 */
	private UnrecognizedActivity() {
		super();
	}

	public UnrecognizedActivity(Element config)
			throws ActivityConfigurationException {
		this();
		this.configure(config);
	}

	@Override
	public void configure(Element conf) throws ActivityConfigurationException {
		this.conf = conf;
	}

	@Override
	public Element getConfiguration() {
		return conf;
	}
}
