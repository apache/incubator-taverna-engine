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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.Processor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultDispatchStackEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private Processor processor;
	private Edit<Processor> defaultDispatchStackEdit;

	@Before
	public void setup() {
		processor = edits.createProcessor("");
		defaultDispatchStackEdit = edits.getDefaultDispatchStackEdit(processor);
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
	}
	
	@Test
	public void testSubject() throws Exception {
		assertSame(processor,defaultDispatchStackEdit.getSubject());
		defaultDispatchStackEdit.doEdit();
		assertSame(processor,defaultDispatchStackEdit.getSubject());		
	}
	
	@Test
	public void testApplied() throws Exception {
		assertFalse(defaultDispatchStackEdit.isApplied());
		defaultDispatchStackEdit.doEdit();
		assertTrue(defaultDispatchStackEdit.isApplied());		
	}
	
}
