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

import java.util.Comparator;

/**
 * Defines an interface for providing a master password for the Credential
 * Manager. This master password is used to encrypt/decrypt the Credential
 * Manager's Keystore/Truststore.
 * <p>
 * A typical implementation of this class would pop up a dialog to ask the user
 * for the master password. Such providers should check
 * {@link GraphicsEnvironment#isHeadless()} before returning, to avoid attempts
 * to pop up dialogues on server/headless installations.
 * <p>
 * Another example may be to read the master password from a file or from
 * command line parameters.
 * 
 * @see CredentialManager
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public interface MasterPasswordProvider {

	/**
	 * Get the master password for the Credential Manager.
	 * <p>
	 * This method will only be called if the provider returned
	 * <code>true</code> from {@link #canProvideMasterPassword()}.
	 * <p>
	 * If the parameter <code>firstTime</code> is <code>true</code>, this is a
	 * request for <em>setting</em> the master password, as the Keystore and
	 * Truststore have not been created yet.
	 * 
	 * @see #canProvideMasterPassword()
	 * @param firstTime
	 *            <code>true</code> if this is the first time the keystore is
	 *            accessed, in which case the returned password will be used to
	 *            encrypt the keystore. If <code>false</code>, the returned
	 *            password will be used to decrypt (unlock) the keystore.
	 * @return The master password, or <code>null</code> if not available (user
	 *         cancelled, etc.)
	 */
	public String getMasterPassword(boolean firstTime);

	/**
	 * Set the master password.
	 * 
	 * @param password
	 *            to set
	 */
	public void setMasterPassword(String password);

	/**
	 * Get the priority of this provider.
	 * <p>
	 * The providers with highest priority will be asked first, lower-priority
	 * providers will be asked only if the higher ones either return
	 * <code>false</code> on the canProvideMasterPassword() method, or return
	 * <code>null</code> on the corresponding actual request.
	 * <p>
	 * It is undetermined who will be asked first if providers have the same
	 * priority.
	 * <p>
	 * A typical priority for UI providers that pop up a dialog to as the user
	 * could be <code>100</code>, allowing server-side providers to override
	 * with priorities like <code>500</code>, or fall-back providers (say by
	 * reading system properties) to have a priority of <code>10</code>.
	 * 
	 * @return The priority of this provider. Higher number means higher
	 *         priority.
	 */
	public int getProviderPriority();

	/**
	 * Set the provider's priority that determines the order in which various
	 * master password providers will be invoked.
	 * 
	 * @param priority
	 *            provider's priority
	 */
	// public void setProviderPriority(int priority);

	public class ProviderComparator implements
			Comparator<MasterPasswordProvider> {
		@Override
		public int compare(MasterPasswordProvider provider1,
				MasterPasswordProvider provider2) {
			return provider1.getProviderPriority()
					- provider2.getProviderPriority();
		}
	}
}
