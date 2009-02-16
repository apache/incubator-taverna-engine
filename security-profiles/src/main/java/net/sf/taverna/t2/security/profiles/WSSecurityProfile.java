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
package net.sf.taverna.t2.security.profiles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Defines a security profile for a Web Service. 
 * Contains a bag of transport-level and two sets of 
 * message-level properties (for REQUEST and RESPONSE message flow).
 * 
 * @author Alexandra Nenadic
 *
 */
public class WSSecurityProfile {
	
	/** 
	 * Transport level properties of a WS.
	 */
	private TransportProperties transportProperties;
	
	/**
	 *  A reference to a WS-Security policy (if this is null, then we 
	 *  need outboundMessageProperties and inboundMessageProperties to describe 
	 *  security properties of a WS.
	 */
	private String wsSecPolicyRef;
	
	/**
	 * Security properties required to send a secure REQUEST message to a WS.
	 */
	private WSSecurityMessageProperties outboundMessageProperties;
	
	/**
	 * Security properties required to receive a secure RESPONSE message from a WS.
	 */
	private WSSecurityMessageProperties inboundMessageProperties;
	
	/**
	 * All the above properties (except wsSecPolicyRef) can be combined into a
	 * single configuration string (in XML wsdd format) that can be passed directly 
	 * to a WSS4J handler. wsSecProfileString parameter holds that string, 
	 * whereas wsSecProfileName and wsSecProfileDescription hold its name and description 
	 * required if we want to save the profile string to a file. Currently, we do not save 
	 * individual properties to the profile file, rather we save just the configuration string, 
	 * its name and description, but this may change in the future. wsSecProfileString can be used to
	 * extract individual parameters and parameters can be used to contruct the configuration string 
	 * (it works both ways).
	 */ 
	private String wsSecProfileString; 
	private String wsSecProfileName; 
	private String wsSecProfileDescription; 
	
	public WSSecurityProfile(){
		transportProperties = new TransportProperties();
		outboundMessageProperties = new WSSecurityMessageProperties();
		inboundMessageProperties = new WSSecurityMessageProperties();
	}

	/**
	 * @param transportProperties the transportProperties to set
	 */
	public void setTransportProperties(TransportProperties transportProperties) {
		this.transportProperties = transportProperties;
	}

	/**
	 * @return the transportProperties
	 */
	public TransportProperties getTransportProperties() {
		return transportProperties;
	}

	/**
	 * @param wsSecPolicyRef the wsSecPolicyRef to set
	 */
	public void setWSSecurityPolicyRef(String wsSecPolicyRef) {
		this.wsSecPolicyRef = wsSecPolicyRef;
	}
	/**
	 * @return the wsSecPolicyRef
	 */
	public String getWSSecurityPolicyRef() {
		return wsSecPolicyRef;
	}
	
	/**
	 * @param outboundMessageProperties the outboundMessageProperties to set
	 */
	public void setOutboundMessageProperties(WSSecurityMessageProperties outboundMessageProperties) {
		this.outboundMessageProperties = outboundMessageProperties;
	}

	/**
	 * @return the outboundMessageProperties
	 */
	public WSSecurityMessageProperties getOutboundMessageProperties() {
		return outboundMessageProperties;
	}

	/**
	 * @param inboundMessageProperties the inboundMessageProperties to set
	 */
	public void setInboundMessageProperties(WSSecurityMessageProperties inboundMessageProperties) {
		this.inboundMessageProperties = inboundMessageProperties;
	}

	/**
	 * @return the inboundMessageProperties
	 */
	public WSSecurityMessageProperties getInboundMessageProperties() {
		return inboundMessageProperties;
	}

	/**
	 * @param wsSecProfileString the wsSecProfileString to set
	 */
	public void setWSSecurityProfileString(String wsSecProfileString) {
		this.wsSecProfileString = wsSecProfileString;
	}
	/**
	 * @return the wsSecProfileString
	 */
	public String getWSSecurityProfileString() {
		if (wsSecProfileString != null){
			return wsSecProfileString;
		}
		else{
			// Assemble the profile configuration string from the parameters defined in this object
			wsSecProfileString = "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
			"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
			"<globalConfiguration> \n" +
			"<requestFlow>\n" +
			"<handler type=\"java:net.sf.taverna.security.T2WSDoAllSender\"> \n";
			try{
				if (outboundMessageProperties.getProperty(WSSecurityMessageProperties.Actions)!= null) {
					wsSecProfileString = wsSecProfileString + "<parameter name=\"action\" value=\""+outboundMessageProperties.getProperty(WSSecurityMessageProperties.Actions)+"\"/> \n";
					HashMap<String,String> parameters = outboundMessageProperties.getProperties();
					Set<String> set = parameters.keySet();
					for (Iterator<String> itr=set.iterator(); itr.hasNext(); ){
						String key = (String) itr.next();
						if (!key.equals(WSSecurityMessageProperties.Actions)){
							String value = outboundMessageProperties.getProperty(key);
							if (value!=null)
								wsSecProfileString = wsSecProfileString + "<parameter name=\""+ key +"\" value=\""+ value +"\"/> \n";
						}
					}
				}
			}
			catch(NoSuchSecurityPropertyException nspe){
				// should not happen as we are only accessing the properties that do exist
			}
			wsSecProfileString = wsSecProfileString + "</handler> \n"+
			"</requestFlow> \n"+
			"<responseFlow>\n" +
			"<handler type=\"java:net.sf.taverna.security.T2WSDoAllSender\"> \n";
			try{
				if (inboundMessageProperties.getProperty(WSSecurityMessageProperties.Actions)!= null) {
					wsSecProfileString = wsSecProfileString + "<parameter name=\"action\" value=\""+inboundMessageProperties.getProperty(WSSecurityMessageProperties.Actions)+"\"/> \n";
					HashMap<String,String> parameters = inboundMessageProperties.getProperties();
					Set<String> set = parameters.keySet();
					for (Iterator<String> itr=set.iterator(); itr.hasNext(); ){
						String key = (String) itr.next();
						if (!key.equals(WSSecurityMessageProperties.Actions)){
							String value = inboundMessageProperties.getProperty(key);
							if (value!=null)
								wsSecProfileString = wsSecProfileString + "<parameter name=\""+ key +"\" value=\""+ value +"\"/> \n";
						}
					}
				}
			}
			catch(NoSuchSecurityPropertyException nspe){
				// should not happen as we are only accessing the properties that do exist
			}
			wsSecProfileString = wsSecProfileString + "</handler> \n"+"</handler> \n"+
			"</responseFlow> \n"+
			"</globalConfiguration> \n"+
			// TODO: Transport level handler here will depend on the transport level protocol (i.e. whether it is HTTPS or not)
			"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
			"</deployment>\n";
			return wsSecProfileString;
		}
	}
	/**
	 * @param wsSecProfileName the wsSecProfileName to set
	 */
	public void setWSSecurityProfileName(String wsSecProfileName) {
		this.wsSecProfileName = wsSecProfileName;
	}
	/**
	 * @return the wsSecProfileName
	 */
	public String getWSSecurityProfileName() {
		return wsSecProfileName;
	}
	/**
	 * @param wsSecProfileDescription the wsSecProfileDescription to set
	 */
	public void setWSSecurityProfileDescription(String wsSecProfileDescription) {
		this.wsSecProfileDescription = wsSecProfileDescription;
	}
	/**
	 * @return the wsSecProfileDescription
	 */
	public String getWSSecurityProfileDescription() {
		return wsSecProfileDescription;
	}

}
