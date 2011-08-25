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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

public class HTTPSConnectionTest {

	// Log4J Logger
	private static Logger logger = Logger.getLogger(HTTPSConnectionTest.class);

//	public static void main(String[] args){
//		
//		try {
//			CredentialManagerOld.initialiseSSL();
//			//CredentialManager.getInstance();
//			//HttpsURLConnection.setDefaultSSLSocketFactory(CredentialManager.createTavernaSSLSocketFactory());
//			URL url = new URL ("https://rpc103.cs.man.ac.uk:8443/wsrf/services/cagrid/SecureHelloWorld?wsdl");
//			HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
//			// user should be asked automatically if they want to trust the connection
//			httpsConnection.connect();
//			
//		} catch (CMException e) {
//			logger.error("", e);
//		} catch (MalformedURLException e) {
//			logger.error("", e);
//		} catch (IOException e) {
//			logger.error("", e);
//		}
//		catch(Exception ex){ // anything we did not expect
//			logger.error("", ex);
//		}
//		
//	}
}
