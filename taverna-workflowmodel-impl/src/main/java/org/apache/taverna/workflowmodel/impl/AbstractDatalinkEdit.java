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

import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a Datalink instance. Handles the check to
 * see that the Datalink supplied is really a DatalinkImpl.
 * 
 * @author David Withers
 */
public abstract class AbstractDatalinkEdit extends EditSupport<Datalink> {
	private final DatalinkImpl datalink;

	protected AbstractDatalinkEdit(Datalink datalink) {
		if (datalink == null)
			throw new RuntimeException(
					"Cannot construct a datalink edit with null datalink");
		if (!(datalink instanceof DatalinkImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Datalink which isn't an instance of DatalinkImpl");
		this.datalink = (DatalinkImpl) datalink;
	}

	@Override
	public final Datalink applyEdit() throws EditException {
		synchronized (datalink) {
			doEditAction(datalink);
		}
		return datalink;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param datalink
	 *            The DatalinkImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DatalinkImpl datalink)
			throws EditException;

	@Override
	public final Datalink getSubject() {
		return datalink;
	}
}
