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
package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.reference.ReferenceContext;

/**
 * A trivial implementation of ReferenceContext, used if the context parameter
 * to any service method is null.
 * 
 * @author Tom Oinn
 * 
 */
public class EmptyReferenceContext implements ReferenceContext {

	/**
	 * Return an empty entity set for all queries.
	 */
	public <T> List<T> getEntities(Class<T> arg0) {
		return new ArrayList<T>();
	}

	public void addEntity(Object entity) {
		// TODO Auto-generated method stub
	}

}
