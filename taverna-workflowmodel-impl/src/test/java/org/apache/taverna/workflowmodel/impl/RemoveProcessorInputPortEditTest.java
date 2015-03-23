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

import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class RemoveProcessorInputPortEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private Processor processor;
	private ProcessorInputPort inputPort;
	private Edit<Processor> removeProcessorInputPortEdit;
	
	@Before
	public void setup() throws Exception {
		processor = edits.createProcessor("test");
		inputPort = edits.createProcessorInputPort(processor, "port", 1);
		edits.getAddProcessorInputPortEdit(processor, inputPort).doEdit();
		removeProcessorInputPortEdit = edits.getRemoveProcessorInputPortEdit(processor,inputPort);
	}
	
	@Test
	public void testDoEdit() throws Exception {
		assertFalse(removeProcessorInputPortEdit.isApplied());
		Processor p = removeProcessorInputPortEdit.doEdit();
		assertTrue(removeProcessorInputPortEdit.isApplied());
		assertSame(p,processor);
		assertEquals(0,processor.getInputPorts().size());
	}
		
	@Test
	public void testSubject() throws Exception {
		assertSame(processor,removeProcessorInputPortEdit.getSubject());
		removeProcessorInputPortEdit.doEdit();
		assertSame(processor,removeProcessorInputPortEdit.getSubject());		
	}
}
