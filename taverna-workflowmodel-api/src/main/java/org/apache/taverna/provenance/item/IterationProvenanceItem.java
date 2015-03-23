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

import java.sql.Timestamp;

import org.apache.taverna.provenance.vocabulary.SharedVocabulary;

/**
 * One of these is created for each iteration inside an enacted activity.
 * Contains both the input and output data and port names contained inside
 * {@link DataProvenanceItem}s. The actual iteration number is contained inside
 * an int array eg [1]
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 */
public class IterationProvenanceItem extends AbstractProvenanceItem {
	private Timestamp enactmentEnded;
	private Timestamp enactmentStarted;
	private ErrorProvenanceItem errorItem;
	private final SharedVocabulary eventType = SharedVocabulary.ITERATION_EVENT_TYPE;
	private InputDataProvenanceItem inputDataItem;
	private int[] iteration;
	private OutputDataProvenanceItem outputDataItem;
	private IterationProvenanceItem parentIterationItem = null;

	public IterationProvenanceItem getParentIterationItem() {
		return parentIterationItem;
	}

	public Timestamp getEnactmentEnded() {
		return enactmentEnded;
	}

	public Timestamp getEnactmentStarted() {
		return enactmentStarted;
	}

	public ErrorProvenanceItem getErrorItem() {
		return errorItem;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public InputDataProvenanceItem getInputDataItem() {
		return inputDataItem;
	}

	public int[] getIteration() {
		return iteration;
	}

	public OutputDataProvenanceItem getOutputDataItem() {
		return outputDataItem;
	}

	public void setEnactmentEnded(Timestamp enactmentEnded) {
		this.enactmentEnded = enactmentEnded;
	}

	public void setEnactmentStarted(Timestamp enactmentStarted) {
		this.enactmentStarted = enactmentStarted;
	}

	public void setErrorItem(ErrorProvenanceItem errorItem) {
		this.errorItem = errorItem;
	}

	public void setInputDataItem(InputDataProvenanceItem inputDataItem) {
		this.inputDataItem = inputDataItem;
	}

	public void setIteration(int[] iteration) {
		this.iteration = iteration;
	}

	public void setOutputDataItem(OutputDataProvenanceItem outputDataItem) {
		this.outputDataItem = outputDataItem;
	}

	public void setParentIterationItem(
			IterationProvenanceItem parentIterationItem) {
		this.parentIterationItem = parentIterationItem;
	}
}
