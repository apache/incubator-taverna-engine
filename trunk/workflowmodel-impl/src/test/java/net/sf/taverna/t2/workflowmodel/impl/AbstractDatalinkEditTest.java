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
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;

import org.junit.Before;
import org.junit.Test;

/**
 * @author David Withers
 * 
 */
public class AbstractDatalinkEditTest {

	private Datalink datalink;
	private boolean editDone;

	@Before
	public void setUp() throws Exception {
		datalink = new DatalinkImpl(null, null);
		editDone = false;
	}

	@Test
	public void testAbstractDatalinkEdit() {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		assertEquals(datalink, edit.getSubject());
	}

	@Test(expected = RuntimeException.class)
	public void testAbstractDatalinkEditWithNull() {
		new AbstractDatalinkEdit(null) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
	}

	@Test
	public void testDoEdit() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
				editDone = true;
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		assertFalse(editDone);
		assertFalse(edit.isApplied());
		assertEquals(datalink, edit.doEdit());
		assertTrue(editDone);
		assertTrue(edit.isApplied());
	}

	@Test(expected = EditException.class)
	public void testDoEditTwice() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		edit.doEdit();
		edit.doEdit();
	}

	@Test(expected = EditException.class)
	public void testDoEditWithWrongImpl() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(new Datalink() {

			public int getResolvedDepth() {
				return 0;
			}

			public EventHandlingInputPort getSink() {
				return null;
			}

			public EventForwardingOutputPort getSource() {
				return null;
			}

			public Edit<? extends Datalink> getAddAnnotationEdit(
					AnnotationChain newAnnotation) {
				// TODO Auto-generated method stub
				return null;
			}

			public Set<? extends AnnotationChain> getAnnotations() {
				// TODO Auto-generated method stub
				return null;
			}

			public Edit<? extends Datalink> getRemoveAnnotationEdit(
					AnnotationChain annotationToRemove) {
				// TODO Auto-generated method stub
				return null;
			}

			public void setAnnotations(Set<AnnotationChain> annotations) {
				// TODO Auto-generated method stub
				
			}

		}) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		edit.doEdit();
	}

	@Test
	public void testGetSubject() {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		assertEquals(datalink, edit.getSubject());
	}

	@Test
	public void testIsApplied() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(edit.isApplied());
		edit.undo();
		assertFalse(edit.isApplied());
	}

	@Test
	public void testUndo() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
				editDone = true;
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
				editDone = false;
			}
		};
		assertFalse(editDone);
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(editDone);
		assertTrue(edit.isApplied());
		edit.undo();
		assertFalse(editDone);
		assertFalse(edit.isApplied());
	}

	@Test(expected = RuntimeException.class)
	public void testUndoBeforeDoEdit() {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}

			@Override
			protected void undoEditAction(DatalinkImpl datalink) {
			}
		};
		edit.undo();
	}

}
