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

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.EncryptionConstants;

/**
 * Contains a collection of message-level security properties 
 * that can be associated with a Web Service.
 * 
 * @author Alexandra Nenadic
 */

public class WSSecurityMessageProperties extends SecurityProperties{

	/**
	 *  Message level properties' constants 
	 *  (there are actually parameters that can be passed to a WSS4J handler
	 *  to tell it what WS-Security actions to apply to a message).
	 */
	public static final String Actions = "action"; // contains concatenated ACTIONS strings separated by a blank character
	public static final String PasswordType = "PasswordType";
	public static final String SignatureParts = "SignatureParts";
	public static final String SignatureKeyIdentifier = "SignatureKeyIdentifier";
	public static final String SignatureAlgorithm = "SignatureAlgorithm";
	public static final String EncryptionParts = "EncryptionParts";
	public static final String EncryptionKeyIdentifier = "EncryptionKeyIdentifier";
	public static final String EncryptionAlgorithm = "EncryptionAlgorithm";
	public static final String EncryptionKeyTransportAlgorithm = "EncryptionKeyTransportAlgorithm";
	
	
	/**
	 * List of WS-security actions (to be performed on a message) accepted by WSS4J
	 */	
	public static final String[] ACTIONS = {
		"None",
		"Timestamp",
		"UsernameToken",
		"Signature",
		"Encrypt",
		"SAMLTokenUnsigned",
		"SAMLTokenSigned",
		"UsernameTokenSignature",
		"NoSerialisation"
		};
	
	/**
	 * List of password types accepted by WSS4J
	 */	
	public static final String[] PASSWORD_TYPES = {
		"PasswordText", 
		"PasswordDigest"
		};
	
	/**
	 * List of signature key identifier types accepted by WSS4J
	 */	
	public static final String[] SIGNATURE_KEY_IDENTIFIERS = {
		"IssuerSerial", //default
		"DirectReference", 
		"X509KeyIdentifier",
		"SKIKeyIdentifier",
		"Thumbprint"
		};
	
	/**
	 * List of signature algorithms (from XML Signature spec.) accepted by WSS4J
	 */	
	public static final  String[] SIGNATURE_ALGORITHMS = {
		XMLSignature.ALGO_ID_SIGNATURE_RSA, //default (currently the only one supported)
		XMLSignature.ALGO_ID_SIGNATURE_DSA, 
		XMLSignature.ALGO_ID_MAC_HMAC_SHA1
		};
		
	/**
	 * List of short names for signature algorithms accepted by WSS4J.
	 */	
	public static final  String[] SIGNATURE_ALGORITHMS_SHORT = {
		"RSA-SHA1", //default (currently the only one supported)
		"DSA-SHA1", 
		"HMAC-SHA1"
		};
	
	/**
	 * List of encryption key identifier types (from XML Encryption spec.) accepted by WSS4J
	 */	
	public static final String[] ENCRYPTION_KEY_IDENTIFIERS = {
		"IssuerSerial", // default
		"DirectReference", 
		"X509KeyIdentifier",
		"SKIKeyIdentifier",
		"EmbeddedKeyName",
		"Thumbprint"
		};	
	
	/**
	 * List of encryption algorithms (from XML Encryption spec.) accepted by WSS4J
	 */
	public static final String[] ENCRYPTION_ALGORITHMS = {
		EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, // default
		EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192, 
		EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256,
		EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES
		};

	/**
	 * List of short names for encryption algorithms accepted by WSS4J.
	 * Full alg. names (XML Encryption spec.) are contained in WSSecurityMessageProperties.
	 */	
	public static final String[] ENCRYPTION_ALGORITHMS_SHORT = {
		"AES_128", // default
		"AES_192", 
		"AES_256",
		"TRIPLE_DES"
		};
	
	/**
	 * List of encryption key transport algorithms (XML Encryption spec.) accepted by WSS4J
	 */	
	public static final String[] ENCRYPTION_KEY_TRANSPORT_ALGORITHMS = {
		EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, //default (currently the only one supported)
		EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP
		};
	
	/**
	 * List of short names for encryption key transport algorithms accepted by WSS4J.
	 */	
	public static final String[] ENCRYPTION_KEY_TRANSPORT_ALGORITHMS_SHORT = {
		"RSA-1_5", //default (currently the only one supported)
		"RSA_OAEP"
		};
	
	public WSSecurityMessageProperties(){
		
		super();
		setProperty("action", null);
		setProperty("PasswordType", null);
		setProperty("SignatureParts", null);
		setProperty("SignatureKeyIdentifier", null);
		setProperty("SignatureAlgorithm", null);
		setProperty("EncryptionParts", null);
		setProperty("EncryptionKeyIdentifier", null);
		setProperty("EncryptionAlgorithm", null);
		setProperty("EncryptionKeyTransportAlgorithm", null);
	}

}
