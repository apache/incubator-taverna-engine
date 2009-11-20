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
package net.sf.taverna.platform.spring.jdbc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Acts as the bridge to the anonymous temporary classloader used to register
 * the real JDBC drivers. The ProxyDriverManager uses this to mediate access to
 * that anonymous class world and proxy it with a DriverProxy
 * 
 * @author Tom Oinn
 */
public class ProxyHelper {

	/**
	 * Used to check whether we can 'see' the specified class.
	 * 
	 * @throws SecurityException
	 *             generally thrown if class loaded by a different class loader
	 *             from us
	 */
	public static ClassLoader getClassLoader(Class<?> theClass) {
		return theClass.getClassLoader();
	}

	/**
	 * Calls the DriverManager.getDrivers method from the temporary classloader
	 * environment.
	 * 
	 * @return an enumeration of JDBC drivers visible to the temporary
	 *         classloader
	 */
	public static java.util.Enumeration<Driver> getDrivers() {
		return DriverManager.getDrivers();
	}

	/**
	 * Calls DriverManager.deregisterDriver on the DriverManager from the
	 * temporary classloader environment, used along with deregistration of the
	 * DriverProxy
	 */
	public static void deregisterDriver(Driver driver) throws SQLException {
		DriverManager.deregisterDriver(driver);
	}
}
