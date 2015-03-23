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

package org.apache.taverna.visit;

/**
 * This visit kind is a dummy for collecting together the information associated
 * with a nested workflow.
 * 
 * @author alanrw
 */
public class DataflowCollation extends VisitKind {
	public static final int NESTED_ISSUES = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * There are no visitors that can perform a DataflowCollation visit. This
	 * is, instead done within the HierarchyTraverser code iteself.
	 * 
	 * @see org.apache.taverna.visit.VisitKind#getVisitorClass()
	 */
	@Override
	public Class<? extends Visitor<?>> getVisitorClass() {
		return null;
	}

	private static class Singleton {
		private static DataflowCollation instance = new DataflowCollation();
	}

	public static DataflowCollation getInstance() {
		return Singleton.instance;
	}
}
