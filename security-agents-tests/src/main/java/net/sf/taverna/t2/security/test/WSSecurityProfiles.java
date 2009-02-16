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

public class WSSecurityProfiles {
	
	public static final String wssUTProfile = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
		"<globalConfiguration> \n" +
		"<requestFlow>\n" +
		"<handler type=\"java:net.sf.taverna.t2.security.test.T2WSDoAllSender\"> \n"+
		"<parameter name=\"action\" value=\"UsernameToken\"/> \n" +
		"<parameter name=\"passwordType\" value=\"PasswordText\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
		"</deployment>\n";

	public static final String wssUTDigestProfile = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> "+
		"<globalConfiguration> \n" +
		"<requestFlow> \n" +
		"<handler type=\"java:net.sf.taverna.t2.security.test.T2WSDoAllSender\"> \n"+
		"<parameter name=\"action\" value=\"UsernameToken\"/> \n" +
		"<parameter name=\"passwordType\" value=\"PasswordDigest\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
		"</deployment>";
	
	public static final String wssUTTimestampProfile = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
		"<globalConfiguration> \n" +
		"<requestFlow> \n" +
		"<handler type=\"java:net.sf.taverna.t2.security.test.T2WSDoAllSender\"> \n"+
		"<parameter name=\"action\" value=\"UsernameToken Timestamp\"/> \n" +
		"<parameter name=\"passwordType\" value=\"PasswordText\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
		"</deployment>";

	public static final String wssUTDigestTimestampProfile = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
		"<globalConfiguration> \n" +
		"<requestFlow> \n" +
		"<handler type=\"java:net.sf.taverna.t2.security.test.T2WSDoAllSender\"> \n"+
		"<parameter name=\"action\" value=\"UsernameToken Timestamp\"/> \n" +
		"<parameter name=\"passwordType\" value=\"PasswordDigest\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.HTTPSender\"/> \n"+
		"</deployment>";
	
	public static final String wssOMIIProfile = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
		"<globalConfiguration> \n" +
		"<requestFlow> \n" +
		"<handler type=\"java:uk.ac.omii.security.wss4j.handler.WSOutboundHandler\"> \n"+
		"<parameter name=\"action\" value=\"Timestamp Signature\"/> \n" +
	    "<parameter name=\"signaturePropFile\" value=\"crypto.properties\" /> \n"+
	    "<parameter name=\"signatureKeyIdentifier\" value=\"DirectReference\" /> \n"+
	    "<parameter name=\"signatureParts\" value=\"{}{http://schemas.xmlsoap.org/soap/envelope/}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp\" /> "+
		"<parameter name=\"passwordCallbackClass\" value=\"uk.ac.omii.security.utils.PWCallback\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"<responseFlow> \n" +
		"<handler type=\"java:uk.ac.omii.security.wss4j.handler.PolicyEnforcementHandler\"> \n"+
		"<parameter name=\"action\" value=\"Timestamp Signature\"/> \n"+
		"<parameter name=\"signaturePropFile\" value=\"crypto.properties\" /> \n"+
		"<parameter name=\"signatureKeyIdentifier\" value=\"DirectReference\" /> \n"+
		"<parameter name=\"passwordCallbackClass\" value=\"uk.ac.omii.security.utils.PWCallback\"/> \n"+
		"<parameter name=\"signatureParts\" value=\"{}{http://schemas.xmlsoap.org/soap/envelope/}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp\"/> \n"+
		"<parameter name=\"ignoreEndpointCNmismatch\" value=\"true\" /> \n"+
		"</handler> \n"+
		"</responseFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:uk.ac.omii.transport.http.OMIIHTTPSender\"/> \n"+
		"</deployment>";
	
	public static final String wssOMIIProfilePartial = 
		"<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "+
		"xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\"> \n"+
		"<globalConfiguration> \n" +
		"<requestFlow> \n" +
		"<handler type=\"java:uk.ac.omii.security.wss4j.handler.WSOutboundHandler\"> \n"+
		"<parameter name=\"action\" value=\"Timestamp Signature\"/> \n" +
	    "<parameter name=\"signaturePropFile\" value=\"crypto.properties\" /> \n"+
	    "<parameter name=\"signatureKeyIdentifier\" value=\"DirectReference\" /> \n"+
	    "<parameter name=\"signatureParts\" value=\"{}{http://schemas.xmlsoap.org/soap/envelope/}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp\" /> "+
		"<parameter name=\"passwordCallbackClass\" value=\"uk.ac.omii.security.utils.PWCallback\"/> \n"+
		"</handler> \n"+
		"</requestFlow> \n"+
		"</globalConfiguration> \n"+
		"<transport name=\"http\" pivot=\"java:uk.ac.omii.transport.http.OMIIHTTPSender\"/> \n"+
		"</deployment>";
}
