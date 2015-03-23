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

package org.apache.taverna.t2referencetest;

import java.util.Collections;
import java.util.Set;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.T2Reference;

public class DummyReferenceSet implements ReferenceSet {
	
	private Set<ExternalReferenceSPI> refs;

	public DummyReferenceSet(ExternalReferenceSPI ref) {
		refs = Collections.singleton(ref);
	}
	
	@Override
	public T2Reference getId() {
		return null;
	}

	@Override
	public Set<ExternalReferenceSPI> getExternalReferences() {
		return refs;
	}

	@Override
	public Long getApproximateSizeInBytes() {
		return null;
	}
}