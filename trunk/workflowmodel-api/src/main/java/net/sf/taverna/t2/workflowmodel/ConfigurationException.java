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

/**
 * Thrown when attempting to configure a
 * {@link net.sf.taverna.t2.workflowmodel.Configurable} with an invalid
 * configuration. Causes may include actual configuration errors, unavailable
 * implementations etc.
 * 
 * @author Stian Soiland-Reyes
 * @author Tom Oinn
 * 
 */
public class ConfigurationException extends Exception {

	private static final long serialVersionUID = -2841928064598107156L;

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}

}
