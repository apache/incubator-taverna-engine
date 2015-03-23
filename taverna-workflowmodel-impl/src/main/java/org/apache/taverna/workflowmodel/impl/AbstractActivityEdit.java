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
import org.apache.taverna.workflowmodel.processor.activity.AbstractActivity;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * Abstraction of an edit acting on a Activity instance. Handles the check to
 * see that the Activity supplied is really a AbstractActivity.
 * 
 * @author David Withers
 * @author Stian Soiland-Reyes
 */
public abstract class AbstractActivityEdit<T> extends EditSupport<Activity<T>> {
	private final AbstractActivity<T> activity;

	protected AbstractActivityEdit(Activity<T> activity) {
		if (activity == null)
			throw new RuntimeException(
					"Cannot construct an activity edit with null activity");
		if (!(activity instanceof AbstractActivity))
			throw new RuntimeException(
					"Edit cannot be applied to an Activity which isn't an instance of AbstractActivity");
		this.activity = (AbstractActivity<T>) activity;
	}

	@Override
	public final Activity<T> applyEdit() throws EditException {
		synchronized (activity) {
			doEditAction(activity);
		}
		return activity;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param processor
	 *            The ProcessorImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(AbstractActivity<T> processor)
			throws EditException;

	@Override
	public final Activity<T> getSubject() {
		return activity;
	}
}
