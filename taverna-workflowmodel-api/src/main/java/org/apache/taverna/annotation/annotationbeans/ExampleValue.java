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

package org.apache.taverna.annotation.annotationbeans;

import org.apache.taverna.annotation.AppliesTo;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;

/**
 * A String containing an example or a description of an example
 * 
 * @author Alan R Williams
 * 
 */
@AppliesTo(targetObjectType = { DataflowInputPort.class , DataflowOutputPort.class }, many = false)
public class ExampleValue extends AbstractTextualValueAssertion {

	/**
	 * Default constructor as mandated by java bean specification
	 */
	public ExampleValue() {
		//
	}

}
