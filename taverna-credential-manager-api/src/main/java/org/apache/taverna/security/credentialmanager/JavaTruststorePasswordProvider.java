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
 * Defines an interface for providing a password for Java's default truststore
 * located in JAVA_HOME/lib/security/cacerts.
 * <p>
 * Used by Credential Manager when trying to copy the trusted certificates from the
 * Java's default truststore into the Credential Manageger's own Truststore. It will
 * first try the default Java passwords and then if they do not work - it will loop 
 * through all the providers until one can provide the password. If none
 * can, the certificates will not be copied. 
 * <p>
 * A typical implementation of this class would pop up a dialog 
 * and ask the user for the password. Such providers should check
 * {@link GraphicsEnvironment#isHeadless()} before returning to avoid 
 * attempts to pop up dialogues on server/headless installations.
 * <p>
 * It is safe to return <code>null</code> if the provider does not have an
 * opinion.
 * 
 * @see CredentialManagerOld
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * 
 */
public interface JavaTruststorePasswordProvider {

	/**
	 * Get the Java truststore password.
	 * <p>
	 * This method will only be called if the provider returned
	 * <code>true</code> from {@link #canProvideJavaTruststorePassword()}.
	 * <p>
	 * This method will be called when initialising the Credential Manager
	 * for the first time, in the cases where the Java truststore password has
	 * been changed from the VM default. The Credential Manager will need this
	 * password to unlock the Java truststore and copy the trusted certificate
	 * into the Credential Managers's own Truststore.
	 * <p>
	 * Generally only advanced users would change this password.
	 * 
	 * @return The Java truststore password, or <code>null</code> if not
	 *         available (for instance if user action was cancelled).
	 */
	public String getJavaTruststorePassword();
	
	/**
	 * Set the Java truststore password.
	 * @param password to set
	 */
	public void setJavaTruststorePassword(String password);
	
}
