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

package org.apache.taverna.workflowmodel.health;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.visit.VisitReport.Status;

import org.junit.Before;
import org.junit.Test;

public class HealthReportTest {

	VisitReport report;
	
	@Before
	public void setUp() throws Exception {
		List<VisitReport> subreports = new ArrayList<>();
		subreports.add(new VisitReport(DummyVisitKind.getInstance(), "sub subject","this is a subreport",0,Status.OK));
		report = new VisitReport(DummyVisitKind.getInstance(), "a subject","a message",0, Status.WARNING,subreports);
	}

	@Test
	public void testActivityVisitReportStringStatus() {
		report = new VisitReport(DummyVisitKind.getInstance(), "the subject","a string",0, Status.SEVERE);
		assertEquals("a string",report.getMessage());
		assertEquals(Status.SEVERE,report.getStatus());
		assertEquals("the subject",report.getSubject());
		assertEquals("the subreports should be an empty list",0,report.getSubReports().size());
	}

	@Test
	public void testGetMessage() {
		assertEquals("a message",report.getMessage());
	}

	@Test
	public void testGetStatus() {
		assertEquals(Status.WARNING,report.getStatus());
	}
	
	@Test
	public void testGetSubject() {
		assertEquals("a subject",report.getSubject());
	}
	
	@Test
	public void testGetSubreports() {
		Collection<VisitReport> subreports = report.getSubReports();
		assertEquals("There should be 1 report",1,subreports.size());
		assertEquals("Wrong subject","sub subject",subreports.iterator().next().getSubject());
	}
	
	@Test 
	public void testStatusHighestIncludingSubReports() {
		report = new VisitReport(DummyVisitKind.getInstance(), "parent","set to ok",0, Status.OK);
		assertEquals("should be OK",Status.OK,report.getStatus());
		report.getSubReports().add(new VisitReport(DummyVisitKind.getInstance(), "child1","set to warning",0, Status.WARNING));
		assertEquals("should be WARNING",Status.WARNING,report.getStatus());
		report.getSubReports().add(new VisitReport(DummyVisitKind.getInstance(), "child1","set to severe",0, Status.SEVERE));
		assertEquals("should be SEVERE",Status.SEVERE,report.getStatus());
	}

}
