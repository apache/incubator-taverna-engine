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
package net.sf.taverna.t2.security.credentialmanager.impl;

import java.net.URI;

import net.sf.taverna.t2.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

/**
 * 
 * @author Stian Soiland-Reyes
 * @author Alex Nenadic
 *
 */
public class HTTPAuthenticatorServiceUsernameAndPasswordProvider implements ServiceUsernameAndPasswordProvider {

	private static UsernamePassword usernamePassword;
	private static URI serviceURI;
	private static String requestMessage;
	private static long calls = 0;
	
	public static long getCalls() {
		return calls;
	}
	

	public static void resetCalls() {
		calls = 0;
	}

	@Override
	public UsernamePassword getServiceUsernameAndPassword(URI serviceURI,
			String requestMessage) {
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.serviceURI = serviceURI;
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.requestMessage = requestMessage;
		calls++;
		return usernamePassword.clone();
	}

	@Override
	public void setServiceUsernameAndPassword(URI serviceURI,
			UsernamePassword usernamePassword) {
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.serviceURI = serviceURI;
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.usernamePassword = usernamePassword;		
	}

	public static URI getServiceURI() {
		return serviceURI;
	}

	public String getRequestMessage() {
		return requestMessage;
	}
}