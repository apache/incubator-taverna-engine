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

import java.util.UUID;

import net.sf.taverna.t2.reference.T2ReferenceGenerator;

/**
 * A T2ReferenceGenerator based on UUIDs. Not as fast as
 * {@link SimpleT2ReferenceGenerator}, but IDs will be globally unique.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class UUIDT2ReferenceGenerator extends AbstractT2ReferenceGenerator
		implements T2ReferenceGenerator {

	public String getNamespace() {
		return "uuid";
	}

	@Override
	protected String getNextLocalPart() {
		return UUID.randomUUID().toString();
	}

}
