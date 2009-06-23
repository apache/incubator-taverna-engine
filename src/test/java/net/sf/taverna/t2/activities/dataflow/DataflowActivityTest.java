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
package net.sf.taverna.t2.activities.dataflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Dataflow Activity Tests
 * 
 * @author David Withers
 * 
 */
public class DataflowActivityTest {

	private Dataflow dataflow;

	private DataflowActivity activity;

	@Before
	public void setUp() throws Exception {
		activity = new DataflowActivity();
		Edits edits = EditsRegistry.getEdits();
		dataflow = edits.createDataflow();
		edits.getCreateDataflowInputPortEdit(dataflow, "input", 0, 0).doEdit();
		edits.getCreateDataflowOutputPortEdit(dataflow, "output").doEdit();
		Datalink datalink = edits.createDatalink(dataflow.getInputPorts().get(0).getInternalOutputPort(), 
				dataflow.getOutputPorts().get(0).getInternalInputPort());
		edits.getConnectDatalinkEdit(datalink).doEdit();
	}

	@Test
	public void testConfigureDataflowActivityConfigurationBean()
			throws Exception {
		activity.configure(dataflow);
		assertEquals(dataflow, activity.getConfiguration());
		assertEquals(1, activity.getInputPorts().size());
		assertEquals("input", activity.getInputPorts().iterator().next()
				.getName());
		assertEquals(1, activity.getOutputPorts().size());
		assertEquals("output", activity.getOutputPorts().iterator().next()
				.getName());
	}

	@Test
	public void testGetConfiguration() {
		assertNull("freshly created activity should not contain configuration",activity.getConfiguration());
	}

}
