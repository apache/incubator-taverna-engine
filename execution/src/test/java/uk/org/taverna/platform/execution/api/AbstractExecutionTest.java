/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.execution.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.impl.ReferenceServiceImpl;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * @author David Withers
 */
public class AbstractExecutionTest {
	
	private WorkflowBundle workflowBundle;

	private Execution execution;
	
	private Workflow workflow;

	private Profile profile;
	
	private Map<String, T2Reference> inputs;
	
	private ReferenceService referenceService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflowBundle = new WorkflowBundle();
		workflow = new Workflow();
		profile = new Profile();
		inputs = new HashMap<String, T2Reference>();
		referenceService = new ReferenceServiceImpl();
		execution = new AbstractExecution(workflowBundle, workflow, profile, inputs, referenceService) {
			public void start() {}
			public void resume() {}
			public void pause() {}
			public void cancel() {}
			protected WorkflowReport createWorkflowReport(Workflow workflow) {
				return new WorkflowReport(workflow) {
					protected ProcessorReport createProcessorReport(Processor processor,
							WorkflowReport parentReport) {
						return null;
					}
				};
			}
		};
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getID()}.
	 */
	@Test
	public void testGetID() {
		assertNotNull(execution.getID());
		assertEquals(execution.getID(), execution.getID());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getWorkflowBundle()}.
	 */
	@Test
	public void testGetWorkflowBundle() {
		assertEquals(workflowBundle, execution.getWorkflowBundle());
	}
	
	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getWorkflow()}.
	 */
	@Test
	public void testGetWorkflow() {
		assertEquals(workflow, execution.getWorkflow());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getInputs()}.
	 */
	@Test
	public void testGetInputs() {
		assertEquals(inputs, execution.getInputs());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getReferenceService()}.
	 */
	@Test
	public void testGetReferenceService() {
		assertEquals(referenceService, execution.getReferenceService());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.execution.api.AbstractExecution#getWorkflowReport()}.
	 */
	@Test
	public void testGetWorkflowReport() {
		assertNotNull(execution.getWorkflowReport());
	}

}
