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

import java.security.cert.X509Certificate;

/**
 * Defines an interface for providing ways to confirm/decline trust in a given
 * service (i.e. its public key certificate).
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
 * <p>
 * It is safe to return <code>null</code> if the provider does not have an
 * opinion.
 * 
 * @see CredentialManager
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * 
 */
public interface TrustConfirmationProvider {
	/**
	 * If the given public key certificate should be trusted or not.
	 * <p>
	 * This method is called when a SSL connection is attempted to a service
	 * which certificate could not be confirmed using the Credential Manager's
	 * Truststore (i.e. it could not be found there).
	 * <p>
	 * A typical implementation of this class would pop up a dialog and ask the
	 * user if they want to trust the service. Such providers should check
	 * {@link GraphicsEnvironment#isHeadless()} before returning to avoid
	 * attempts to pop up dialogues on server/headless installations.
	 * <p>
	 * The provider can return <code>null</code> if it does not have an opinion
	 * whether the certificate should be trusted or not (in which case other
	 * providers will be asked), or an instance of {@link TrustConfirmation}
	 * confirming or denying if the certificate is to be trusted.
	 * <p>
	 * If the provider returns <code>true</code>, the Credential Manager will
	 * also save the first certificate of the certificate chain (chain[0]) in
	 * its Truststore so the user will not be asked next time.
	 * 
	 * @param chain
	 *            X509 certificate chain to confirm whether it is trusted or not
	 * @return <code>null</code> if the provider does not have an opinion,
	 *         <code>true</code> if certificate is to be trusted and
	 *         <code>false</code> if not.
	 */
	public Boolean shouldTrustCertificate(X509Certificate[] chain);
}
