/*******************************************************************************
 * Copyright (C) 2007-2014 The University of Manchester   
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

import static java.lang.System.identityHashCode;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.OrderedPair;
import net.sf.taverna.t2.workflowmodel.Processor;

/**
 * Generalization over all operations acting on an ordered pair of ProcessorImpl
 * objects. These include most operations where a relationship is created,
 * modified or destroyed between two processors.
 * 
 * @author Tom Oinn
 * @author Donal Fellows
 */
abstract class AbstractBinaryProcessorEdit extends
		EditSupport<OrderedPair<Processor>> {
	private final OrderedPair<Processor> processors;

	public AbstractBinaryProcessorEdit(Processor a, Processor b) {
		if (!(a instanceof ProcessorImpl) || !(b instanceof ProcessorImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Processor which isn't an instance of ProcessorImpl");
		processors = new OrderedPair<>(a, b);
	}

	@Override
	public final OrderedPair<Processor> applyEdit() throws EditException {
		ProcessorImpl pia = (ProcessorImpl) processors.getA();
		ProcessorImpl pib = (ProcessorImpl) processors.getB();

		/*
		 * Acquire both locks. Guarantee to acquire in a consistent order, based
		 * on the system hash code (i.e., the object addresses, which we're not
		 * supposed to know). This means that we should not deadlock, as we've
		 * got a total order over all extant processors.
		 * 
		 * If someone is silly enough to use the same processor for both halves,
		 * it doesn't matter which arm of the conditional we take.
		 */
		if (identityHashCode(pia) < identityHashCode(pib)) {
			synchronized (pia) {
				synchronized (pib) {
					doEditAction(pia, pib);
				}
			}
		} else {
			synchronized (pib) {
				synchronized (pia) {
					doEditAction(pia, pib);
				}
			}
		}
		return processors;
	}

	@Override
	public final OrderedPair<Processor> getSubject() {
		return processors;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param processorA
	 *            The ProcessorImpl which is in some sense the source of the
	 *            relation between the two being asserted or operated on by this
	 *            edit
	 * @param processorB
	 *            The ProcessorImpl at the other end of the relation. *
	 * @throws EditException
	 */
	protected abstract void doEditAction(ProcessorImpl processorA,
			ProcessorImpl processorB) throws EditException;
}
