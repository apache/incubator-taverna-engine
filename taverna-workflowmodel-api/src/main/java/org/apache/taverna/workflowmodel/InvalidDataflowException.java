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

package org.apache.taverna.workflowmodel;

/**
 * Thrown if attempting to use a workflow that is not
 * {@link Dataflow#checkValidity() valid}.
 * <p>
 * The {@link DataflowValidationReport} can be retrieved using
 * {@link #getDataflowValidationReport()} and will provide details on how the
 * dataflow is invalid. The {@link #getDataflow()} will provide the invalid
 * dataflow.
 * 
 * @author Stian Soiland-Reyes
 */
public class InvalidDataflowException extends Exception {
	private static final long serialVersionUID = -8470683930687738369L;
	private final DataflowValidationReport report;
	private final Dataflow dataflow;

	public InvalidDataflowException(Dataflow dataflow,
			DataflowValidationReport report) {
		this.report = report;
		this.dataflow = dataflow;
	}

	/**
	 * Get the {@link DataflowValidationReport validation report} for the
	 * failing dataflow.
	 * 
	 * @return Dataflow validation report
	 */
	public DataflowValidationReport getDataflowValidationReport() {
		return report;
	}

	/**
	 * Get the {@link Dataflow} that is not valid.
	 * 
	 * @return Invalid Dataflow
	 */
	public Dataflow getDataflow() {
		return dataflow;
	}
}
