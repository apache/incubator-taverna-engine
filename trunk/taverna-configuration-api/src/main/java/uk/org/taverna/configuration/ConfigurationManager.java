/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.configuration;

/**
 * Handles the configuration for a {@link Configurable} object
 *
 * @author David Withers
 */
public interface ConfigurationManager {

	/**
	 * Write out the properties configuration to disk based on the UUID of the
	 * {@link Configurable}
	 * <br>
	 * Default values are not stored within the file, but only those that have been changed or deleted.
	 *
	 * @param configurable
	 * @throws Exception
	 */
	public void store(Configurable configurable) throws Exception;

	/**
	 * Loads the configuration details from disk or from memory and populates the provided Configurable
	 *
	 * @param configurable
	 * @return
	 * @throws Exception
	 *             if there are no configuration details available
	 */
	public void populate(Configurable configurable) throws Exception;

}