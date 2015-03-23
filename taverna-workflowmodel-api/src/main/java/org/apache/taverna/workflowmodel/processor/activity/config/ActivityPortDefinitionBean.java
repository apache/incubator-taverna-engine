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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationProperty;

/**
 * A generic bean that describes the shared properties of input and output
 * ports.
 * 
 * @author Stuart Owen
 * 
 */
@ConfigurationBean(uri = "http://ns.taverna.org.uk/2010/scufl2#PortDefinition")
public abstract class ActivityPortDefinitionBean {
	private String name;
	private int depth;
	private List<String> mimeTypes;

	/**
	 * @return the port name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the port name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the depth of the port
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth of the port
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return a list a MIME types that describe the port
	 */
	public List<String> getMimeTypes() {
		if (mimeTypes == null)
			return Collections.emptyList();
		return mimeTypes;
	}

	/**
	 * @param mimeTypes
	 *            the list of MIME-types that describe the port
	 */
	public void setMimeTypes(List<String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	/**
	 * @param mimeTypes
	 *            the list of MIME-types that describe the port
	 */
	@ConfigurationProperty(name = "expectedMimeType", label = "Mime Types", description = "The MIME-types that describe the port", required = false)
	public void setMimeTypes(Set<URI> mimeTypes) {
		this.mimeTypes = new ArrayList<>();
		for (URI uri : mimeTypes)
			this.mimeTypes.add("'"
					+ URI.create("http://purl.org/NET/mediatypes/").relativize(
							uri) + "'");
	}
}
