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
package net.sf.taverna.platform.spring;

import java.util.List;

import net.sf.taverna.raven.spi.InstanceRegistry;
import net.sf.taverna.raven.spi.SpiRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for the SPI instance registry functionality in Raven. Inject an
 * SpiRegistry and a list of constructor arguments to initialize.
 * 
 * @author Tom Oinn
 * 
 */
public class InstanceRegistryFactoryBean implements FactoryBean {

	private SpiRegistry classRegistry = null;
	private List<Object> constructorArgs = null;
	private Log log = LogFactory.getLog(InstanceRegistryFactoryBean.class);

	/**
	 * Return a newly constructed InstanceRegistry. If the spi registry property
	 * isn't defined this method throws a runtime exception and logs the problem
	 * to the logging system.
	 */
	@SuppressWarnings("unchecked")
	public Object getObject() throws Exception {
		if (classRegistry != null) {
			Object[] args;
			if (constructorArgs != null) {
				args = constructorArgs.toArray();
			} else {
				args = new Object[0];
			}
			return new InstanceRegistry(classRegistry, args);
		} else {
			log.error("Must specify spiRegistry property"
					+ " to construct an InstanceRegistry");
			throw new RuntimeException();
		}
	}

	/**
	 * Inject an SpiRegistry instance to provide classes to instantiate objects
	 * from within the instance registry
	 */
	public void setSpiRegistry(SpiRegistry registry) {
		this.classRegistry = registry;
	}

	/**
	 * Optionally specify a list of arguments to the constructors of classes in
	 * the spi registry. If this property is undefined it defaults to using an
	 * empty constructor list, in general this is the required behaviour when
	 * constructing beans as they'll always have a default constructor.
	 */
	public void setConstructorArgs(List<Object> args) {
		this.constructorArgs = args;
	}

	/**
	 * @return InstanceRegistry.class
	 */
	@SuppressWarnings("unchecked")
	public Class getObjectType() {
		return InstanceRegistry.class;
	}

	/**
	 * Singleton by default
	 */
	public boolean isSingleton() {
		return true;
	}

}
