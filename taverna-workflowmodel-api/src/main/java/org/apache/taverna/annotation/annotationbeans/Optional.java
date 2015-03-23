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
import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.workflowmodel.InputPort;

/**
 * A declaration that the bound input port is optional, if this annotation is
 * refuted then the interpretation should be that the input port is required.
 * 
 * @author Tom Oinn
 * @author Alan Williams
 */
@AppliesTo(targetObjectType = { InputPort.class }, many = false)
public class Optional implements AnnotationBeanSPI {

	/**
	 * Default constructor as mandated by java bean specification
	 */
	public Optional() {
		//
	}

}
