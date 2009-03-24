/*******************************************************************************
 * Copyright (C) 2007-2009 The University of Manchester   
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

import java.util.List;

import net.sf.taverna.t2.spi.SPIRegistry;

/**
 * Registry for finding the {@link Edits} implementation.
 * 
 */
public class EditsRegistry extends SPIRegistry<Edits> {

	private static EditsRegistry instance;

	protected EditsRegistry() {
		super(Edits.class);
	}

	public static synchronized EditsRegistry getInstance() {
		if (instance == null) {
			instance = new EditsRegistry();
		}
		return instance;
	}

	public static Edits getEdits() {
		List<Edits> instances = getInstance().getInstances();
		Edits result = null;
		if (instances.size() == 0) {
			System.out.println("No Edits implementation defined");
		} else {
			if (instances.size() > 1)
				System.out
						.println("More that 1 Edits implementation defined, using the first");
			result = instances.get(0);
		}
		return result;
	}
}
