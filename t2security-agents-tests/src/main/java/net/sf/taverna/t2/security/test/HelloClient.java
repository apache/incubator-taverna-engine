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
package net.sf.taverna.t2.security.test;

import net.sf.taverna.t2.security.agents.SecurityAgentManager;
import net.sf.taverna.t2.security.requests.SecurityRequest;
import net.sf.taverna.t2.security.agents.WSSecurityAgent;
import net.sf.taverna.t2.security.requests.WSSecurityRequest;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.profiles.WSSecurityProfile;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName ; 
//import org.apache.ws.security.handler.WSHandlerConstants;
//import org.apache.ws.security.WSConstants;
import org.apache.axis.configuration.XMLStringProvider;

//import net.sf.taverna.security.agents.T2WSDoAllSender;
public class HelloClient {


public static void main(String [] args) { 
	try { 
		

		String endpoint = "http://www.mygrid.org.uk/axis/services/UsernameHelloService"; // test server
		//String endpoint = "http://www.mygrid.org.uk/axis/services/UsernameTimestampHelloService"; // test server
		//String endpoint = "http://www.mygrid.org.uk/axis/services/UsernameDigestHelloService"; // test server
		//String endpoint = "http://www.mygrid.org.uk/axis/services/UsernameDigestTimestampHelloService"; // test server
		
		//String endpoint = "http://localhost:8080/axis/services/UsernameHelloService" ; // local server
		//String endpoint = "http://localhost:8080/axis/services/UsernameTimestampHelloService" ; 
		//String endpoint = "http://localhost:8080/axis/services/UsernameDigestHelloService" ; 
		//String endpoint = "http://localhost:8080/axis/services/UsernameDigestTimestampHelloService" ; 
		
		//Service service = new org.apache.axis.client.Service() ; 
		
		String wssEngineConfigurationString = WSSecurityProfiles.wssUTProfile;
		//String wssEngineConfigurationString = WSSecurityProfiles.wssUTTimestampProfile;
		//String wssEngineConfigurationString = WSSecurityProfiles.wssUTDigestProfile;
		//String wssEngineConfigurationString = WSSecurityProfiles.wssUTDigestTimestampProfile;
	
		XMLStringProvider wssEngineConfiguration = new XMLStringProvider(wssEngineConfigurationString); // WSS configuration from a WSDD string
		//System.err.println(wssEngineConfigurationString);
		Service service = new Service(wssEngineConfiguration);
	
		Call call = new Call(service);
		call.setTargetEndpointAddress(endpoint) ; 
		call.setOperationName(new QName("hello")) ;
		
		//WSS4J WSDoAllSender's invoke() method expects username not to be empty before the agent takes over 
		// to set it so we set it to the WSDL location here (that is used as keystore alias) and later on overwrite it 
		call.setProperty(Call.USERNAME_PROPERTY, endpoint + "?wsdl"); 
		
		// Get the Credential Manager instance
		CredentialManager credManager = null;
		try{
    		credManager = CredentialManager.getInstance();
    	}
    	catch (CMException cme){
    		
    		// Failed to instantiate Credential Manager - warn the user
    		String sMessage = cme.getMessage();
    		//logger.error(sMessage);
            JOptionPane.showMessageDialog(new JFrame(),
            		sMessage,
            		"Credential Manager Error", 
            		JOptionPane.ERROR_MESSAGE);
    	}

    	if (!credManager.isInitialised()){
    		
        	// Ask for master password to initialise Credential Manager
        	// The Keystore/Truststore must already exist at this point

            /*String mPassword = null;
        	GetPasswordDialog dGetMasterPassword = new GetPasswordDialog(this,
        			"Credential Manager", 
        			true,
        			"Enter the master password");         
        	dGetMasterPassword.setLocationRelativeTo(this);
        	dGetMasterPassword.setVisible(true);
        	
            mPassword = dGetMasterPassword.getPassword();

            if (mPassword == null) { //user cancelled
                // exit 
            }*/
    		
        	String mPassword = "uber"; // master password for the keystore
            try{
                // Initialise the Credential Manager with the master password
            	credManager.init(mPassword);
            }
            catch(CMException cme){
        		// Failed to initialise Credential Manager - warn the user and exit
        		String sMessage = cme.getMessage();
        		//logger.error(sMessage);
                JOptionPane.showMessageDialog(new JFrame(),
                		sMessage,
                		"Credential Manager", 
                		JOptionPane.ERROR_MESSAGE);
        		//System.exit(1);
            }
    	}
		
  	

    	SecurityAgentManager saManager = credManager.getSecurityAgentManager();
    	
    	WSSecurityProfile wsSecprof = new WSSecurityProfile();
    	wsSecprof.setWSSecurityProfileString(wssEngineConfigurationString);
    	
    	WSSecurityRequest wsSecReq = new WSSecurityRequest(endpoint + "?wsdl", wsSecprof);

    	WSSecurityAgent sa = (WSSecurityAgent) saManager.getSecurityAgent((SecurityRequest) wsSecReq);
    	
    	// check if agent is null
    	
		call.setProperty("security_agent", sa);
		
		/*T2WSDoAllSender t2WSDoAllSender = new T2WSDoAllSender();
		t2WSDoAllSender.setOption(WSHandlerConstants.ACTION, WSConstants.UT);
		t2WSDoAllSender.setOption(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);		
		   
		call.setClientHandlers(t2WSDoAllSender, null);*/
				
		String nickName = "Beauty" ; 
		System.out.println("Sent: '" + nickName + "'") ; 
		String ret = (String) call.invoke(new Object[] {nickName}) ;
		System.out.println("Got: '" + ret + "'") ; 
	} 
	catch (Exception e) { 
		e.printStackTrace() ; 
	} 
	System.exit(0) ; 
} 

} 


