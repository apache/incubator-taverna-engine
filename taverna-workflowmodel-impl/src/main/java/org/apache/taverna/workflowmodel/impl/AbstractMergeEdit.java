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
import org.apache.taverna.workflowmodel.Merge;

public abstract class AbstractMergeEdit extends EditSupport<Merge> {
	private final MergeImpl merge;
	
	public AbstractMergeEdit(Merge merge) {
		if (merge == null)
			throw new RuntimeException(
					"Cannot construct a merge edit with a null merge");
		if (!(merge instanceof MergeImpl))
			throw new RuntimeException("Merge must be an instanceof MergeImpl");
		this.merge = (MergeImpl) merge;
	}

	@Override
	public final Merge applyEdit() throws EditException {
		synchronized (merge) {
			doEditAction(merge);
		}
		return merge;
	}

	protected abstract void doEditAction(MergeImpl mergeImpl) throws EditException;
	
	@Override
	public final Object getSubject() {
		return merge;
	}
}
