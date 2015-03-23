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
import org.apache.taverna.workflowmodel.Condition;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.Port;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;

/**
 * A SemanticAssertion holds a String which contains RDF about an Object
 * @author alanrw
 *
 */
@AppliesTo(targetObjectType = { Dataflow.class, Processor.class, Port.class, Activity.class, Datalink.class, Merge.class, Condition.class, DispatchLayer.class }, many = false)
public class SemanticAnnotation implements AnnotationBeanSPI {
	
	private String mimeType = "text/rdf+n3";
	
	private String content = "";

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

}
