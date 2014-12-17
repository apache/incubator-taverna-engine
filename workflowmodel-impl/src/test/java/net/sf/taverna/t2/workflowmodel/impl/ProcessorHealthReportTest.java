/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.impl;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.taverna.t2.visit.VisitKind;
import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.Visitor;
import net.sf.taverna.t2.visit.VisitReport.Status;

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
