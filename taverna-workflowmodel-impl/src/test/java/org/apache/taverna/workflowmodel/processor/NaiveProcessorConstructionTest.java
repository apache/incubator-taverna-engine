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

package org.apache.taverna.workflowmodel.processor;

import static org.apache.taverna.workflowmodel.processor.Tools.buildFromActivity;

import java.io.IOException;

import junit.framework.TestCase;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.impl.EditsImpl;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;

import org.jdom2.JDOMException;

/**
 * Tests the processor factory along with activity serialisation logic
 * 
 * @author Tom Oinn
 */
public class NaiveProcessorConstructionTest extends TestCase {
	public void testProcessorFactory() throws EditException, JDOMException,
			IOException, ActivityConfigurationException {
		AsynchEchoActivity activity = new AsynchEchoActivity();
		activity.setEdits(new EditsImpl());
		activity.configure(new EchoConfig("blah"));
		buildFromActivity(activity);
	}
}
