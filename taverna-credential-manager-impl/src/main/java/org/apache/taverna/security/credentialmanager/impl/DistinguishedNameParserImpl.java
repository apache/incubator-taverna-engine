/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.security.credentialmanager.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.DistinguishedNameParser;

/**
 * Utility methods for Credential Manager and security-related stuff.
 * 
 */
public class DistinguishedNameParserImpl implements DistinguishedNameParser{
	private static Logger logger = Logger.getLogger(DistinguishedNameParserImpl.class);

        public DistinguishedNameParserImpl(){
        }
        
	/**
	 * Get the configuration directory where the security stuff will be/is saved
	 * to.
	 */
	public static Path getTheCredentialManagerDefaultDirectory(
			ApplicationConfiguration applicationConfiguration) {
		Path home = applicationConfiguration.getApplicationHomeDir();
		Path secConfigDirectory = home.resolve("security");
		try {
			Files.createDirectories(secConfigDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return secConfigDirectory;
	}

        @Override
	public final Path getCredentialManagerDefaultDirectory(
			ApplicationConfiguration applicationConfiguration) {
		return getTheCredentialManagerDefaultDirectory(applicationConfiguration);
	}

        static URI resolveUriFragment(URI uri, String realm)
			throws URISyntaxException {
		/*
		 * Little hack to encode the fragment correctly - why does not
		 * java.net.URI expose this quoting or have setFragment()?
		 */
		URI fragment = new URI("http", "localhost", "/", realm);
		fragment = fragment.resolve(fragment.getPath()).relativize(fragment);
		return uri.resolve(fragment);
	}

        @Override
	public final URI setFragmentForURI(URI uri, String fragment)
			throws URISyntaxException {
		return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
				uri.getPort(), uri.getPath(), uri.getQuery(), fragment);
	}

        @Override
	public final URI setUserInfoForURI(URI uri, String userinfo)
			throws URISyntaxException {
		return new URI(uri.getScheme(), userinfo, uri.getHost(), uri.getPort(),
				uri.getPath(), uri.getQuery(), uri.getFragment());
	}

        @Override
	public final X509Certificate convertCertificate(Certificate cert)
			throws CMException {
		try {
			// Get the factory for X509 certificates
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// Get the encoded (binary) form of the certificate.
			// For an X509 certificate the encoding will be DER.
			ByteArrayInputStream bais = new ByteArrayInputStream(
					cert.getEncoded());
			// Create the X509 certificate object from the stream
			return (X509Certificate) cf.generateCertificate(bais);
		} catch (CertificateException ex) {
			throw new CMException(
					"Failed to convert the certificate object into X.509 certificate.",
					ex);
		}
	}

	/**
	 * Get the message digest of the given byte array as a string of hexadecimal
	 * characters in the form XX:XX:XX... using the given digest algorithm.
	 */
	public String getMessageDigestAsFormattedString(byte[] messageBytes,
			String digestAlgorithm) {

		MessageDigest messageDigest;
		byte[] digestBytes;
		try {
			messageDigest = MessageDigest.getInstance(digestAlgorithm);
			digestBytes = messageDigest.digest(messageBytes);
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Failed to create message digest.", ex);
			return "";
		}

		// Create the integer value from the digest bytes
		BigInteger number = new BigInteger(1, digestBytes);
		// Convert the integer from decimal to hexadecimal representation
		String hexValueString = number.toString(16).toUpperCase();

		StringBuffer strBuff = new StringBuffer(hexValueString);
		// If the hex number contains odd number of characters -
		// insert a padding "0" at the front of the string
		if ((strBuff.length() % 2) != 0)
			strBuff.insert(0, '0');

		// Insert colons after every two hex characters - start form the end of
		// the hex string
		if (strBuff.length() > 2)
			for (int i = 2; i < strBuff.length(); i += 3)
				strBuff.insert(i, ':');

		return strBuff.toString();
	}


	private String emailAddress; // not from RFC 2253, yet some certificates
									// contain this field

	private String CN;
	private String L;
	private String ST;
	private String C;
	private String O;
	private String OU;

	/**
	 * Parses a DN string and fills in fields with DN parts. Heavily based on
	 * uk.ac.omii.security.utils.DNParser class from omii-security-utils
	 * library.
	 * 
	 * http://maven.omii.ac.uk/maven2/repository/omii/omii-security-utils/
	 */
	public ParsedDistinguishedNameImpl parseDN(String DNstr) {
            return new ParsedDistinguishedNameImpl(DNstr);
        }

}
