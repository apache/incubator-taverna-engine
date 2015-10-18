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

package org.apache.taverna.configuration.database;

import java.util.Map;

import org.apache.taverna.configuration.Configurable;

/**
 *
 *
 * @author David Withers
 */
public interface DatabaseConfiguration extends Configurable {

	public static final String IN_MEMORY = "in_memory";
	public static final String ENABLE_PROVENANCE = "provenance";
	public static final String CONNECTOR_TYPE = "connector";
	public static final String PORT = "port";
	public static final String CURRENT_PORT = "current_port";
	public static final String REFERENCE_SERVICE_CONTEXT = "referenceService.context";
	public static final String IN_MEMORY_CONTEXT = "inMemoryReferenceServiceContext.xml";
	public static final String HIBERNATE_CONTEXT = "hibernateReferenceServiceContext.xml";
	public static final String HIBERNATE_DIALECT = "dialect";
	public static final String START_INTERNAL_DERBY = "start_derby";
	public static final String POOL_MAX_ACTIVE = "pool_max_active";
	public static final String POOL_MIN_IDLE = "pool_min_idle";
	public static final String POOL_MAX_IDLE = "pool_max_idle";
	public static final String DRIVER_CLASS_NAME = "driver";
	public static final String JDBC_URI = "jdbcuri";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EXPOSE_DATANATURE = "taverna.exposedatanature";
	// FIXME: these should me just mysql & derby - but build & dependency issues
	// is causing the provenance to expect these values:
	public static final String CONNECTOR_MYSQL = "mysql";
	public static final String CONNECTOR_DERBY = "derby";
	public static final String JNDI_NAME = "jdbc/taverna";

	public boolean isAutoSave();

	public void enableAutoSave();

	public void disableAutoSave();

	public boolean isInMemory();

	public void setInMemory(boolean value);

	public boolean isExposeDatanature();

	public void setExposeDatanature(boolean exposeDatanature);

	public String getDatabaseContext();

	public void setPort(int port);

	public void setPort(String port);

	public void setDriverClassName(String driverClassName);

	public String getDriverClassName();

	public boolean isProvenanceEnabled();

	public void setProvenanceEnabled(boolean value);

	public void setStartInternalDerbyServer(boolean value);

	public boolean getStartInternalDerbyServer();

	public int getPort();

	public void setCurrentPort(int port);

	public int getCurrentPort();

	public int getPoolMaxActive();

	public int getPoolMinIdle();

	public int getPoolMaxIdle();

	public String getCategory();

	public Map<String, String> getDefaultPropertyMap();

	public String getHibernateDialect();

	public String getDisplayName();

	public String getFilePrefix();

	public String getUUID();

	public String getConnectorType();

	public String getJDBCUri();

	public void setJDBCUri(String uri);

	public String getUsername();

	public String getPassword();

}