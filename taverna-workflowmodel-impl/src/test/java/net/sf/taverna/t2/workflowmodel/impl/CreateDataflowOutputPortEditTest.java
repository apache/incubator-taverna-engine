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
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Withers
 *
 */
public class CreateDataflowOutputPortEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private DataflowImpl dataflow;
	private String portName;

	@Before
	public void setUp() throws Exception {
		dataflow = new DataflowImpl();
		portName = "port name";
	}

	@Test
	public void testDoEditAction() throws EditException {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(0, dataflow.getOutputPorts().size());
		edit.doEdit();
		assertEquals(1, dataflow.getOutputPorts().size());
		DataflowOutputPort outputPort = dataflow.getOutputPorts().get(0);
		assertSame(dataflow, outputPort.getDataflow());
		assertEquals(portName, outputPort.getName());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(0, dataflow.getOutputPorts().size());
		edit.doEdit();
		assertEquals(1, dataflow.getOutputPorts().size());
	}

	@Test
	public void testCreateDataflowOutputPortEdit() {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(dataflow, edit.getSubject());
	}

}
