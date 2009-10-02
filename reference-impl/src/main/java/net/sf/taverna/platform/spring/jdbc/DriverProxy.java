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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A proxy implementation of the java.sql.Driver interface, used to act as a
 * proxy in the platform classloading environment to a 'real' JDBC driver
 * registered from a different class loader. We need to do this because the
 * DriverManager implementation uses native methods to hide drivers loaded by
 * other classloaders than the one used to construct the caller, this way we can
 * have a real JDBC driver loaded from a raven artifact and have an instance of
 * the proxy loaded through the platform acting as a view over it.
 * <p>
 * To use this you should use the {@link ProxyDriverManager}.
 * 
 * @author Tom Oinn
 * 
 */
public class DriverProxy implements Driver {

	/**
	 * The driver being proxied
	 */
	private final Driver target;

	/**
	 * This is always constructed from ProxyDriverManager
	 * 
	 * @param target
	 *            Driver to proxy
	 */
	DriverProxy(java.sql.Driver target) {
		if (target == null) {
			throw new NullPointerException();
		}
		this.target = target;
	}

	/**
	 * Get the proxied driver instance
	 */
	public Driver getTarget() {
		return target;
	}

	/**
	 * Retrieves whether the target driver thinks that it can open a connection
	 * to the given URL.
	 */
	public boolean acceptsURL(String url) throws SQLException {
		return target.acceptsURL(url);
	}

	/**
	 * Uses the target Driver to attempt to make a database connection to the
	 * given URL.
	 */
	public Connection connect(String url, Properties info) throws SQLException {
		return target.connect(url, info);
	}

	/**
	 * Retrieves the target driver's major version number.
	 */
	public int getMajorVersion() {
		return target.getMajorVersion();
	}

	/**
	 * Gets the target driver's minor version number.
	 */
	public int getMinorVersion() {
		return target.getMinorVersion();
	}

	/**
	 * Gets information about the possible properties for the target driver.
	 */
	public java.sql.DriverPropertyInfo[] getPropertyInfo(String url,
			java.util.Properties info) throws SQLException {
		return target.getPropertyInfo(url, info);
	}

	/**
	 * Reports whether the target driver is a genuine JDBC Compliant driver.
	 */
	public boolean jdbcCompliant() {
		return target.jdbcCompliant();
	}

	/**
	 * Wrap original string description to denote a proxy driver in debug output
	 */
	@Override
	public String toString() {
		return "Proxy JDBC driver for : " + target;
	}

	/**
	 * Pretend to be the target as far as hashcode is concerned.
	 */
	@Override
	public int hashCode() {
		return target.hashCode();
	}

	/**
	 * Equal if the other is also a DriverProxy and both have the same target
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DriverProxy)) {
			return false;
		}
		DriverProxy other = (DriverProxy) obj;
		return this.target.equals(other.target);
	}

}
