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
import static org.junit.Assert.assertSame;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.EditException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author David Withers
 *
 */
public class CreateDataflowInputPortEditTest {

	private DataflowImpl dataflow;
	
	private String portName;

	private int portDepth;
	
	private int portGranularDepth;

	@Before
	public void setUp() throws Exception {
		dataflow = new DataflowImpl();
		portName = "port name";
		portDepth = 3;
		portGranularDepth = 2;
	}

	@Test
	public void testDoEditAction() throws EditException {
		CreateDataflowInputPortEdit edit = new CreateDataflowInputPortEdit(dataflow, portName, portDepth, portGranularDepth);
		assertEquals(0, dataflow.getInputPorts().size());
		edit.doEditAction(dataflow);
		assertEquals(1, dataflow.getInputPorts().size());
		DataflowInputPort inputPort = dataflow.getInputPorts().get(0);
		assertSame(dataflow, inputPort.getDataflow());
		assertEquals(portName, inputPort.getName());
		assertEquals(portDepth, inputPort.getDepth());
		assertEquals(portGranularDepth, inputPort.getGranularInputDepth());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		CreateDataflowInputPortEdit edit = new CreateDataflowInputPortEdit(dataflow, portName, portDepth, portGranularDepth);
		assertEquals(0, dataflow.getInputPorts().size());
		edit.doEditAction(dataflow);
		edit.undoEditAction(dataflow);
		assertEquals(0, dataflow.getInputPorts().size());
	}

	@Test
	public void testCreateDataflowInputPortEdit() {
		CreateDataflowInputPortEdit edit = new CreateDataflowInputPortEdit(dataflow, portName, portDepth, portGranularDepth);
		assertEquals(dataflow, edit.getSubject());
	}

}
