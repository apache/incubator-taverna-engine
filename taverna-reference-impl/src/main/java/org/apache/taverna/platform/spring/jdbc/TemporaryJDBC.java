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

import static java.io.File.createTempFile;

import java.io.File;
import java.io.IOException;

/**
 * Create JDBC connection strings for temporary use (ie. from tests)
 * <p>
 * {@link #getTemporaryDerbyJDBC()} creates a temporary directory that is used
 * to construct the JDBC connection string for a local Derby database.
 * </p>
 * <p>
 * This is most useful from a spring configuration, for example when using
 * {@link InterpolatingDriverManagerDataSource}:
 * </p>
 * 
 * <pre>
 * &lt;!-- Apache Derby rooted at a temporary directory --&gt;
 *  &lt;bean id=&quot;t2reference.jdbc.temporaryjdbc&quot;
 *  class=&quot;org.apache.taverna.platform.spring.jdbc.TemporaryJDBC&quot;&gt;
 *  &lt;/bean&gt;
 *  &lt;bean id=&quot;t2reference.jdbc.url&quot; class=&quot;java.lang.String&quot;
 *  factory-bean=&quot;t2reference.jdbc.temporaryjdbc&quot;
 *  factory-method=&quot;getTemporaryDerbyJDBC&quot; /&gt;
 *  &lt;bean id=&quot;t2reference.jdbc.datasource&quot;
 *  class=&quot;org.apache.taverna.platform.spring.jdbc.InterpolatingDriverManagerDataSource&quot;&gt;
 *  &lt;property name=&quot;driverClassName&quot;&gt;
 *  &lt;value&gt;org.apache.derby.jdbc.EmbeddedDriver&lt;/value&gt;
 *  &lt;/property&gt;
 *  &lt;property name=&quot;url&quot;&gt;
 *  &lt;ref bean=&quot;t2reference.jdbc.url&quot; /&gt;
 *  &lt;/property&gt;
 *  &lt;property name=&quot;repository&quot;&gt;
 *  &lt;ref bean=&quot;raven.repository&quot; /&gt;
 *  &lt;/property&gt;
 *  &lt;property name=&quot;driverArtifact&quot;&gt;
 *  &lt;value&gt;org.apache.derby:derby:10.4.1.3&lt;/value&gt;
 *  &lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre>
 * 
 * @author Stian Soiland-Reyes
 */
public class TemporaryJDBC {
	public String getTemporaryDerbyJDBC() throws IOException {
		File tmpDir = createTempFile("t2platform-", ".db");
		tmpDir.delete();
		if (!tmpDir.mkdir())
			throw new IOException("Could not create temporary directory "
					+ tmpDir);
		return "jdbc:derby:" + tmpDir.getPath() + "/database;create=true";
	}
}
