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

package org.apache.taverna.workflowmodel.processor.activity.impl;

import java.util.Set;

import org.apache.taverna.annotation.AnnotationChain;
import org.apache.taverna.workflowmodel.AbstractOutputPort;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

import org.apache.log4j.Logger;

/**
 * An output port on an Activity instance, used as a bean to hold port name,
 * depth and granular depth properties.
 * 
 * @author Tom Oinn
 */
public class ActivityOutputPortImpl extends AbstractOutputPort implements ActivityOutputPort {
	private static Logger logger = Logger.getLogger(ActivityOutputPortImpl.class);

	/**
	 * Constructs an Activity output port instance with the provided name,depth
	 * and granular depth.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 */
	public ActivityOutputPortImpl(String portName, int portDepth,
			int granularDepth) {
		super(portName, portDepth, granularDepth);
	}

	/**
	 * Constructs an Activity input port with the provided name, depth and
	 * granularDepth together with a list of predetermined annotations.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 * @param annotations
	 */
	public ActivityOutputPortImpl(String portName, int portDepth,
			int granularDepth, Set<AnnotationChain> annotations) {
		this(portName, portDepth, granularDepth);
		for (AnnotationChain newAnnotation : annotations)
			try {
				getAddAnnotationEdit(newAnnotation).doEdit();
			} catch (EditException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
	}

}
