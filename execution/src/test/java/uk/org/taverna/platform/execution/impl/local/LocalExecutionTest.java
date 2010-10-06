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
package uk.org.taverna.platform.execution.impl.local;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.platform.report.WorkflowReport;

/**
 * 
 * @author David Withers
 */
public class LocalExecutionTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#start()}.
	 */
	@Test
	public void testStart() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#pause()}.
	 */
	@Test
	public void testPause() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#resume()}.
	 */
	@Test
	public void testResume() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#cancel()}.
	 */
	@Test
	public void testCancel() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#DataflowExecution(uk.org.taverna.scufl2.api.core.Workflow, java.util.Map, net.sf.taverna.t2.reference.ReferenceService)}
	 * .
	 * @throws Exception 
	 */
	@Test
	public void testDataflowExecution() throws Exception {
//		URL wfResource = getClass().getResource("/t2flow/in-out.t2flow");
//		assertNotNull(wfResource);
//		TavernaResearchObject researchObject = new T2FlowParser().parseT2Flow(wfResource.openStream());
//		Workflow workflow = researchObject.getMainWorkflow();
//		Profile profile = researchObject.getProfiles().iterator().next();
//
//		T2Reference reference = context.getReferenceService().register("test-input", 0, true, context);
//		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
//		inputs.put("in", reference);
//			
//		DataflowExecution execution = new DataflowExecution(workflow, profile, inputs, context.getReferenceService());
//		WorkflowReport report = execution.getWorkflowReport();
//		assertEquals(State.CREATED, report.getState());
//		execution.start();
//		
//		Map<String, Object> results = execution.getResults();		
//		waitForResult(results, "out", report);
//		
//		String result = (String) context.getReferenceService().renderIdentifier((T2Reference) results.get("out"), String.class, context);
//		assertEquals("test-input", result);
//		assertEquals(State.COMPLETED, report.getState());
//		System.out.println(report);
	}

	@Test
	public void testDataflowExecution2() throws Exception {
//		URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
//		assertNotNull(wfResource);
//		T2FlowParser t2FlowParser = new T2FlowParser();
//		t2FlowParser.setStrict(true);
//		TavernaResearchObject researchObject = t2FlowParser.parseT2Flow(wfResource.openStream());
//		Workflow workflow = researchObject.getMainWorkflow();
//		Profile profile = researchObject.getProfiles().iterator().next();
//
//		T2Reference reference = context.getReferenceService().register("test-input", 0, true, context);
//		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
//		inputs.put("in", reference);
//				
//		DataflowExecution execution = new DataflowExecution(workflow, profile, inputs, context.getReferenceService());
//		WorkflowReport report = execution.getWorkflowReport();
//		System.out.println(report);
//		assertEquals(State.CREATED, report.getState());
//		execution.start();
//		System.out.println(report);
//		
//		Map<String, Object> results = execution.getResults();		
//		waitForResult(results, "out", report);
//		
//		List<String> result = (List<String>) context.getReferenceService().renderIdentifier((T2Reference) results.get("out"), String.class, context);
//		assertEquals(1000, result.size());
//		assertEquals("test-input:0", result.get(0));
//		assertEquals(State.COMPLETED, report.getState());
//		System.out.println(report);
	}

	private void waitForResult(Map<String, Object> results, String port, WorkflowReport report) throws InterruptedException {
		int wait = 0;
		while (!results.containsKey(port) && wait++ < 10) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.execution.impl.local.LocalExecution#resultTokenProduced(net.sf.taverna.t2.invocation.WorkflowDataToken, java.lang.String)}
	 * .
	 */
	@Test
	public void testResultTokenProduced() {
//		fail("Not yet implemented");
	}

}
