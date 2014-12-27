/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.platform;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
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
