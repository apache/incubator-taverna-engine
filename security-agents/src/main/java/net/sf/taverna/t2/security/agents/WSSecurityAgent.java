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
 ******************************************************************************/
package net.sf.taverna.t2.security.agents;

import java.util.HashMap;
import java.util.Vector;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyStore;

import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.WSSecUsernameToken;

import org.w3c.dom.Document;

/**
 * A security agent for Web Services that knows how to perform security actions on
 * a message to be sent to a WS, according to the WS-Security specification.
 * 
 * @author Alexandra Nenadic
 *
 */
public class WSSecurityAgent extends SecurityAgent {
	   
    /** The BouncyCastle 'UBER'-type Keystore containing user's credentials */
    private KeyStore keyStore;
    
    /** The map of service urls associated with private keys from the Keystore */
    private  HashMap<String, Vector<String>> serviceURLs;
    
    /** The BouncyCastle 'UBER'-type Truststore containing services' credentials */
    private KeyStore trustStore;
    
    /** URL of the service this agent is performing actions for  */
    private String serviceURL;
    
    
    /**
     * Constructs a new WSSecurityAgent for the supplied master Keystore and Truststore.
     */ 
    public WSSecurityAgent(String url, KeyStore ks, HashMap<String, Vector<String>> urls, KeyStore ts)
    {
    	serviceURL = url;
    	
    	keyStore = ks;
    	serviceURLs = urls;
    	trustStore = ts;

    }
    
	public void wssUsernameToken(Document doc, RequestData reqData) throws SAException {
        
		String password = null;//= "testpasswd";
		String username = null;
		
		// Username field is only used to pass the service URL 
		// that can be used to derine the Keystore alias for the required password entry
		//String alias = "password#" +reqData.getUsername(); 
		//serviceURL = reqData.getUsername();
		
		//String alias = "password#" + ((MessageContext)reqData.getMsgContext()).getProperty("alias");
		
		String alias = "password#"+serviceURL;
		
        try{
        	// Password is saved as SecretKeySpec object in the Keystore
        	byte [] bPassword = (((SecretKeySpec) keyStore.getKey(alias, null))).getEncoded();
        	if (bPassword==null){
            	throw new SAException("No password defined for the service.");
        	}
        	else{
            	String concatenatedUsernamePassword = new String(bPassword);
            	username = concatenatedUsernamePassword.substring(0,concatenatedUsernamePassword.indexOf(' '));
            	password = concatenatedUsernamePassword.substring(concatenatedUsernamePassword.indexOf(' ')+1); ;	
        	}
        }
        catch (Exception ex){
        	throw new SAException("Failed to fetch the password entry from the Keystore.");
            //logger.error("Security Agent Error: Failed to fetch the password entry from the Keystore.");
        }
        
		// Set the correct username on the RequestData obejct
        reqData.setUsername(username);
        
        WSSecUsernameToken builder = new WSSecUsernameToken();
        builder.setWsConfig(reqData.getWssConfig());
        builder.setPasswordType(reqData.getPwType());
        builder.setUserInfo(username, password);

        if (reqData.getUtElements() != null && reqData.getUtElements().length > 0) {
            for (int j = 0; j < reqData.getUtElements().length; j++) {
                reqData.getUtElements()[j].trim();
                if (reqData.getUtElements()[j].equals("Nonce")) {
                    builder.addNonce();
                }
                if (reqData.getUtElements()[j].equals("Created")) {
                    builder.addCreated();
                }
                reqData.getUtElements()[j] = null;
            }
        }
        builder.build(doc, reqData.getSecHeader());        
	}
}
