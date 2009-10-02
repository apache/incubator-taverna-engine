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
package net.sf.taverna.platform.spring.orm.hibernate3;

import java.util.List;

import net.sf.taverna.raven.spi.SpiRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.ReflectHelper;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * An extension to the regular spring hibernate session factory which allows an
 * additional property containing a list of SpiRegistry beans. If this is
 * defined then each SpiRegistry in the list is used to get a list of Class
 * objects and these are in turn used to add classes to the hibernate
 * configuration.
 * 
 * @author Tom Oinn
 */
public class SpiRegistryAwareLocalSessionFactoryBean extends
		LocalSessionFactoryBean {

	private Log log = LogFactory
			.getLog(SpiRegistryAwareLocalSessionFactoryBean.class);
	private List<SpiRegistry> spiRegistries = null;
	private List<SpiRegistry> preloadRegistries = null;

	/**
	 * Set the list of SPI registries from which to pull class definitions for
	 * mapped beans.
	 * 
	 * @param spis
	 */
	public void setSpiRegistries(List<SpiRegistry> spis) {
		log.debug("Set SPI registry list");
		this.spiRegistries = spis;
	}

	/**
	 * Set the list of preload registries - classes from these registries are
	 * not mapped to hibernate but are registered with the customized classname ->
	 * classloader linkage. This is therefore used for any classes which mapped
	 * classes will depend upon but which don't have a mapping themselves such
	 * as component classes
	 */
	public void setPreloadRegistries(List<SpiRegistry> spis) {
		log.debug("Set preload list");
		this.preloadRegistries = spis;
	}

	/**
	 * Use any defined spiRegistries, adding any classes they contain to the
	 * configuration before returning it. Initially delegates to the superclass,
	 * so explicitly defined mappings will be mapped before those from the
	 * SpiRegistry(s)
	 */
	@Override
	protected Configuration newConfiguration() {
		Configuration conf = super.newConfiguration();
		log.trace("Starting SPI registry based configuration");
		if (preloadRegistries != null) {
			for (SpiRegistry spi : preloadRegistries) {
				for (Class<?> theClass : spi.getClasses()) {
					log.info("Preloading class '" + theClass.getCanonicalName()
							+ "' from SPI to hibernate classloader mapping");
					ReflectHelper.registerClass(theClass);
				}
			}
		}
		if (spiRegistries != null) {
			for (SpiRegistry spi : spiRegistries) {
				for (Class<?> theClass : spi.getClasses()) {
					log.info("Added class '" + theClass.getCanonicalName()
							+ "' from SPI to hibernate configuration");
					ReflectHelper.registerClass(theClass);
					conf.addClass(theClass);
				}
			}
		} else {
			log.info("No SPI registries defined");
		}
		log.trace("Done SPI registry based configuration");
		return conf;
	}

}
