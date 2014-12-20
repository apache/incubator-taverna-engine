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
package net.sf.taverna.t2.workflowmodel.health;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;

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
