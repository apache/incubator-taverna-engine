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
//import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.taverna.visit.VisitKind;
import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.visit.Visitor;
import org.apache.taverna.visit.VisitReport.Status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ProcessorHealthReportTest {

	List<VisitReport> activityReports;
	VisitReport report;
	
	@Before
	public void setUp() throws Exception {
		activityReports = new ArrayList<VisitReport>();
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport (DummyKind.getInstance(), "","",0, Status.OK));
		
		report = new VisitReport (DummyKind.getInstance(), "processor subject", "", 0, activityReports);
	}

	@Test
	public void testProcessorHealthReportImpl() {
		assertEquals("There should be 3 activity reports",3,report.getSubReports().size());
	}

	@Test
	public void testGetActivityHealthReports() {
		Collection<VisitReport> subReports = report.getSubReports();
		assertEquals("There should be 3 activity reports",3,subReports.size());
		assert(subReports.containsAll(activityReports));
	}

	@Ignore("Not yet implented, flagged as Ignore to allow commit")
	@Test
	public void testGetMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStatusAllOK() {
		List<VisitReport> activityReports = new ArrayList<VisitReport>();
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		
		VisitReport report = new VisitReport(DummyKind.getInstance(), "processor subject","", 0, activityReports);
		
		assertEquals("the status should be OK",Status.OK,report.getStatus());
	}
	
	@Test
	public void testGetStatusContainsWarning() {
		List<VisitReport> activityReports = new ArrayList<VisitReport>();
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.WARNING));
		
		VisitReport report = new VisitReport(DummyKind.getInstance(), "processor subject","", 0, activityReports);
		
		assertEquals("the status should be WARNING",Status.WARNING,report.getStatus());
	}
	
	@Test
	public void testGetStatusContainsSevere() {
		List<VisitReport> activityReports = new ArrayList<VisitReport>();
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.SEVERE));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.OK));
		
		VisitReport report = new VisitReport(DummyKind.getInstance(), "processor subject","", 0, activityReports);
		
		assertEquals("the status should be SEVERE",Status.SEVERE,report.getStatus());
	}
	
	@Test
	public void testGetStatusAllSevere() {
		List<VisitReport> activityReports = new ArrayList<VisitReport>();
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.SEVERE));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.SEVERE));
		activityReports.add(new VisitReport(DummyKind.getInstance(), "","",0, Status.SEVERE));
		
		VisitReport report = new VisitReport(DummyKind.getInstance(), "processor subject","", 0, activityReports);
		
		assertEquals("the status should be SEVERE",Status.SEVERE,report.getStatus());
	}
	
	@Test
	public void testGetSubject() {
		assertEquals("processor subject",report.getSubject());
	}
	
	private static class DummyKind extends VisitKind {
		@Override
		public Class<Visitor<Object>> getVisitorClass() {
			return null;
		}

		private static class Singleton {
			private static DummyKind instance = new DummyKind();
		}
		
		public static DummyKind getInstance() {
			return Singleton.instance;
		}
	}
}
