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

package org.apache.taverna.reference.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.reference.ReferenceContext;

/**
 * A trivial implementation of ReferenceContext, used if the context parameter
 * to any service method is null.
 * 
 * @author Tom Oinn
 */
public class EmptyReferenceContext implements ReferenceContext {
	/**
	 * Return an empty entity set for all queries.
	 */
	@Override
	public <T> List<T> getEntities(Class<T> arg0) {
		return new ArrayList<>();
	}

	@Override
	public void addEntity(Object entity) {
	}
}
