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

import java.net.URI;

/**
 * Defines an interface for providing a username and password for a service to
 * be invoked as part of a workflow run.
 * <p>
 * Used by Credential Manager when looking up the username and password for the
 * service in its Keystore - if it cannot find anything it will loop through all
 * providers until one can provide them. If none can, the service invocation
 * will (most probably) fail.
 * <p>
 * A typical implementation of this class would pop up a dialog and ask the user
 * for the password. Such providers should check
 * {@link GraphicsEnvironment#isHeadless()} before returning to avoid attempts
 * to pop up dialogues on server/headless installations.
 * 
 * @see CredentialManager
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public interface ServiceUsernameAndPasswordProvider {
	/**
	 * Get the username and password pair for the given service URI.
	 * 
	 * @param serviceURI
	 *            The service we are looking username and password for
	 * @param requestMessage
	 *            The message to be presented to the user when asking for the
	 *            username and password, normally useful for UI providers that
	 *            pop up dialogs, can be ignored otherwise
	 * @return the username and password pair for the given service URI, or
	 *         <tt>null</tt> if the provider does not know for this URI.
	 */
	UsernamePassword getServiceUsernameAndPassword(URI serviceURI,
			String requestMessage);

	/**
	 * Set the username and password pair for the given service URI.
	 */
	void setServiceUsernameAndPassword(URI serviceURI,
			UsernamePassword usernamePassword);
}
