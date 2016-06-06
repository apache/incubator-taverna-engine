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
public class PortsSequenceType implements ProvenanceEventType {
	private PortType[] port;

	public PortsSequenceType() {
	}

	public PortsSequenceType(PortType[] port) {
		this.port = port;
	}

	/**
	 * Gets the port value for this PortsSequenceType.
	 *
	 * @return port
	 */
	public PortType[] getPort() {
		return port;
	}

	/**
	 * Sets the port value for this PortsSequenceType.
	 *
	 * @param port
	 */
	public void setPort(PortType[] port) {
		this.port = port;
	}

	public PortType getPort(int i) {
		return this.port[i];
	}

	public void setPort(int i, PortType _value) {
		this.port[i] = _value;
	}

}
