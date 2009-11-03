/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
 */
package net.sf.taverna.t2.security.credentialmanager;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.workbench.StartupSPI;

/**
 * 
 * Startup hook to initialise SSL socket factory used by Taverna for
 * creating HTTPS connections.
 * 
 * @author Alex Nenadic
 *
 */
public class InitialiseSSLStartupHook implements StartupSPI{

	private Logger logger = Logger.getLogger(InitialiseSSLStartupHook.class);

	public int positionHint() {
		return 750;
	}

	public boolean startup() {
		logger.info("Initialising SSL socker factory for SSL connections from Taverna.");
		try {
			CredentialManager.initialiseSSL();
		} catch (CMException e) {
			logger.error("Could not initialise SSL socket factory and Taverba's Truststore for SSL connections from Taverna.", e);
		}
		return true;
	}

}
