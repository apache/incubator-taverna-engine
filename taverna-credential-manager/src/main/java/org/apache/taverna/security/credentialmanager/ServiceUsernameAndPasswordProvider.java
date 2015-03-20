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
