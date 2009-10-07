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
import java.util.Set;

import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workflowmodel.Condition;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorFinishedEvent;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.health.HealthReport;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;

public class DummyProcessor implements Processor{

	public HealthReport checkProcessorHealth() {
		return new ProcessorHealthReport("",new ArrayList<HealthReport>());
	}

	public String firedOwningProcess = null;
	
	public List<Condition> preConditionList = new ArrayList<Condition>();
	public List<ProcessorInputPort> inputPorts = new ArrayList<ProcessorInputPort>();
	
	public boolean doTypeCheck() throws IterationTypeMismatchException {
		// TODO Auto-generated method stub
		return false;
	}

	public void fire(String owningProcess, InvocationContext context) {
		firedOwningProcess=owningProcess;
		
	}

	public List<? extends Activity<?>> getActivityList() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends Condition> getControlledPreconditionList() {
		// TODO Auto-generated method stub
		return null;
	}

	public DispatchStack getDispatchStack() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends ProcessorInputPort> getInputPorts() {
		return inputPorts;
	}

	public IterationStrategyStack getIterationStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends ProcessorOutputPort> getOutputPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<? extends Condition> getPreconditionList() {
		return preConditionList;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Edit<? extends Processor> getAddAnnotationEdit(
			AnnotationChain newAnnotation) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<? extends AnnotationChain> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	public Edit<? extends Processor> getRemoveAnnotationEdit(
			AnnotationChain annotationToRemove) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAnnotations(Set<AnnotationChain> annotations) {
		// TODO Auto-generated method stub
		
	}

	public void addObserver(Observer<ProcessorFinishedEvent> observer) {
		// TODO Auto-generated method stub
		
	}

	public List<Observer<ProcessorFinishedEvent>> getObservers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeObserver(Observer<ProcessorFinishedEvent> observer) {
		// TODO Auto-generated method stub
		
	}

}
