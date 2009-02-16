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

/**
 * Contains a collection of transport-level security properties 
 * that can be associated with a service.
 * 
 * @author Alexandra Nenadic
 */
public class TransportProperties extends SecurityProperties{
	
	/**
	 *  Transport level properties' constants.
	 */
	public static final String Protocol = "Protocol";
	public static final String Port = "Port";
	public static final String AuthNType = "AuthNType";
	public static final String RequiresClientCert = "RequiresClientCert";
	public static final String ProxyCertDepth = "ProxyCertDepth";
	
	/**
	 * List of PROTOCOLS.
	 */
	public static final String[] PROTOCOLS = {"HTTP",
			"HTTPS"
			};	
	
	/**
	 * List of HTTP authentication types.
	 */	
	public static final String[] HTTP_AUTHENTICATION_TYPES = {"None",
			"Basic",
			"Digest",
			"SPNEGO",
			"OAuth",
			"Kerberos"
			};
	/**
	 * List of proxy certificate depths.
	 */
	public static final String[] PROXY_CERT_DEPTHS = {"0", 
		"Infinite", 
		"1", 
		"2", 
		"3", 
		"4", 
		"5", 
		"6", 
		"7", 
		"8", 
		"9", 
		"10"
		};
	
	
	
	public TransportProperties(){
		
		super();
		setProperty(TransportProperties.Protocol, null);
		setProperty(TransportProperties.Port, null);
		setProperty(TransportProperties.AuthNType, null);
		setProperty(TransportProperties.RequiresClientCert, null);
		setProperty(TransportProperties.ProxyCertDepth, null);
	}

}
