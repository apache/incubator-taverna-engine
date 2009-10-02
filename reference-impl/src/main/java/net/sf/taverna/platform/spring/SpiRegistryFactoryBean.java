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

import net.sf.taverna.raven.repository.Repository;
import net.sf.taverna.raven.spi.ArtifactFilter;
import net.sf.taverna.raven.spi.SpiRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for the SpiRegistry functionality in Raven. Exposes the
 * repository, spi name and filter list properties. Initializes the spi registry
 * before returning it, constructed instances are ready to use.
 * 
 * @author Tom Oinn
 * 
 */
public class SpiRegistryFactoryBean implements FactoryBean {

	List<ArtifactFilter> filterList = null;
	Repository repository = null;
	String spiClassName = null;
	private Log log = LogFactory.getLog(SpiRegistryFactoryBean.class);

	public Object getObject() throws Exception {
		if (repository != null && spiClassName != null) {
			SpiRegistry registry = new SpiRegistry(repository, spiClassName,
					null);
			if (filterList != null) {
				registry.setFilters(filterList);
			}
			registry.updateRegistry();
			return registry;
		}
		log
				.error("Must specify repository and spi class name for spi registry");
		throw new RuntimeException();
	}

	public void setFilterList(List<ArtifactFilter> filterList) {
		this.filterList = filterList;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setSpiClassName(String spiClassName) {
		this.spiClassName = spiClassName;
	}

	/**
	 * @return SpiRegistry.class
	 */
	@SuppressWarnings("unchecked")
	public Class getObjectType() {
		return SpiRegistry.class;
	}

	/**
	 * Singleton by default
	 */
	public boolean isSingleton() {
		return true;
	}

}
