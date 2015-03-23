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

import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.AppliesTo;
import org.apache.taverna.workflowmodel.Dataflow;

/**
 * An IdentificationAssertion is used to hold previous identifications of an
 * object.
 * 
 * @author alanrw
 * 
 */
@AppliesTo(targetObjectType = { Dataflow.class }, many = false, pruned = false)
public class IdentificationAssertion implements AnnotationBeanSPI {

	private String identification;

	/**
	 * @return The identification. This will be a previous identifier of the
	 *         annotated object.
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * @param identification
	 *            A previous identified of the annotated object.
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

}
