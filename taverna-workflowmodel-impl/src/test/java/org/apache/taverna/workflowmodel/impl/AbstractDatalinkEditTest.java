/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.taverna.annotation.AnnotationChain;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;

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
		};
		edit.doEdit();
		edit.doEdit();
	}

	@Test(expected = RuntimeException.class)
	public void testDoEditWithWrongImpl() throws EditException {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(new Datalink() {

			@Override
			public int getResolvedDepth() {
				return 0;
			}

			@Override
			public EventHandlingInputPort getSink() {
				return null;
			}

			@Override
			public EventForwardingOutputPort getSource() {
				return null;
			}

			@Override
			public Edit<? extends Datalink> getAddAnnotationEdit(
					AnnotationChain newAnnotation) {
				return null;
			}

			@Override
			public Set<? extends AnnotationChain> getAnnotations() {
				return null;
			}

			@Override
			public Edit<? extends Datalink> getRemoveAnnotationEdit(
					AnnotationChain annotationToRemove) {
				return null;
			}

			@Override
			public void setAnnotations(Set<AnnotationChain> annotations) {
			}

		}) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
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
		};
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(edit.isApplied());		
	}

	@Test(expected = RuntimeException.class)
	public void testUndoBeforeDoEdit() {
		AbstractDatalinkEdit edit = new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink)
					throws EditException {
			}
		};
		edit.undo();
	}

}
