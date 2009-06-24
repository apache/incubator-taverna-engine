/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel;

import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;

/**
 * Interface for workflow items that can be configured from a bean.
 * 
 * @param <ConfigurationType>
 *            the ConfigurationType associated with the workflow item. This is
 *            an arbitrary java class that provides details on how the item is
 *            configured. To allow successful serialisation it's recommended to
 *            keep this configuration as a simple Java bean.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * @see ActivityConfigurationException
 * 
 */
public interface Configurable<ConfigurationType> {

	/**
	 * Each item stores configuration within a bean of type ConfigurationType,
	 * this method returns the configuration. This is used by the automatic
	 * serialisation framework to store the item definition in the workflow XML.
	 */
	public abstract ConfigurationType getConfiguration();

	/**
	 * When the item is built from the workflow definition XML the object is
	 * first constructed with a default constructor then this method is called,
	 * passing in the configuration bean returned by getConfiguration().
	 * 
	 * @throws ConfigurationException
	 *             if a problem occurs when configuring the item
	 */
	public abstract void configure(ConfigurationType conf)
			throws ConfigurationException;

}