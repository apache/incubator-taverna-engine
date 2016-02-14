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

package org.apache.taverna.configuration.database.impl;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import org.apache.taverna.configuration.database.DatabaseConfiguration;
import org.apache.taverna.configuration.database.DatabaseManager;
import org.apache.taverna.configuration.app.ApplicationConfiguration;

/**
 * A set of utility methods related to basic data management.
 *
 * @author Stuart Owen
 * @author Stian Soiland-Reyes
 *
 */
public class DatabaseManagerImpl implements DatabaseManager {

	private final static Logger logger = Logger.getLogger(DatabaseManagerImpl.class);

	private  NetworkServerControl server;

	private BasicDataSource dataSource;

	private DatabaseConfiguration databaseConfiguration;

	private ApplicationConfiguration applicationConfiguration;

	public DatabaseManagerImpl(ApplicationConfiguration applicationConfiguration, DatabaseConfiguration databaseConfiguration) throws SQLException {
		this.applicationConfiguration = applicationConfiguration;
		this.databaseConfiguration = databaseConfiguration;
		getConnection();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	@Override
	public DataSource getDataSource() {
		if (dataSource == null) {
			setupDataSource();
		}
		return dataSource;
	}

	@Override
	public synchronized void startDerbyNetworkServer() {
		setDerbyPaths();

        System.setProperty("derby.drda.host","localhost");
        System.setProperty("derby.drda.minThreads","5");
        System.setProperty("derby.drda.maxThreads",String.valueOf(databaseConfiguration.getPoolMaxActive()));
        int port = databaseConfiguration.getPort();
        int maxPort = port+50;

        try {
        	System.setProperty("derby.drda.portNumber",String.valueOf(port));
            if (server==null) server = new NetworkServerControl();
            while(port<maxPort) { //loop to find another available port on which Derby isn't already running
            	if (!isRunning()) break;
            	logger.info("Derby connection port: "+port+" is currently not available for Taverna, trying next value");
            	port++;
            	System.setProperty("derby.drda.portNumber",String.valueOf(port));
            	server = new NetworkServerControl();
            }
            server.start(null);
            databaseConfiguration.setCurrentPort(port);
        } catch (Exception ex) {
            logger.error("Error starting up Derby network server",ex);
        }
    }

	@Override
	public void stopDerbyNetworkServer() {
		try {
			server.shutdown();
		} catch (Exception e) {
			logger.error("Error shutting down Derby network server",e);
		}
	}

	@Override
	public boolean isRunning() {
		if (server==null) {
			return false;
		}
		else {
			try {
				server.ping();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	private void setupDataSource() {
		setDerbyPaths();

		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(databaseConfiguration.getDriverClassName());

		System.setProperty("hibernate.dialect", databaseConfiguration.getHibernateDialect());

		dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		dataSource.setMaxActive(databaseConfiguration.getPoolMaxActive());
		dataSource.setMinIdle(databaseConfiguration.getPoolMinIdle());
		dataSource.setMaxIdle(databaseConfiguration.getPoolMaxIdle());
		dataSource.setDefaultAutoCommit(true);
		dataSource.setInitialSize(databaseConfiguration.getPoolMinIdle());
		//Derby blows up if the username of password is empty (even an empty string thats not null).
		if (databaseConfiguration.getUsername()!=null && databaseConfiguration.getUsername().length()>=1) dataSource.setUsername(databaseConfiguration.getUsername());
		if (databaseConfiguration.getPassword()!=null && databaseConfiguration.getPassword().length()>=1) dataSource.setPassword(databaseConfiguration.getPassword());

		dataSource.setUrl(databaseConfiguration.getJDBCUri());
    }

	private void setDerbyPaths() {
		if (databaseConfiguration.getConnectorType() == DatabaseConfiguration.CONNECTOR_DERBY) {
			String homeDir = applicationConfiguration.getApplicationHomeDir().toAbsolutePath().toString();
			System.setProperty("derby.system.home",homeDir);
			Path logFile = applicationConfiguration.getLogDir().resolve("derby.log");
			System.setProperty("derby.stream.error.file", logFile.toAbsolutePath().toString());
		}

	}

	/**
	 * Sets the databaseConfiguration.
	 *
	 * @param databaseConfiguration the new value of databaseConfiguration
	 */
	public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

	/**
	 * Sets the applicationConfiguration.
	 *
	 * @param applicationConfiguration the new value of applicationConfiguration
	 */
	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}
