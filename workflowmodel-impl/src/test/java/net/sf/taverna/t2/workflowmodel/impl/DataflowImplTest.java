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
package net.sf.taverna.t2.workflowmodel.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DataflowImplTest {
	DataflowImpl df = new DataflowImpl();
	
	@Test
	public void testInternalIdentifer() {
		assertNotNull("the identifier should be created at construction time",df.getInternalIdentier());
	}
	
	@Test
	public void testRefreshInternalIndentifier() {
		String oldId=df.getInternalIdentier();
		df.refreshInternalIdentifier();
		assertNotNull("the new identifier should not be null",df.getInternalIdentier());
		assertFalse("the identifier should have changed",oldId.equals(df.getInternalIdentier()));
	}
}
