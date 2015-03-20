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
