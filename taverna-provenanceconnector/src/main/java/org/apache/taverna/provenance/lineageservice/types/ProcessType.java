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
public class ProcessType implements ProvenanceEventType {
	private ProcessorType[] processor;
	private String dataflowID; // attribute
	private String facadeID; // attribute

	public ProcessType() {
	}

	public ProcessType(ProcessorType[] processor, String dataflowID,
			String facadeID) {
		this.processor = processor;
		this.dataflowID = dataflowID;
		this.facadeID = facadeID;
	}

	/**
	 * Gets the processor value for this ProcessType.
	 *
	 * @return processor
	 */
	public ProcessorType[] getProcessor() {
		return processor;
	}

	/**
	 * Sets the processor value for this ProcessType.
	 *
	 * @param processor
	 */
	public void setProcessor(ProcessorType[] processor) {
		this.processor = processor;
	}

	public ProcessorType getProcessor(int i) {
		return this.processor[i];
	}

	public void setProcessor(int i, ProcessorType _value) {
		this.processor[i] = _value;
	}

	/**
	 * Gets the dataflowID value for this ProcessType.
	 *
	 * @return dataflowID
	 */
	public String getDataflowID() {
		return dataflowID;
	}

	/**
	 * Sets the dataflowID value for this ProcessType.
	 *
	 * @param dataflowID
	 */
	public void setDataflowID(String dataflowID) {
		this.dataflowID = dataflowID;
	}

	/**
	 * Gets the facadeID value for this ProcessType.
	 *
	 * @return facadeID
	 */
	public String getFacadeID() {
		return facadeID;
	}

	/**
	 * Sets the facadeID value for this ProcessType.
	 *
	 * @param facadeID
	 */
	public void setFacadeID(String facadeID) {
		this.facadeID = facadeID;
	}

}
