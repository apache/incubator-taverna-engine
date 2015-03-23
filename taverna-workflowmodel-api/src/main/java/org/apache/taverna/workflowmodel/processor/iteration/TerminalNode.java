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

package org.apache.taverna.workflowmodel.processor.iteration;

import javax.swing.tree.MutableTreeNode;

/**
 * The terminal node is the root of the iteration strategy tree, it is
 * responsible for forwarding all events up to the iteration strategy itself
 * which can then propogate them to the strategy stack.
 */
@SuppressWarnings("serial")
public abstract class TerminalNode extends AbstractIterationStrategyNode {
	@Override
	public synchronized void insert(MutableTreeNode child, int index) {
		if (getChildCount() > 0 && getChildAt(0) != child)
			throw new IllegalStateException(
					"The terminal node can have maximum one child");
		super.insert(child, index);
	}
}
