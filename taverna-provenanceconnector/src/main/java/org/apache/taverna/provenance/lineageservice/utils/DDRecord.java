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
package org.apache.taverna.provenance.lineageservice.utils;

/**
 * a simple bean to hold a database record from the DD table
 *
 * @author paolo
 *
 */
public class DDRecord {

	private String PFrom;
	private String PTo;
	private String vTo;
	private String valTo;
	private String vFrom;
	private String valFrom;
	private String iteration;
	public boolean isInput;

	@Override
	public String toString() {
		return new String("proc: " + PFrom + " vFrom: " + vFrom + " valFrom: "
				+ valFrom + "PTo: " + PTo + " vTo: " + vTo + " valTo: " + valTo);
	}

	/**
	 * @return the vTo
	 */
	public String getVTo() {
		return vTo;
	}

	/**
	 * @param to
	 *            the vTo to set
	 */
	public void setVTo(String to) {
		vTo = to;
	}

	/**
	 * @return the valTo
	 */
	public String getValTo() {
		return valTo;
	}

	/**
	 * @param valTo
	 *            the valTo to set
	 */
	public void setValTo(String valTo) {
		this.valTo = valTo;
	}

	/**
	 * @return the vFrom
	 */
	public String getVFrom() {
		return vFrom;
	}

	/**
	 * @param from
	 *            the vFrom to set
	 */
	public void setVFrom(String from) {
		vFrom = from;
	}

	/**
	 * @return the valFrom
	 */
	public String getValFrom() {
		return valFrom;
	}

	/**
	 * @param valFrom
	 *            the valFrom to set
	 */
	public void setValFrom(String valFrom) {
		this.valFrom = valFrom;
	}

	/**
	 * @return the isInput
	 */
	public boolean isInput() {
		return isInput;
	}

	/**
	 * @param isInput
	 *            the isInput to set
	 */
	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}

	/**
	 * @return the pFrom
	 */
	public String getPFrom() {
		return PFrom;
	}

	/**
	 * @param from
	 *            the pFrom to set
	 */
	public void setPFrom(String from) {
		PFrom = from;
	}

	/**
	 * @return the pTo
	 */
	public String getPTo() {
		return PTo;
	}

	/**
	 * @param to
	 *            the pTo to set
	 */
	public void setPTo(String to) {
		PTo = to;
	}

	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}

	/**
	 * @param iteration
	 *            the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

}
