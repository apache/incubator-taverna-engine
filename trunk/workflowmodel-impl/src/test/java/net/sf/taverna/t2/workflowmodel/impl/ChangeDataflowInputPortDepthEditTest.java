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

import static org.junit.Assert.assertEquals;
import net.sf.taverna.t2.workflowmodel.EditException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author David Withers
 *
 */
public class ChangeDataflowInputPortDepthEditTest {

	private DataflowInputPortImpl dataflowInputPort;
	
	private int depth;
	
	private int granularDepth;
	
	@Before
	public void setUp() throws Exception {
		depth = 3;
		granularDepth = 1;
		dataflowInputPort = new DataflowInputPortImpl("port name", depth, granularDepth, null);
	}

	@Test
	public void testDoEditAction() throws EditException {
		int newDepth = 2;
		ChangeDataflowInputPortDepthEdit edit = new ChangeDataflowInputPortDepthEdit(dataflowInputPort, newDepth);
		assertEquals(depth, dataflowInputPort.getDepth());
		assertEquals(granularDepth, dataflowInputPort.getGranularInputDepth());		
		edit.doEditAction(dataflowInputPort);
		assertEquals(newDepth, dataflowInputPort.getDepth());
		assertEquals(granularDepth, dataflowInputPort.getGranularInputDepth());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		int newDepth = 2;
		ChangeDataflowInputPortDepthEdit edit = new ChangeDataflowInputPortDepthEdit(dataflowInputPort, newDepth);
		assertEquals(depth, dataflowInputPort.getDepth());
		assertEquals(granularDepth, dataflowInputPort.getGranularInputDepth());		
		edit.doEditAction(dataflowInputPort);
		edit.undoEditAction(dataflowInputPort);
		assertEquals(depth, dataflowInputPort.getDepth());
		assertEquals(granularDepth, dataflowInputPort.getGranularInputDepth());
	}

	@Test
	public void testCreateDataflowInputPortEdit() {
		ChangeDataflowInputPortDepthEdit edit = new ChangeDataflowInputPortDepthEdit(dataflowInputPort, 0);
		assertEquals(dataflowInputPort, edit.getSubject());
	}

}
