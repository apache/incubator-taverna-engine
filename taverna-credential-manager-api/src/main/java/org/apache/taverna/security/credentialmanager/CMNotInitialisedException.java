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

package org.apache.taverna.security.credentialmanager;

/**
 * Represents an exception thrown by Credential Manager if an application tries
 * to invoke certain methods on it before it has been initialised.
 * 
 * @author Alex Nenadic
 */
public class CMNotInitialisedException extends Exception {
	private static final long serialVersionUID = 6041577726294822985L;

	/**
	 * Creates a new CMNotInitialisedException.
	 */
	public CMNotInitialisedException() {
		super();
	}

	/**
	 * Creates a new CMNotInitialisedException with the specified message.
	 */
	public CMNotInitialisedException(String message) {
		super(message);
	}
}
