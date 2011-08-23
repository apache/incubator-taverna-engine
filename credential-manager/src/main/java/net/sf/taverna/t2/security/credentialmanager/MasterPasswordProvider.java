/*******************************************************************************
 * Copyright (C) 2008-2010 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.security.credentialmanager;

import java.util.Comparator;

/**
 * Defines an interface for providing a master password for the Credential Manager.
 * This master password is used to encrypt/decrypt the Credential Manager's Keystore/Truststore.
 * <p>
 * A typical implementation of this class would pop up a dialog to ask 
 * the user for the master password. Such providers should check
 * {@link GraphicsEnvironment#isHeadless()} before returning, to avoid attempts to pop up dialogues 
 * on server/headless installations.
 * <p>
 * Another example may be to read the master password from a file or from command line
 * parameters.
 * <p>
 * It is safe to return <code>null</code> if the provider does not have an
 * opinion.
 * 
 * @see CredentialManager
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * 
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
	 * @param password to set
	 */
	public void setMasterPassword(String password);

	/**
	 * Priority of this provider.
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
	 * could be <code>100</code>, allowing server-side providers to override with priorities like
	 * <code>500</code>, or fall-back providers (say by reading system
	 * properties) to have a priority of <code>10</code>.
	 * 
	 * @return The priority of this provider. Higher number means higher
	 *         priority.
	 */
	public int getProviderPriority();
	
	public class ProviderComparator implements Comparator<MasterPasswordProvider>{

		@Override
		public int compare(MasterPasswordProvider provider1, MasterPasswordProvider provider2) {
			return provider1.getProviderPriority() - provider2.getProviderPriority();
		};
	}

}
