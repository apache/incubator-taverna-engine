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

import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.AddDispatchLayerEdit;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.DispatchStackImpl;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ErrorBounce;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Failover;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Invoke;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Retry;

public class DefaultDispatchStackEdit extends AbstractProcessorEdit {
	private Edit<?> compoundEdit=null;
	private static final int MAX_JOBS = 1;
	private static final long BACKOFF_FACTOR = (long) 1.1;
	private static final int MAX_DELAY = 5000;
	private static final int INITIAL_DELAY = 1000;
	private static final int MAX_RETRIES = 0;

	public DefaultDispatchStackEdit(Processor processor) {
		super(processor);
		DispatchStackImpl stack = ((ProcessorImpl)processor).getDispatchStack();
		// Top level parallelise layer
		int layer = 0;
		List<Edit<?>> edits = new ArrayList<Edit<?>>();
		
		edits.add(new AddDispatchLayerEdit(stack, new Parallelize(MAX_JOBS), layer++));
		edits.add(new AddDispatchLayerEdit(stack, new ErrorBounce(), layer++));
		edits.add(new AddDispatchLayerEdit(stack, new Failover(), layer++));
		edits.add(new AddDispatchLayerEdit(stack, new Retry(MAX_RETRIES, INITIAL_DELAY,
				MAX_DELAY, BACKOFF_FACTOR), layer++));
		edits.add(new AddDispatchLayerEdit(stack, new Invoke(), layer++));
		compoundEdit=new CompoundEdit(edits);
	}

	@Override
	protected void doEditAction(ProcessorImpl processor) throws EditException {
		compoundEdit.doEdit();
	}

	@Override
	protected void undoEditAction(ProcessorImpl processor) {
		compoundEdit.undo();
	}

}
