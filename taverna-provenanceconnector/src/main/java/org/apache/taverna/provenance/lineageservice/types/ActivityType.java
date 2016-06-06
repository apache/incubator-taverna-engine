/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice.types;

import org.apache.taverna.provenance.lineageservice.types.ProvenanceEventType;

/**
 *
 * @author Paolo Missier
 *
 */
public class ActivityType implements ProvenanceEventType {
	private IterationType[] iteration;

	private String id; // attribute

	public ActivityType() {
	}

	public ActivityType(IterationType[] iteration, String id) {
		this.iteration = iteration;
		this.id = id;
	}

	/**
	 * Gets the iteration value for this ActivityType.
	 *
	 * @return iteration
	 */
	public IterationType[] getIteration() {
		return iteration;
	}

	/**
	 * Sets the iteration value for this ActivityType.
	 *
	 * @param iteration
	 */
	public void setIteration(IterationType[] iteration) {
		this.iteration = iteration;
	}

	public IterationType getIteration(int i) {
		return this.iteration[i];
	}

	public void setIteration(int i, IterationType _value) {
		this.iteration[i] = _value;
	}

	/**
	 * Gets the id value for this ActivityType.
	 *
	 * @return id
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * Sets the id value for this ActivityType.
	 *
	 * @param id
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

}
