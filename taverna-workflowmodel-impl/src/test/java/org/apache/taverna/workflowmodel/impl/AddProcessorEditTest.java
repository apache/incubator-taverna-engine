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
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.NamingException;
import org.apache.taverna.workflowmodel.Processor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AddProcessorEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private Processor processor;
	
	@Before
	public void createProcessor() {
		processor = edits.createProcessor("the_processor");
	}
	
	@Test
	public void testAddingOfProcessor() throws Exception {
		Dataflow f = edits.createDataflow();
		
		Edit<Dataflow> edit = edits.getAddProcessorEdit(f,processor);
		edit.doEdit();
		
		assertEquals(1,f.getProcessors().size());
		assertEquals(processor,f.getProcessors().get(0));
	}
	
	@Test(expected=EditException.class)
	public void testCantEditTwice() throws Exception {
		Dataflow f = new DataflowImpl();
		Edit<Dataflow> edit = edits.getAddProcessorEdit(f,processor);
		edit.doEdit();
		edit.doEdit();
	}
	
	@Test(expected=NamingException.class)
	public void testDuplicateName() throws Exception {
		Dataflow f = new DataflowImpl();
		Edit<Dataflow> edit = edits.getAddProcessorEdit(f,processor);
		edit.doEdit();
		
		ProcessorImpl processor2=new ProcessorImpl();
		processor2.setName(processor.getLocalName());
		Edit<Dataflow> edit2 = edits.getAddProcessorEdit(f,processor);
		edit2.doEdit();
	}
	
	@Test
	public void testThroughEditsImpl() throws Exception {
		//Essentially the same as testAddingOfProcessor, but a sanity test that it works correctly through the Edits API
		Dataflow f = new DataflowImpl();
		new EditsImpl().getAddProcessorEdit(f, processor).doEdit();
		
		assertEquals(1,f.getProcessors().size());
		assertEquals(processor,f.getProcessors().get(0));
	}	
}
