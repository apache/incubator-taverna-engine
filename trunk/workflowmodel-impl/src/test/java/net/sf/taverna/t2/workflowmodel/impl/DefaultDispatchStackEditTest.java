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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DefaultDispatchStackEditTest {
	
	private ProcessorImpl processor;
	private DefaultDispatchStackEdit defaultDispatchStackEdit;

	@Before
	public void setup() {
		processor = new ProcessorImpl();
		defaultDispatchStackEdit = new DefaultDispatchStackEdit(processor);
	}
	@Test
	public void testEdit() throws Exception {
		assertEquals(0,processor.getDispatchStack().getLayers().size());
		defaultDispatchStackEdit.doEdit();
		assertTrue(processor.getDispatchStack().getLayers().size()>0);
	}
	
	@Test
	public void testUndo() throws Exception {
		defaultDispatchStackEdit.doEdit();
		assertTrue(processor.getDispatchStack().getLayers().size()>0);
		defaultDispatchStackEdit.undo();
		assertEquals(0,processor.getDispatchStack().getLayers().size());
	}
	
	@Test
	public void testSubject() throws Exception {
		assertSame(processor,defaultDispatchStackEdit.getSubject());
		defaultDispatchStackEdit.doEdit();
		assertSame(processor,defaultDispatchStackEdit.getSubject());
		defaultDispatchStackEdit.undo();
		assertSame(processor,defaultDispatchStackEdit.getSubject());
		defaultDispatchStackEdit.doEdit();
		assertSame(processor,defaultDispatchStackEdit.getSubject());
	}
	
	@Test
	public void testApplied() throws Exception {
		assertFalse(defaultDispatchStackEdit.isApplied());
		defaultDispatchStackEdit.doEdit();
		assertTrue(defaultDispatchStackEdit.isApplied());
		defaultDispatchStackEdit.undo();
		assertFalse(defaultDispatchStackEdit.isApplied());
		defaultDispatchStackEdit.doEdit();
		assertTrue(defaultDispatchStackEdit.isApplied());
	}
	
}
