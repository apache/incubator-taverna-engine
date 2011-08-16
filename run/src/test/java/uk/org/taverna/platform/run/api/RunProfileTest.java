/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.run.api;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.impl.ReferenceServiceImpl;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.execution.impl.local.LocalExecutionService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 *
 *
 * @author David Withers
 */
public class RunProfileTest {

	private RunProfile runProfile;
	private WorkflowBundle workflowBundle;
	private ReferenceService referenceService;
	private ExecutionService executionService;
	private Workflow workflow, mainWorkflow;
	private Profile profile, mainProfile;
	private Map<String, T2Reference> inputs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflow = new Workflow();
		mainWorkflow = new Workflow();
		profile = new Profile();
		mainProfile = new Profile();
		workflowBundle = new WorkflowBundle();
		workflowBundle.setMainProfile(mainProfile);
		workflowBundle.setMainWorkflow(mainWorkflow);
		referenceService = new ReferenceServiceImpl();
		executionService = new LocalExecutionService();
		inputs = new HashMap<String, T2Reference>();
		runProfile = new RunProfile(workflowBundle, workflow, profile, inputs, referenceService,
				executionService);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#RunProfile(uk.org.taverna.scufl2.api.container.WorkflowBundle, net.sf.taverna.t2.reference.ReferenceService, uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleReferenceServiceExecutionService() {
		runProfile = new RunProfile(workflowBundle, referenceService, executionService);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#RunProfile(uk.org.taverna.scufl2.api.container.WorkflowBundle, java.util.Map, net.sf.taverna.t2.reference.ReferenceService, uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(workflowBundle, inputs, referenceService, executionService);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#RunProfile(uk.org.taverna.scufl2.api.container.WorkflowBundle, uk.org.taverna.scufl2.api.core.Workflow, uk.org.taverna.scufl2.api.profiles.Profile, java.util.Map, net.sf.taverna.t2.reference.ReferenceService, uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleWorkflowProfileMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(workflowBundle, workflow, profile, inputs, referenceService,
				executionService);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getWorkflowBundle()}.
	 */
	@Test
	public void testGetWorkflowBundle() {
		assertNotNull(runProfile.getWorkflowBundle());
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
		assertEquals(runProfile.getWorkflowBundle(), runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setWorkflowBundle(uk.org.taverna.scufl2.api.container.WorkflowBundle)}
	 * .
	 */
	@Test
	public void testSetWorkflowBundle() {
		runProfile.setWorkflowBundle(null);
		assertNull(runProfile.getWorkflowBundle());
		runProfile.setWorkflowBundle(workflowBundle);
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getWorkflow()}.
	 */
	@Test
	public void testGetWorkflow() {
		assertNotNull(runProfile.getWorkflow());
		assertEquals(workflow, runProfile.getWorkflow());
		assertEquals(runProfile.getWorkflow(), runProfile.getWorkflow());
		runProfile.setWorkflow(null);
		assertNotNull(runProfile.getWorkflow());
		assertEquals(mainWorkflow, runProfile.getWorkflow());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setWorkflow(uk.org.taverna.scufl2.api.core.Workflow)}
	 * .
	 */
	@Test
	public void testSetWorkflow() {
		runProfile.setWorkflow(null);
		assertNotNull(runProfile.getWorkflow());
		assertEquals(mainWorkflow, runProfile.getWorkflow());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setWorkflow(null);
		assertNull(runProfile.getWorkflow());
		runProfile.setWorkflow(workflow);
		assertEquals(workflow, runProfile.getWorkflow());
		runProfile.setWorkflow(mainWorkflow);
		assertEquals(mainWorkflow, runProfile.getWorkflow());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getProfile()}.
	 */
	@Test
	public void testGetProfile() {
		assertNotNull(runProfile.getProfile());
		assertEquals(profile, runProfile.getProfile());
		assertEquals(runProfile.getProfile(), runProfile.getProfile());
		runProfile.setProfile(null);
		assertNotNull(runProfile.getProfile());
		assertEquals(mainProfile, runProfile.getProfile());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setProfile(uk.org.taverna.scufl2.api.profiles.Profile)}
	 * .
	 */
	@Test
	public void testSetProfile() {
		runProfile.setProfile(null);
		assertNotNull(runProfile.getProfile());
		assertEquals(mainProfile, runProfile.getProfile());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setProfile(null);
		assertNull(runProfile.getProfile());
		runProfile.setProfile(profile);
		assertEquals(profile, runProfile.getProfile());
		runProfile.setProfile(mainProfile);
		assertEquals(mainProfile, runProfile.getProfile());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getInputs()}.
	 */
	@Test
	public void testGetInputs() {
		assertNotNull(runProfile.getInputs());
		assertEquals(inputs, runProfile.getInputs());
		assertEquals(runProfile.getInputs(), runProfile.getInputs());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setInputs(java.util.Map)}
	 * .
	 */
	@Test
	public void testSetInputs() {
		runProfile.setInputs(null);
		assertNull(runProfile.getInputs());
		runProfile.setInputs(inputs);
		assertEquals(inputs, runProfile.getInputs());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getReferenceService()}.
	 */
	@Test
	public void testGetReferenceService() {
		assertNotNull(runProfile.getReferenceService());
		assertEquals(referenceService, runProfile.getReferenceService());
		assertEquals(runProfile.getReferenceService(), runProfile.getReferenceService());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setReferenceService(net.sf.taverna.t2.reference.ReferenceService)}
	 * .
	 */
	@Test
	public void testSetReferenceService() {
		runProfile.setReferenceService(null);
		assertNull(runProfile.getReferenceService());
		runProfile.setReferenceService(referenceService);
		assertEquals(referenceService, runProfile.getReferenceService());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#getExecutionService()}.
	 */
	@Test
	public void testGetExecutionService() {
		assertNotNull(runProfile.getExecutionService());
		assertEquals(executionService, runProfile.getExecutionService());
		assertEquals(runProfile.getExecutionService(), runProfile.getExecutionService());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.run.api.RunProfile#setExecutionService(uk.org.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testSetExecutionService() {
		runProfile.setExecutionService(null);
		assertNull(runProfile.getExecutionService());
		runProfile.setExecutionService(executionService);
		assertEquals(executionService, runProfile.getExecutionService());
	}

}