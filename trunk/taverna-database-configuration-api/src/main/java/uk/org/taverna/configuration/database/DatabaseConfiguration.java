/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
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
package uk.org.taverna.configuration.database;

import java.util.Map;

import uk.org.taverna.configuration.Configurable;

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