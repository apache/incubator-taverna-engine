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
package net.sf.taverna.t2.monitor.impl;

import static org.junit.Assert.fail;

import java.util.HashSet;

import net.sf.taverna.t2.monitor.MonitorableProperty;

import org.junit.Test;

public class MonitorTreeModelTest {

	@Test
	public void testAddNodes() throws InterruptedException {
		MonitorTreeModel m = MonitorTreeModel.getInstance();
		m.registerNode(this, new String[] { "foo" },
				new HashSet<MonitorableProperty<?>>());
		m.registerNode(this, new String[] { "foo", "bar" },
				new HashSet<MonitorableProperty<?>>());
	}

	@Test
	public void testAddNodesShouldFail() {
		MonitorTreeModel m = MonitorTreeModel.getInstance();
		m.registerNode(this, new String[] { "foo" },
				new HashSet<MonitorableProperty<?>>());
		try {
			m.registerNode(this, new String[] { "bar", "wibble" },
					new HashSet<MonitorableProperty<?>>());
			fail("Should have thrown index out of bounds exception");
		} catch (IndexOutOfBoundsException ioobe) {
			// Okay, should see this
		}

	}
}
