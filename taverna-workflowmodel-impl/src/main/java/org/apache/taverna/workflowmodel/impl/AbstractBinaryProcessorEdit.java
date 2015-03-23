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

import static java.lang.System.identityHashCode;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.OrderedPair;
import org.apache.taverna.workflowmodel.Processor;

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
