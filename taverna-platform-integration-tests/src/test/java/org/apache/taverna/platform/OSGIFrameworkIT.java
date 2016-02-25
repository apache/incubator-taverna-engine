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
package org.apache.taverna.platform;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.springframework.osgi.util.OsgiStringUtils;

public class OSGIFrameworkIT extends PlatformIT {

	public void testOsgiEnvironment() throws Exception {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out.println(OsgiStringUtils.nullSafeName(bundles[i]));
		}
		System.out.println();
	}

	protected String[] getTestFrameworkBundlesNames() {
		String[] frameworkBundles = super.getTestFrameworkBundlesNames();
		System.out.println("Test Framework bundles:");
		for (String bundle : frameworkBundles) {
			System.out.println("  " + bundle);
		}
		return frameworkBundles;
	}

	protected String[] getTestBundlesNames() {
		String[] frameworkBundles = super.getTestBundlesNames();
		System.out.println("Framework bundles:");
		for (String bundle : frameworkBundles) {
			System.out.println("  " + bundle);
		}
		return frameworkBundles;
	}

	public void testPrintConfig() throws IOException {
		Resource[] bundles = getTestBundles();
		Resource[] testBundles = getTestFrameworkBundles();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		System.out.println("mkdir platform");
		System.out.println("mkdir platform/configuration");
		sb2.append("cp ");
		sb.append("osgi.bundles=");
		boolean printComma = false;
		for (Resource resource : bundles) {
			if (printComma) {
				sb.append(", ");
				sb2.append(" ");
			}
			sb.append(resource.getFilename() + "@start");
			sb2.append(resource.getFile());
			printComma = true;
		}
		for (Resource resource : testBundles) {
			if (!resource.getFilename().contains("test")) {
				if (printComma) {
					sb.append(", ");
					sb2.append(" ");
				}
				sb.append(resource.getFilename() + "@start");
				sb2.append(resource.getFile());
				printComma = true;
			}
		}
		sb2.append(" platform");
		System.out.println("echo \"" + sb.toString() + "\" > platform/configuration/config.ini");
		System.out.println(sb2.toString());
		System.out.println("zip platform.zip platform/*");
	}

}
