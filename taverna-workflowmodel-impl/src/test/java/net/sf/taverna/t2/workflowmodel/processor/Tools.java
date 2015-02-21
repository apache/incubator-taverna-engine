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
package net.sf.taverna.t2.workflowmodel.processor;

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.InputPort;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;
import net.sf.taverna.t2.workflowmodel.impl.ProcessorImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * Not to be confused with the probably more helpful 
 * {@link net.sf.taverna.t2.workflowmodel.utils.Tools}.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 */
public class Tools {
	private static EditsImpl edits = new EditsImpl();

	/**
	 * Construct a new {@link Processor} with a single {@link Activity} and
	 * overall processor inputs and outputs mapped to the activity inputs and
	 * outputs. This is intended to be equivalent to the processor creation in
	 * Taverna1 where the concepts of Processor and Activity were somewhat
	 * confused; it also inserts retry, parallelise and failover layers
	 * configured as a Taverna1 process would be.
	 * <p>
	 * Modifies the given activity object, adding the mappings for input and
	 * output port names (these will all be fooport->fooport but they're still
	 * needed)
	 * 
	 * @param activity
	 *            the {@link Activity} to use to build the new processor around
	 * @return An initialised {@link ProcessorImpl}
	 */
	public static Processor buildFromActivity(Activity<?> activity)
			throws EditException {
		Processor processor = edits.createProcessor("");
		edits.getDefaultDispatchStackEdit(processor).doEdit();
		// Add the Activity to the processor
		edits.getAddActivityEdit(processor, activity).doEdit();
		/*
		 * Create processor inputs and outputs corresponding to activity inputs
		 * and outputs and set the mappings in the Activity object.
		 */
		activity.getInputPortMapping().clear();
		activity.getOutputPortMapping().clear();
		for (InputPort ip : activity.getInputPorts()) {
			ProcessorInputPort pip = edits.createProcessorInputPort(processor,
					ip.getName(), ip.getDepth());
			edits.getAddProcessorInputPortEdit(processor, pip).doEdit();
			activity.getInputPortMapping().put(ip.getName(), ip.getName());
		}
		for (OutputPort op : activity.getOutputPorts()) {
			ProcessorOutputPort pop = edits.createProcessorOutputPort(
					processor, op.getName(), op.getDepth(),
					op.getGranularDepth());
			edits.getAddProcessorOutputPortEdit(processor, pop).doEdit();
			activity.getOutputPortMapping().put(op.getName(), op.getName());
		}
		
		return processor;
	}

	private Tools() {
	}
}
