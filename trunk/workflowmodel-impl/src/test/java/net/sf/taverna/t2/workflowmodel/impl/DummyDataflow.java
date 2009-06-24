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

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.FailureTransmitter;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.NamedWorkflowEntity;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;

public class DummyDataflow extends AbstractAnnotatedThing<Dataflow> implements
		Dataflow {

	public List<DataflowInputPort> inputPorts = new ArrayList<DataflowInputPort>();
	public List<DataflowOutputPort> outputPorts = new ArrayList<DataflowOutputPort>();
	public List<Processor> processors = new ArrayList<Processor>();
	public List<Merge> merges = new ArrayList<Merge>();

	public DataflowValidationReport checkValidity() {
		return new DummyValidationReport(true);
	}

	public <T extends NamedWorkflowEntity> List<? extends T> getEntities(
			Class<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends DataflowInputPort> getInputPorts() {
		return inputPorts;
	}

	public List<? extends Datalink> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends DataflowOutputPort> getOutputPorts() {
		return outputPorts;
	}

	public List<? extends Processor> getProcessors() {
		return processors;
	}

	public List<? extends Merge> getMerges() {
		return merges;
	}

	public String getLocalName() {
		return "test_dataflow";
	}

	public void fire(String owningProcess, InvocationContext context) {
		String newOwningProcess = owningProcess + ":" + getLocalName();
		for (Processor p : processors) {
			if (p.getInputPorts().isEmpty()) {
				p.fire(newOwningProcess, context);
			}
		}
	}

	public FailureTransmitter getFailureTransmitter() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean doTypeCheck() throws IterationTypeMismatchException {
		throw new UnsupportedOperationException(
				"Not implemented for this class");
	}

	public String getInternalIdentier() {
		return "an id";
	}

}
