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
package net.sf.taverna.t2.reference;

import static net.sf.taverna.t2.reference.ReferencedDataNature.*;

/**
 * A trivial implementation of ExternalReference. This abstract class should be
 * used as the superclass of any ExternalReference implementations as it
 * provides base metadata for the hibernate-based persistence system used by the
 * main reference manager implementation. While the interface contract cannot
 * require this your extensions will likely not work properly unless you use
 * this class.
 * 
 * @author Tom Oinn
 */
public abstract class AbstractExternalReference implements ExternalReferenceSPI {

	// Used internally by Hibernate for this class and subclasses
	private int primaryKey;

	/**
	 * Used by Hibernate internally to establish a foreign key relationship
	 * between this abstract superclass and tables corresponding to
	 * implementations of the ExternalReference interface. Has no impact on any
	 * application level code, this method is only ever used by the internals of
	 * the hibernate framework.
	 */
	public final void setPrimaryKey(int newKey) {
		this.primaryKey = newKey;
	}

	/**
	 * Used by Hibernate internally to establish a foreign key relationship
	 * between this abstract superclass and tables corresponding to
	 * implementations of the ExternalReference interface. Has no impact on any
	 * application level code, this method is only ever used by the internals of
	 * the hibernate framework.
	 */
	public final int getPrimaryKey() {
		return this.primaryKey;
	}

	/**
	 * Default to returning DataReferenceNature.UNKNOWN
	 */
	public ReferencedDataNature getDataNature() {
		return UNKNOWN;
	}

	/**
	 * Default to returning null for charset
	 */
	public String getCharset() {
		return null;
	}

	/**
	 * Default to a value of 0.0f for the resolution cost, but implementations
	 * should at least attempt to set this to a more sensible level!
	 */
	public float getResolutionCost() {
		return 0.0f;
	}
}
