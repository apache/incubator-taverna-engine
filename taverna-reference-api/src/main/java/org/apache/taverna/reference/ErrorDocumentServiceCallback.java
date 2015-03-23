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

package org.apache.taverna.reference;

/**
 * Callback interface used by asynchronous methods in the
 * {@link ErrorDocumentService} interface
 * 
 * @author Tom Oinn
 */
public interface ErrorDocumentServiceCallback {
	/**
	 * Called when the requested {@link ReferenceSet} has been successfully
	 * retrieved.
	 * 
	 * @param errorDoc
	 *            the ErrorDocument requested
	 */
	void errorRetrieved(ErrorDocument errorDoc);

	/**
	 * Called if the retrieval failed for some reason
	 * 
	 * @param cause
	 *            an ErrorDocumentServiceException explaining the retrieval
	 *            failure
	 */
	void errorRetrievalFailed(ErrorDocumentServiceException cause);
}
