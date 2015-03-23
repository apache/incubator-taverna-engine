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

import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Processor;

/**
 * Abstraction of an edit acting on a Processor instance. Handles the check to
 * see that the Processor supplied is really a ProcessorImpl.
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractProcessorEdit extends EditSupport<Processor> {
	private final ProcessorImpl processor;

	protected AbstractProcessorEdit(Processor processor) {
		if (processor == null)
			throw new RuntimeException(
					"Cannot construct a processor edit with null processor");
		if (!(processor instanceof ProcessorImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Processor which isn't an instance of ProcessorImpl");
		this.processor = (ProcessorImpl) processor;
	}

	@Override
	public final Processor applyEdit() throws EditException {
		synchronized (processor) {
			doEditAction(processor);
		}
		return processor;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param processor
	 *            The ProcessorImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(ProcessorImpl processor)
			throws EditException;

	@Override
	public final Processor getSubject() {
		return processor;
	}
}
