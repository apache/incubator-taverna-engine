/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester
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
package uk.org.taverna.platform.database.impl;

import java.util.HashMap;
import java.util.Map;

import uk.org.taverna.platform.configuration.AbstractConfigurable;
import uk.org.taverna.platform.configuration.ConfigurationManager;
import uk.org.taverna.platform.database.DatabaseConfiguration;

/**
 * Configuration for the reference service and provenance.
 *
 * @author David Withers
 * @author Stuart Owen
 */

public class DatabaseConfigurationImpl extends AbstractConfigurable implements DatabaseConfiguration {

	private Map<String, String> defaultPropertyMap;

	private boolean autoSave = true;

	public DatabaseConfigurationImpl(ConfigurationManager configurationManager) {
		super(configurationManager);
	}

	@Override
	public boolean isAutoSave() {
		return autoSave;
	}

	@Override
	public void enableAutoSave() {
		autoSave = true;
	}

	@Override
	public void disableAutoSave() {
		autoSave = false;
	}

	@Override
	protected void store() {
		if (autoSave) {
			super.store();
		}
	}

	@Override
	public boolean isInMemory() {
		return getProperty(IN_MEMORY).equalsIgnoreCase("true");
	}

	@Override
	public void setInMemory(boolean value) {
		setProperty(IN_MEMORY, String.valueOf(value));
	}

	@Override
	public boolean isExposeDatanature() {
		return getProperty(EXPOSE_DATANATURE).equalsIgnoreCase("true");
	}

	@Override
	public void setExposeDatanature(boolean exposeDatanature) {
		setProperty(EXPOSE_DATANATURE, String.valueOf(exposeDatanature));
	}

	@Override
	public String getDatabaseContext() {
		if (getProperty(IN_MEMORY).equalsIgnoreCase("true")) {
			return IN_MEMORY_CONTEXT;
		} else {
			return HIBERNATE_CONTEXT;
		}
	}

	@Override
	public void setPort(int port) {
		setPort(String.valueOf(port));
	}

	@Override
	public void setPort(String port) {
		setProperty(PORT, port);
	}

	@Override
	public void setDriverClassName(String driverClassName) {
		setProperty(DRIVER_CLASS_NAME, driverClassName);
	}

	@Override
	public String getDriverClassName() {
		return getProperty(DRIVER_CLASS_NAME);
	}

	@Override
	public boolean isProvenanceEnabled() {
		return getProperty(ENABLE_PROVENANCE).equalsIgnoreCase("true");
	}

	@Override
	public void setProvenanceEnabled(boolean value) {
		setProperty(ENABLE_PROVENANCE, String.valueOf(value));
	}

	@Override
	public void setStartInternalDerbyServer(boolean value) {
		setProperty(START_INTERNAL_DERBY, String.valueOf(value));
	}

	@Override
	public boolean getStartInternalDerbyServer() {
		return getProperty(START_INTERNAL_DERBY).equalsIgnoreCase("true");
	}

	@Override
	public int getPort() {
		return Integer.valueOf(getProperty(PORT));
	}

	@Override
	public void setCurrentPort(int port) {
		setProperty(CURRENT_PORT, String.valueOf(port));
	}

	@Override
	public int getCurrentPort() {
		return Integer.valueOf(getProperty(CURRENT_PORT));
	}

	@Override
	public int getPoolMaxActive() {
		return Integer.valueOf(getProperty(POOL_MAX_ACTIVE));
	}

	@Override
	public int getPoolMinIdle() {
		return Integer.valueOf(getProperty(POOL_MIN_IDLE));
	}

	@Override
	public int getPoolMaxIdle() {
		return Integer.valueOf(getProperty(POOL_MAX_IDLE));
	}

	@Override
	public String getCategory() {
		return "general";
	}

	@Override
	public Map<String, String> getDefaultPropertyMap() {

		if (defaultPropertyMap == null) {
			defaultPropertyMap = new HashMap<String, String>();
			defaultPropertyMap.put(IN_MEMORY, "true");
			defaultPropertyMap.put(ENABLE_PROVENANCE, "true");
			defaultPropertyMap.put(PORT, "1527");
			// defaultPropertyMap.put(DRIVER_CLASS_NAME,
			// "org.apache.derby.jdbc.ClientDriver");
			defaultPropertyMap.put(DRIVER_CLASS_NAME,
					"org.apache.derby.jdbc.EmbeddedDriver");
			defaultPropertyMap.put(HIBERNATE_DIALECT,
					"org.hibernate.dialect.DerbyDialect");
			defaultPropertyMap.put(POOL_MAX_ACTIVE, "50");
			defaultPropertyMap.put(POOL_MAX_IDLE, "50");
			defaultPropertyMap.put(POOL_MIN_IDLE, "10");
			defaultPropertyMap.put(USERNAME, "");
			defaultPropertyMap.put(PASSWORD, "");
			defaultPropertyMap.put(JDBC_URI,
					"jdbc:derby:t2-database;create=true;upgrade=true");
			defaultPropertyMap.put(START_INTERNAL_DERBY, "false");

			defaultPropertyMap.put(CONNECTOR_TYPE, CONNECTOR_DERBY);
			defaultPropertyMap.put(EXPOSE_DATANATURE, "false");
		}
		return defaultPropertyMap;
	}

	@Override
	public String getHibernateDialect() {
		return getProperty(HIBERNATE_DIALECT);
	}

	@Override
	public String getDisplayName() {
		return "Data and provenance";
	}

	@Override
	public String getFilePrefix() {
		return "DataAndProvenance";
	}

	@Override
	public String getUUID() {
		return "6BD3F5C1-C68D-4893-8D9B-2F46FA1DDB19";
	}

	@Override
	public String getConnectorType() {
		return getProperty(CONNECTOR_TYPE);
	}

	@Override
	public String getJDBCUri() {
		if (CONNECTOR_DERBY.equals(getConnectorType())
				&& getStartInternalDerbyServer()) {
			return "jdbc:derby://localhost:" + getCurrentPort()
					+ "/t2-database;create=true;upgrade=true";
		} else {
			return getProperty(JDBC_URI);
		}
	}

	@Override
	public void setJDBCUri(String uri) {
		setProperty(JDBC_URI, uri);
	}

	@Override
	public String getUsername() {
		return getProperty(USERNAME);
	}

	@Override
	public String getPassword() {
		return getProperty(PASSWORD);
	}
}
