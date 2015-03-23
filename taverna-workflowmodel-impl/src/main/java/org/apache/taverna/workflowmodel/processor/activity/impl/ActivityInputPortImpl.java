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

import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.AbstractPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;

/**
 * An input port on an Activity instance. Simply used as a bean to hold port
 * name and depth properties.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 */
public class ActivityInputPortImpl extends AbstractPort implements
		ActivityInputPort {
	private Class<?> translatedElementClass;
	private List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes;
	boolean allowsLiteralValues;

	/**
	 * Constructs an Activity input port instance with the provided name and
	 * depth.
	 * 
	 * @param portName
	 * @param portDepth
	 */
	public ActivityInputPortImpl(String portName, int portDepth) {
		super(portName, portDepth);
	}

	/**
	 * Constructs an Activity input port with the provided name and depth,
	 * together with a list of predetermined annotations.
	 * 
	 * @param portName
	 * @param portDepth
	 */
	public ActivityInputPortImpl(
			String portName,
			int portDepth,
			boolean allowsLiteralValues,
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes,
			Class<?> translatedElementClass) {
		this(portName, portDepth);
		this.allowsLiteralValues = allowsLiteralValues;
		this.handledReferenceSchemes = handledReferenceSchemes;
		this.translatedElementClass = translatedElementClass;
	}

	@Override
	public boolean allowsLiteralValues() {
		return this.allowsLiteralValues;
	}

	@Override
	public List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes() {
		return unmodifiableList(this.handledReferenceSchemes);
	}

	@Override
	public Class<?> getTranslatedElementClass() {
		return this.translatedElementClass;
	}
}
