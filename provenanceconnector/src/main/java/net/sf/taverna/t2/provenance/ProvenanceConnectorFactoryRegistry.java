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
package net.sf.taverna.t2.provenance;

import net.sf.taverna.t2.spi.SPIRegistry;

/**
 * Get all the instances of {@link ProvenanceConnectorFactory} that are available using
 * this SPI Registry. Loads instances by looking at all files in
 * META-INF/services/net.sf.taverna.t2.provenance.ProvenanceConnectorFactory
 * 
 * @author Ian Dunlop
 * 
 */
public class ProvenanceConnectorFactoryRegistry extends
		SPIRegistry<ProvenanceConnectorFactory> {

	private static ProvenanceConnectorFactoryRegistry instance;

	protected ProvenanceConnectorFactoryRegistry() {
		super(ProvenanceConnectorFactory.class);
	}

	/**
	 * Get an instance of the registry that you can then get all the available
	 * {@link ProvenanceConnector} instances from
	 * 
	 * @return
	 */
	public static synchronized ProvenanceConnectorFactoryRegistry getInstance() {

		if (instance == null) {
			instance = new ProvenanceConnectorFactoryRegistry();
		}
		return instance;
	}

}
