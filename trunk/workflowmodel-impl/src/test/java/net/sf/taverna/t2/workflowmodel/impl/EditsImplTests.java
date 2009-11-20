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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.processor.AsynchEchoActivity;
import net.sf.taverna.t2.workflowmodel.processor.EchoConfig;

import org.junit.BeforeClass;
import org.junit.Test;

public class EditsImplTests {
	public class InvalidDummyDataflow extends DummyDataflow {
		@Override
		public DataflowValidationReport checkValidity() {
			return new DummyValidationReport(false);
		}
	}

	private static Edits edits;
	
	@BeforeClass
	public static void createEditsInstance() {
		edits=new EditsImpl();
	}
	
	@Test
	public void createDataflow() {
		Dataflow df = edits.createDataflow();
		assertNotNull(df.getInternalIdentier());
	}
	
	@Test
	public void testGetConfigureActivityEdit() {
		Edit<?> edit = edits.getConfigureActivityEdit(new AsynchEchoActivity(), new EchoConfig());
		assertTrue(edit instanceof ConfigureActivityEdit);
	}
}
