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
import org.apache.taverna.workflowmodel.Port;

/**
 * A single MIME type, intended to be used to annotate an input or output port
 * within the workflow to denote the type within that system of data produced or
 * consumed by the port.
 * 
 * @author Tom Oinn
 * 
 */
@AppliesTo(targetObjectType = { Port.class })
public class MimeType extends AbstractTextualValueAssertion {

	/**
	 * Default constructor as mandated by java bean specification
	 */
	public MimeType() {
		super();
	}

	/**
	 * Return the MIME type as a string, mime types look like 'part/part'. We
	 * may want to consider whether it's possible to make this a genuine
	 * enumeration driven off a canonical list of MIME types or whether it's
	 * best kept as the current (free) string. The advantage of an enumerated
	 * type is that we could attach description to the MIME types which would
	 * help with the UI construction but maybe this isn't the place to put it
	 * (should this link be in the UI layer? probably)
	 * 
	 * @return the MIME type as a string.
	 */
	@Override
	public String getText() {
		return super.getText();
	}

}
