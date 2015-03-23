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

package org.apache.taverna.provenance.item;

import java.util.Map;

import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;

/**
 * Contains references to data which a workflow has used or created. Parent is
 * an {@link IterationProvenanceItem}
 * 
 * @author Ian Dunlop
 * @auhor Stuart Owen
 * @author Paolo Missier
 */
public abstract class DataProvenanceItem extends AbstractProvenanceItem {
	/** A map of port name to data reference */
	private Map<String, T2Reference> dataMap;
	private ReferenceService referenceService;

	/**
	 * Is this {@link ProvenanceItem} for input or output data
	 * 
	 * @return
	 */
	protected abstract boolean isInput();

	public Map<String, T2Reference> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, T2Reference> dataMap) {
		this.dataMap = dataMap;
	}
	
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}
}
