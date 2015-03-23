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

import org.apache.taverna.provenance.vocabulary.SharedVocabulary;

/**
 * When an error is received in the dispatch stack, one of these is created and
 * sent across to the {@link ProvenanceConnector}. Parent is an
 * {@link IterationProvenanceItem}
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author Paolo Missier
 */
public class ErrorProvenanceItem extends AbstractProvenanceItem {
	private Throwable cause;
	private String message;
	private String errorType;
	private SharedVocabulary eventType = SharedVocabulary.ERROR_EVENT_TYPE;

	public ErrorProvenanceItem() {
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
