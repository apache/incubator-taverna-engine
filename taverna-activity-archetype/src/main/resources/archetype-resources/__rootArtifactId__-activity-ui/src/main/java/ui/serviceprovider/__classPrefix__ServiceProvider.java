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
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;

public class \${classPrefix}ServiceProvider implements ServiceDescriptionProvider {

	private static final URI providerId = URI
		.create("http://example.com/2011/service-provider/\${rootArtifactId}");

	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		// Use callback.status() for long-running searches
		// callBack.status("Resolving example services");

		List<ServiceDescription> results = new ArrayList<ServiceDescription>();

		// FIXME: Implement the actual service search/lookup instead
		// of dummy for-loop
		for (int i = 1; i <= 5; i++) {
			\${classPrefix}ServiceDesc service = new ${classPrefix}ServiceDesc();
			// Populate the service description bean
			service.setExampleString("Example " + i);
			service.setExampleUri("http://localhost:8192/service");

			// Optional: set description
			service.setDescription("Service example number " + i);
			results.add(service);
		}

		// partialResults() can also be called several times from inside
		// for-loop if the full search takes a long time
		callBack.partialResults(results);

		// No more results will be coming
		callBack.finished();
	}

	/**
	 * Icon for service provider
	 */
	public Icon getIcon() {
		return \${classPrefix}ServiceIcon.getIcon();
	}

	/**
	 * Name of service provider, appears in right click for 'Remove service
	 * provider'
	 */
	public String getName() {
		return "My example service";
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getId() {
		return providerId.toASCIIString();
	}

}
