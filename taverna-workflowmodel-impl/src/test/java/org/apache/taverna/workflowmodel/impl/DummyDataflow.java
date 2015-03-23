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

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.FailureTransmitter;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.NamedWorkflowEntity;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;

public class DummyDataflow extends AbstractAnnotatedThing<Dataflow> implements
		Dataflow {

	public List<DataflowInputPort> inputPorts = new ArrayList<DataflowInputPort>();
	public List<DataflowOutputPort> outputPorts = new ArrayList<DataflowOutputPort>();
	public List<Processor> processors = new ArrayList<Processor>();
	public List<Merge> merges = new ArrayList<Merge>();

	@Override
	public DataflowValidationReport checkValidity() {
		return new DummyValidationReport(true);
	}

	@Override
	public <T extends NamedWorkflowEntity> List<? extends T> getEntities(
			Class<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DataflowInputPort> getInputPorts() {
		return inputPorts;
	}

	@Override
	public List<? extends Datalink> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends DataflowOutputPort> getOutputPorts() {
		return outputPorts;
	}

	@Override
	public List<? extends Processor> getProcessors() {
		return processors;
	}

	@Override
	public List<? extends Merge> getMerges() {
		return merges;
	}

	@Override
	public String getLocalName() {
		return "test_dataflow";
	}

	@Override
	public void fire(String owningProcess, InvocationContext context) {
		String newOwningProcess = owningProcess + ":" + getLocalName();
		for (Processor p : processors) {
			if (p.getInputPorts().isEmpty()) {
				p.fire(newOwningProcess, context);
			}
		}
	}

	@Override
	public FailureTransmitter getFailureTransmitter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doTypeCheck() throws IterationTypeMismatchException {
		throw new UnsupportedOperationException(
				"Not implemented for this class");
	}

	@Override
	public String getIdentifier() {
		return "an id";
	}

	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIsRunning(boolean isRunning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInputPortConnected(DataflowInputPort inputPort) {
		return false;
	}

	@Override
	public String recordIdentifier() {
		return getIdentifier();
	}

    @Override
    public void setImmutable() {
        // TODO Auto-generated method stub
        
    }

}
