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
package net.sf.taverna.t2.workflowmodel.health;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.workflowmodel.health.HealthCheckerFactory;

import org.junit.Before;
import org.junit.Test;

public class HealthCheckerFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetHealthCheckerForObject() {
		String str = "A String";
		List<HealthChecker<?>> checkers = HealthCheckerFactory.getInstance().getHealthCheckersForObject(str);
		assertEquals("There should be 1 checker for String",1,checkers.size());
		
		Long l = new Long(123);
		checkers = HealthCheckerFactory.getInstance().getHealthCheckersForObject(l);
		assertEquals("There should be 0 checkers for Long",0,checkers.size());
		
		Float f = new Float(2.5f);
		checkers = HealthCheckerFactory.getInstance().getHealthCheckersForObject(f);
		assertEquals("There should be 2 checkers for Float",2,checkers.size());
	}

}
