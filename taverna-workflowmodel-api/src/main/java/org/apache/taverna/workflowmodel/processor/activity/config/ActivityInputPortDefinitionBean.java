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

package org.apache.taverna.workflowmodel.processor.activity.config;

import java.util.Collections;
import java.util.List;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;

/**
 * A bean that describes properties of an Input port.
 * 
 * @author Stuart Owen
 * @author Stian Soiland-Reyes
 */
@ConfigurationBean(uri = "http://ns.taverna.org.uk/2010/scufl2#InputPortDefinition")
public class ActivityInputPortDefinitionBean extends ActivityPortDefinitionBean {
	private List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes;
	private Class<?> translatedElementType;
	private boolean allowsLiteralValues;

	public List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes() {
		if (handledReferenceSchemes == null)
			return Collections.emptyList();
		return handledReferenceSchemes;
	}

	public void setHandledReferenceSchemes(
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes) {
		this.handledReferenceSchemes = handledReferenceSchemes;
	}

	public Class<?> getTranslatedElementType() {
		return translatedElementType;
	}

	public void setTranslatedElementType(Class<?> translatedElementType) {
		this.translatedElementType = translatedElementType;
	}

	public boolean getAllowsLiteralValues() {
		return allowsLiteralValues;
	}

	public void setAllowsLiteralValues(boolean allowsLiteralValues) {
		this.allowsLiteralValues = allowsLiteralValues;
	}
}
