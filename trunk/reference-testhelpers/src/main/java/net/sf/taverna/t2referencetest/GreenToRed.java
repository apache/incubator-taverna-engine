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
package net.sf.taverna.t2referencetest;

import net.sf.taverna.t2.reference.ExternalReferenceTranslatorSPI;
import net.sf.taverna.t2.reference.ReferenceContext;

public class GreenToRed implements
		ExternalReferenceTranslatorSPI<GreenReference, RedReference> {

	public RedReference createReference(GreenReference ref,
			ReferenceContext context) {
		RedReference newReference = new RedReference();
		newReference.setContents(ref.getContents());
		// Insert a two second pause to simulate reference translation and to
		// test the behaviour of multiple concurrent translations
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			System.out
					.println("Translation thread was interrupted, probably something wrong.");
		}
		return newReference;
	}

	public Class<GreenReference> getSourceReferenceType() {
		return GreenReference.class;
	}

	public Class<RedReference> getTargetReferenceType() {
		return RedReference.class;
	}

	public float getTranslationCost() {
		return 0.4f;
	}

	public boolean isEnabled(ReferenceContext arg0) {
		return true;
	}

}
