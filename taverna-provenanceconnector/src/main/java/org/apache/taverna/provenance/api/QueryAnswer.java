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
package org.apache.taverna.provenance.api;



/**
 * Encapsulates a native Java data structure as a well as a String that holds
 * the OPM graph that represents the query answer
 *
 * @author Paolo Missier
 *
 */
public class QueryAnswer {
	private NativeAnswer nativeAnswer;
	private String _OPMAnswer_AsRDF;

	/**
	 * @return the native Java part of the query answer
	 */
	public NativeAnswer getNativeAnswer() {
		return nativeAnswer;
	}

	/**
	 * @param sets
	 *            the query answer
	 */
	public void setNativeAnswer(NativeAnswer a) {
		this.nativeAnswer = a;
	}

	/**
	 * @return the OPM graph as RDF/XML string, or null if OPM was inhibited
	 *         {@see OPM.computeGraph in APIClient.properties}
	 */
	public String getOPMAnswer_AsRDF() {
		return _OPMAnswer_AsRDF;
	}

	/**
	 * @param set
	 *            the OPM graph as RDF/XML string
	 */
	public void setOPMAnswer_AsRDF(String asRDF) {
		_OPMAnswer_AsRDF = asRDF;
	}
}
