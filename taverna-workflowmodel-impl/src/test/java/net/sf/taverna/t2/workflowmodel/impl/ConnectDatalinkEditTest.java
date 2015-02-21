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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Withers
 * 
 */
public class ConnectDatalinkEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private DatalinkImpl datalink;
	private EventForwardingOutputPort source;
	private EventHandlingInputPort sink;

	@Before
	public void setUp() throws Exception {
		source = new BasicEventForwardingOutputPort("output", 0, 0);
		sink = new DataflowInputPortImpl("input", 0, 0, null);
		datalink = new DatalinkImpl(source, sink);
	}

	@Test
	public void testDoEditAction() throws EditException {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(0, datalink.getSource().getOutgoingLinks().size());
		assertNull(datalink.getSink().getIncomingLink());
		edit.doEdit();
		assertEquals(1, datalink.getSource().getOutgoingLinks().size());
		assertEquals(datalink, datalink.getSource().getOutgoingLinks()
				.iterator().next());
		assertEquals(datalink, datalink.getSink().getIncomingLink());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(0, datalink.getSource().getOutgoingLinks().size());
		assertNull(datalink.getSink().getIncomingLink());
		edit.doEdit();
		assertEquals(1, datalink.getSource().getOutgoingLinks().size());
		assertNotNull(datalink.getSink().getIncomingLink());
	}

	@Test
	public void testConnectDatalinkEdit() {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(datalink, edit.getSubject());
	}

}
