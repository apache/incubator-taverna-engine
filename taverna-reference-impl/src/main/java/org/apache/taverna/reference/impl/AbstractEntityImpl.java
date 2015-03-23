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

import org.apache.taverna.reference.T2Reference;

/**
 * Abstract superclass of ReferenceSetImpl, IdentifiedArrayList and
 * ErrorDocumentImpl, manages the T2Reference field for these types and their
 * hibernate backing.
 * 
 * @author Tom Oinn
 */
public class AbstractEntityImpl {
	private T2ReferenceImpl id;
	private String compactId = null;

	public T2Reference getId() {
		return id;
	}

	/**
	 * This method is only ever called from within Hibernate, and is used to
	 * initialize the unique ID of this reference set.
	 */
	public void setTypedId(T2ReferenceImpl newId) {
		id = newId;
	}

	/**
	 * Used because technically you can't accept and return implementation types
	 * in the methods on a bean which implements an interface, but Hibernate
	 * needs to construct concrete input and output types!
	 */
	public T2ReferenceImpl getTypedId() {
		return id;
	}

	public void setInternalId(String newId) {
		compactId = newId;
	}

	public final String getInternalId() {
		if (compactId == null)
			compactId = id.getCompactForm();
		return compactId;
	}
}
