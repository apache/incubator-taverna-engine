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

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.taverna.raven.repository.Artifact;
import net.sf.taverna.raven.repository.ArtifactStatus;
import net.sf.taverna.raven.repository.Repository;

/**
 * Supports registration of JDBC drivers with the DriverManager through Raven
 * artifact specified and classname. This internally causes Raven to download
 * the appropriate artifact if required.
 * 
 * @author Tom Oinn
 * 
 */
public class ProxyDriverManager {

	private Log log = LogFactory.getLog(ProxyDriverManager.class);

	private static String proxyHelperClassName = "net.sf.taverna.platform.spring.jdbc.ProxyHelper";

	/**
	 * Register a JDBC driver with the DriverManager, making it visible to the
	 * platform's classloader.
	 * 
	 * @param repository
	 *            a Raven repository to use when downloading and linking the
	 *            driver
	 * @param driverArtifact
	 *            the maven artifact containing the JDBC driver
	 * @param driverClassName
	 *            the JDBC driver class name
	 */
	public static ProxyDriverManager registerDriver(Repository repository,
			Artifact driverArtifact, String driverClassName) {
		// First obtain the appropriate artifact...
		try {
			repository.addArtifact(driverArtifact);
			repository.update();
			if (repository.getStatus(driverArtifact).equals(
					ArtifactStatus.Ready) == false) {
				throw new RuntimeException("Cannot initialize artifact "
						+ driverArtifact + " for JDBC driver "
						+ driverClassName);
			}
			// Use the artifact class loader to infer the location of the
			// downloaded jar file.
			ClassLoader artifactLoader = repository.getLoader(driverArtifact,
					null);

			// URLs to use to populate the temporary class loader
			URL rootJarUrl = getRootJarUrl(driverClassName, artifactLoader);
			URL proxyHelperLocation = null;
			try {
				proxyHelperLocation = getProxyHelperLocation();
			} catch(IndexOutOfBoundsException e) {
				proxyHelperLocation = new URL(proxyHelperClassName);
			}
			ClassLoader loader;
			if (proxyHelperLocation != null) {
				loader = URLClassLoader.newInstance(new URL[] {rootJarUrl, proxyHelperLocation}, null);
			} else {
				loader = URLClassLoader.newInstance(new URL[] {rootJarUrl}, null);
			}
			URL[] loaderUrls = new URL[] {
					rootJarUrl,
					proxyHelperLocation };
			return new ProxyDriverManager(loader, driverClassName);
		} catch (Exception e) {
			throw new RuntimeException("Failed to register driver", e);
		}
	}

	private final ClassLoader loader;
	private final Method getClassLoader;
	private final Method deregisterDriver;
	private final Method getDrivers;

	private ProxyDriverManager(ClassLoader loader, String driverClassName)
			throws SQLException, ClassNotFoundException {

		this.loader = loader;
		Class<?> proxyHelperClass = null;
		// Get helper class
		try {
			proxyHelperClass = Class.forName(proxyHelperClassName, true, loader);
		} catch (ClassNotFoundException cnfe) {
			try {
				proxyHelperClass = Class.forName(proxyHelperClassName);
			}
			catch (ClassNotFoundException e) {
			log.error("Failed to locate proxy helper class", cnfe);
			throw new RuntimeException(e);
}
		}
		final Class<?> helperClass = proxyHelperClass;
		// Find and store methods on helper class for later use
		try {
			this.deregisterDriver = helperClass.getMethod("deregisterDriver",
					Driver.class);
			this.getDrivers = helperClass.getMethod("getDrivers");
			this.getClassLoader = helperClass.getMethod("getClassLoader",
					Class.class);
		} catch (SecurityException se) {
			log.error("Can't access method in proxy helper", se);
			throw new RuntimeException(se);
		} catch (NoSuchMethodException nsme) {
			log.error("Can't locate method in proxy helper", nsme);
			throw new RuntimeException(nsme);
		}
		boolean registered = false;
		try {
			Class.forName(driverClassName, true, loader);
			registerProxyDrivers();
			registered = true;
		} finally {
			if (!registered) {
				deregister();
			}
		}
	}

	/**
	 * Iterate over the drivers visible to the private classloader and register
	 * proxy drivers for each one which are therefore visible to <em>this</em>
	 * classloader.
	 * 
	 * @throws SQLException
	 */
	private void registerProxyDrivers() throws SQLException {
		for (Enumeration<?> en = getLoaderDrivers(); en.hasMoreElements();) {
			Driver driver = (Driver) en.nextElement();
			if (isLoaderClassLoader(driver.getClass())) {
				DriverProxy proxy = new DriverProxy(driver);
				DriverManager.registerDriver(proxy);
			}
		}
	}

	/**
	 * De-register Drivers. Drivers in this context are de-registered. Some
	 * cleanup of the dynamic class loader is required due to a memory leak in
	 * JDBC.
	 */
	public void deregister() throws SQLException {
		for (Enumeration<Driver> en = DriverManager.getDrivers(); en
				.hasMoreElements();) {
			Driver driver = en.nextElement();
			// If this is a DriverProxy then unload it from the DriverManager on
			// this side, also have to de-register the driver it's proxying on
			// the other side!
			if (driver instanceof DriverProxy) {
				DriverProxy proxy = (DriverProxy) driver;
				Driver target = proxy.getTarget();
				if (isLoaderClassLoader(target.getClass())) {
					DriverManager.deregisterDriver(proxy);
				}
			}
		}
		for (Enumeration<?> en = getLoaderDrivers(); en.hasMoreElements();) {
			Driver driver = (Driver) en.nextElement();
			try {
				deregisterDriver.invoke(null, driver);
			} catch (IllegalArgumentException exc) {
				throw new Error(exc);
			} catch (java.lang.reflect.InvocationTargetException exc) {
				Throwable cause = exc.getCause();
				if (cause instanceof SQLException) {
					throw (SQLException) cause;
				} else {
					throw new Error(exc);
				}
			} catch (java.lang.IllegalAccessException exc) {
				throw new Error(exc);
			}
		}
	}

	/**
	 * Use reflection to call the DriverManager.getDrivers method through the
	 * proxy helper. As this is in the same classloader as used to register the
	 * JDBC driver from Raven we'll actually be able to see the registered
	 * driver.
	 * 
	 * @return enumeration of drivers in the DriverManager visible to the newly
	 *         created private classloader.
	 */
	private Enumeration<?> getLoaderDrivers() {
		try {
			return (Enumeration<?>) getDrivers.invoke(null);
		} catch (IllegalArgumentException exc) {
			throw new Error(exc);
		} catch (java.lang.reflect.InvocationTargetException exc) {
			throw new Error(exc);
		} catch (java.lang.IllegalAccessException exc) {
			throw new Error(exc);
		}
	}

	/**
	 * Indicates whether {@code clazz} was loaded by {@link #loader}.
	 */
	private boolean isLoaderClassLoader(Class<?> clazz) {
		final ClassLoader clazzClassLoader;
		try {
			clazzClassLoader = (ClassLoader) getClassLoader.invoke(null, clazz);
		} catch (IllegalArgumentException exc) {
			throw new Error(exc);
		} catch (java.lang.reflect.InvocationTargetException exc) {
			Throwable cause = exc.getCause();
			if (cause instanceof SecurityException) {
				return false;
			} else {
				throw new Error(exc);
			}
		} catch (java.lang.IllegalAccessException exc) {
			throw new Error(exc);
		}
		return clazzClassLoader == this.loader;
	}

	private static URL getProxyHelperLocation() throws MalformedURLException {
		return getRootJarUrl(proxyHelperClassName, ProxyDriverManager.class
				.getClassLoader());
	}

	public static URL getRootJarUrl(String className, ClassLoader loader)
			throws MalformedURLException {
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		String classAsResource = className.replaceAll("\\.", "/") + ".class";
		URL resourceURL = loader.getResource(classAsResource);
		String resourceFile = resourceURL.getFile();
		String result = "";
		if (resourceFile.contains(".jar")) {
		result = resourceURL.getFile().substring(0,
				resourceURL.getFile().indexOf(".jar"))
				+ ".jar";
		} else {
			result = "file:" + resourceFile.substring(0, resourceFile.lastIndexOf("/")) + "/";
		}
		return new URL(result);
	}

}
