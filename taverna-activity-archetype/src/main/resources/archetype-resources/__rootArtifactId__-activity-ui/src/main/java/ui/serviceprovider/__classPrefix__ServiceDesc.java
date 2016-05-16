##    Licensed to the Apache Software Foundation (ASF) under one or more
##    contributor license agreements.  See the NOTICE file distributed with
##    this work for additional information regarding copyright ownership.
##    The ASF licenses this file to You under the Apache License, Version 2.0
##    (the "License"); you may not use this file except in compliance with
##    the License.  You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
##    Unless required by applicable law or agreed to in writing, software
##    distributed under the License is distributed on an "AS IS" BASIS,
##    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##    See the License for the specific language governing permissions and
##    limitations under the License.
##
## Note: Above Velocity comment should NOT be included in generated
## code from the archetype
package \${package}.ui.serviceprovider;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.taverna.servicedescriptions.ServiceDescription;

public class \${classPrefix}ServiceDesc extends ServiceDescription {

	private static final URI ACTIVITY_TYPE = URI
			.create("http://example.com/2013/activity/\${rootArtifactId}");

	// FIXME: Replace example fields and getters/setters with any required
	// and optional fields. (All fields are searchable in the Service palette,
	// for instance try a search for exampleString:3)
	private String exampleString;
	private String exampleUri;

	public String getExampleString() {
		return exampleString;
	}
	public void setExampleString(String exampleString) {
		this.exampleString = exampleString;
	}

	public String getExampleUri() {
		return exampleUri;
	}
	public void setExampleUri(String exampleUri) {
		this.exampleUri = exampleUri;
	}

	/**
	 * The type of Activity which should be instantiated when adding a service
	 * for this description
	 */
	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	/**
	 * The configuration  which is to be used for configuring the instantiated activity.
	 * Making this configuration will typically require some of the fields set on this service
	 * description, like an endpoint URL or method name.
	 *
	 */
	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = configuration.getJsonAsObjectNode();
		json.put("exampleString", exampleString);
		json.put("exampleUri", exampleUri);
		return configuration;
	}

	/**
	 * An icon to represent this service description in the service palette.
	 */
	@Override
	public Icon getIcon() {
		return \${classPrefix}ServiceIcon.getIcon();
	}

	/**
	 * The display name that will be shown in service palette and will
	 * be used as a template for processor name when added to workflow.
	 */
	@Override
	public String getName() {
		return exampleString;
	}

	/**
	 * The path to this service description in the service palette. Folders
	 * will be created for each element of the returned path.
	 */
	@Override
	public List<String> getPath() {
		// For deeper paths you may return several strings
		return Arrays.asList("\${classPrefix}s " + exampleUri);
	}

	/**
	 * Return a list of data values uniquely identifying this service
	 * description (to avoid duplicates). Include only primary key like fields,
	 * ie. ignore descriptions, icons, etc.
	 */
	@Override
	protected List<? extends Object> getIdentifyingData() {
		// FIXME: Use your fields instead of example fields
		return Arrays.<Object>asList(exampleString, exampleUri);
	}

}
