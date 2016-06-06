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

/**
 *
 * @author Paolo Missier
 *
 */
public class IterationType implements ProvenanceEventType {
	private PortsSequenceType inputdata;
	private PortsSequenceType outputdata;
	private String id; // attribute

	public IterationType() {
	}

	public IterationType(PortsSequenceType inputdata,
			PortsSequenceType outputdata, String id) {
		this.inputdata = inputdata;
		this.outputdata = outputdata;
		this.id = id;
	}

	/**
	 * Gets the inputdata value for this IterationType.
	 *
	 * @return inputdata
	 */
	public PortsSequenceType getInputdata() {
		return inputdata;
	}

	/**
	 * Sets the inputdata value for this IterationType.
	 *
	 * @param inputdata
	 */
	public void setInputdata(PortsSequenceType inputdata) {
		this.inputdata = inputdata;
	}

	/**
	 * Gets the outputdata value for this IterationType.
	 *
	 * @return outputdata
	 */
	public PortsSequenceType getOutputdata() {
		return outputdata;
	}

	/**
	 * Sets the outputdata value for this IterationType.
	 *
	 * @param outputdata
	 */
	public void setOutputdata(PortsSequenceType outputdata) {
		this.outputdata = outputdata;
	}

	/**
	 * Gets the id value for this IterationType.
	 *
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id value for this IterationType.
	 *
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
