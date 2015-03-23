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
import java.util.Set;

import org.apache.taverna.annotation.AnnotationChain;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.workflowmodel.Condition;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorFinishedEvent;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchStack;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyStack;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;

public class DummyProcessor implements Processor{

	public String firedOwningProcess = null;
	
	public List<Condition> preConditionList = new ArrayList<Condition>();
	public List<ProcessorInputPort> inputPorts = new ArrayList<ProcessorInputPort>();
	
	@Override
	public boolean doTypeCheck() throws IterationTypeMismatchException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fire(String owningProcess, InvocationContext context) {
		firedOwningProcess=owningProcess;
		
	}

	@Override
	public List<? extends Activity<?>> getActivityList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Condition> getControlledPreconditionList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatchStack getDispatchStack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends ProcessorInputPort> getInputPorts() {
		return inputPorts;
	}

	@Override
	public IterationStrategyStack getIterationStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends ProcessorOutputPort> getOutputPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Condition> getPreconditionList() {
		return preConditionList;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edit<? extends Processor> getAddAnnotationEdit(
			AnnotationChain newAnnotation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends AnnotationChain> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edit<? extends Processor> getRemoveAnnotationEdit(
			AnnotationChain annotationToRemove) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAnnotations(Set<AnnotationChain> annotations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addObserver(Observer<ProcessorFinishedEvent> observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Observer<ProcessorFinishedEvent>> getObservers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeObserver(Observer<ProcessorFinishedEvent> observer) {
		// TODO Auto-generated method stub
		
	}

}
