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

package org.apache.taverna.platform.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import org.apache.taverna.scufl2.api.core.Workflow;

/**
 * Unit tests for StatusReport.
 *
 * @author David Withers
 */
public class StatusReportTest {

	private StatusReport<Workflow, ?> statusReport;
	private Workflow subject;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		subject = new Workflow();
		statusReport = new StatusReport<Workflow,StatusReport<?,?>>(subject);
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#StatusReport()}.
	 */
	@Test
	public void testStatusReport() {
		Date preCreationTime = new Date();
		statusReport = new StatusReport<Workflow,StatusReport<?,?>>(null);
		assertFalse(statusReport.getCreatedDate().before(preCreationTime));
		assertFalse(statusReport.getCreatedDate().after(new Date()));
		assertEquals(State.CREATED, statusReport.getState());
		assertTrue(statusReport.getPausedDates().isEmpty());
		assertTrue(statusReport.getResumedDates().isEmpty());
		assertNull(statusReport.getCancelledDate());
		assertNull(statusReport.getCompletedDate());
		assertNull(statusReport.getFailedDate());
		assertNull(statusReport.getPausedDate());
		assertNull(statusReport.getResumedDate());
		assertNull(statusReport.getStartedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getSubject()}.
	 */
	@Test
	public void testGetSubject() {
		assertNotNull(statusReport.getSubject());
		assertEquals(subject, statusReport.getSubject());
		assertEquals(subject, statusReport.getSubject());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getState()}.
	 */
	@Test
	public void testGetState() {
		assertEquals(State.CREATED, statusReport.getState());
		assertEquals(State.CREATED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getCreatedDate()}.
	 */
	@Test
	public void testGetCreatedDate() {
		assertNotNull(statusReport.getCreatedDate());
		assertFalse(statusReport.getCreatedDate().after(new Date()));
		assertEquals(statusReport.getCreatedDate(), statusReport.getCreatedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setCreatedDate(java.util.Date)}.
	 */
	@Test
	public void testSetCreatedDate() {
		Date now = new Date();
		statusReport.setCreatedDate(now);
		assertEquals(now, statusReport.getCreatedDate());
		assertEquals(State.CREATED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getStartedDate()}.
	 */
	@Test
	public void testGetStartedDate() {
		assertNull(statusReport.getStartedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setStartedDate(java.util.Date)}.
	 */
	@Test
	public void testSetStartedDate() {
		Date now = new Date();
		statusReport.setStartedDate(now);
		assertEquals(now, statusReport.getStartedDate());
		assertEquals(State.RUNNING, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getPausedDate()}.
	 */
	@Test
	public void testGetPausedDate() {
		assertNull(statusReport.getPausedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setPausedDate(java.util.Date)}.
	 */
	@Test
	public void testSetPausedDate() {
		Date now = new Date();
		statusReport.setPausedDate(now);
		assertEquals(now, statusReport.getPausedDate());
		assertEquals(State.PAUSED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getResumedDate()}.
	 */
	@Test
	public void testGetResumedDate() {
		assertNull(statusReport.getResumedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setResumedDate(java.util.Date)}.
	 */
	@Test
	public void testSetResumedDate() {
		Date now = new Date();
		statusReport.setResumedDate(now);
		assertEquals(now, statusReport.getResumedDate());
		assertEquals(State.RUNNING, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getCancelledDate()}.
	 */
	@Test
	public void testGetCancelledDate() {
		assertNull(statusReport.getCancelledDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setCancelledDate(java.util.Date)}.
	 */
	@Test
	public void testSetCancelledDate() {
		Date now = new Date();
		statusReport.setCancelledDate(now);
		assertEquals(now, statusReport.getCancelledDate());
		assertEquals(State.CANCELLED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getCompletedDate()}.
	 */
	@Test
	public void testGetCompletedDate() {
		assertNull(statusReport.getCompletedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setCompletedDate(java.util.Date)}.
	 */
	@Test
	public void testSetCompletedDate() {
		Date now = new Date();
		statusReport.setCompletedDate(now);
		assertEquals(now, statusReport.getCompletedDate());
		assertEquals(State.COMPLETED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getFailedDate()}.
	 */
	@Test
	public void testGetFailedDate() {
		assertNull(statusReport.getFailedDate());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#setFailedDate(java.util.Date)}.
	 */
	@Test
	public void testSetFailedDate() {
		Date now = new Date();
		statusReport.setFailedDate(now);
		assertEquals(now, statusReport.getFailedDate());
		assertEquals(State.FAILED, statusReport.getState());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getPausedDates()}.
	 */
	@Test
	public void testGetPausedDates() {
		assertTrue(statusReport.getPausedDates().isEmpty());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.report.StatusReport#getResumedDates()}.
	 */
	@Test
	public void testGetResumedDates() {
		assertTrue(statusReport.getResumedDates().isEmpty());
	}

}
