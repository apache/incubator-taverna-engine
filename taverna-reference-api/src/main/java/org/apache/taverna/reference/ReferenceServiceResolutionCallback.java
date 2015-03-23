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
 * Used by the asynchronous form of the resolveIdentifier method in
 * {@link ReferenceService}
 * 
 * @author Tom Oinn
 */
public interface ReferenceServiceResolutionCallback {
	/**
	 * Called when the resolution process has completed
	 * 
	 * @param result
	 *            the Identified that corresponds to the {@link T2Reference}
	 *            specified in the call to
	 *            {@link ReferenceService#resolveIdentifierAsynch}
	 */
	void identifierResolved(Identified result);

	/**
	 * Called when the resolution process has failed
	 * 
	 * @param cause
	 *            a ReferenceServiceException describing the failure
	 */
	void resolutionFailed(ReferenceServiceException cause);
}
