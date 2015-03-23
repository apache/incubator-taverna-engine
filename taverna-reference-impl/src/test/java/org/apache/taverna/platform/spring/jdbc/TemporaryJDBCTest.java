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

package org.apache.taverna.platform.spring.jdbc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * Test {@link TemporaryJDBC}
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class TemporaryJDBCTest {

	private static final String DB = ".db";
	private static final String T2PLATFORM = "t2platform-";
	private static final String CREATE_TRUE = ";create=true";
	private static final String JDBC_DERBY = "jdbc:derby:";

	@Test
	public void getDerby() throws Exception {
		TemporaryJDBC temporaryJDBC = new TemporaryJDBC();
		String jdbcURL = temporaryJDBC.getTemporaryDerbyJDBC();
		assertTrue("Not a Derby URL", jdbcURL.startsWith(JDBC_DERBY));
		String url = jdbcURL.split(JDBC_DERBY)[1];
		assertTrue("Did not end with " + CREATE_TRUE, url.endsWith(CREATE_TRUE));
		String location = url.split(CREATE_TRUE)[0];
		assertFalse("Location was an empty string", location.equals(""));
		File locationFile = new File(location);
		assertFalse("File already exists: " + locationFile, locationFile.exists());
		File parentFile = locationFile.getParentFile();
		assertTrue("Parent directory did not exist", parentFile.isDirectory());
		assertTrue("Did not end with " + T2PLATFORM, parentFile.getName().startsWith(T2PLATFORM));
		assertTrue("Did not start with " + DB , parentFile.getName().endsWith(DB));
	}
	
}
