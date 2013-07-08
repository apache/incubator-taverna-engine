/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package net.sf.taverna.t2.activities.dataflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author David Withers
 */
public class DataflowActivityFactoryTest {

	private DataflowActivityFactory factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		factory = new DataflowActivityFactory();
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.activities.beanshell.BeanshellActivityFactory#createActivity()}.
	 */
	@Test
	public void testCreateActivity() {
		DataflowActivity createActivity = factory.createActivity();
		assertNotNull(createActivity);
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.activities.beanshell.BeanshellActivityFactory#getActivityType()}.
	 */
	@Test
	public void testGetActivityURI() {
		assertEquals(URI.create(DataflowActivity.URI), factory.getActivityType());
	}

}
