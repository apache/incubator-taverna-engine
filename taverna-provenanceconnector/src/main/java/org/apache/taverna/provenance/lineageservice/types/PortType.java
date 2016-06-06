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
public class PortType implements ProvenanceEventType {
	private DataDocumentType dataDocument;
	private LiteralType literal;
	private String name; // attribute

	public PortType() {
	}

	public PortType(DataDocumentType dataDocument, LiteralType literal,
			String name) {
		this.dataDocument = dataDocument;
		this.literal = literal;
		this.name = name;
	}

	/**
	 * Gets the dataDocument value for this PortType.
	 *
	 * @return dataDocument
	 */
	public DataDocumentType getDataDocument() {
		return dataDocument;
	}

	/**
	 * Sets the dataDocument value for this PortType.
	 *
	 * @param dataDocument
	 */
	public void setDataDocument(DataDocumentType dataDocument) {
		this.dataDocument = dataDocument;
	}

	/**
	 * Gets the literal value for this PortType.
	 *
	 * @return literal
	 */
	public LiteralType getLiteral() {
		return literal;
	}

	/**
	 * Sets the literal value for this PortType.
	 *
	 * @param literal
	 */
	public void setLiteral(LiteralType literal) {
		this.literal = literal;
	}

	/**
	 * Gets the name value for this PortType.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name value for this PortType.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
