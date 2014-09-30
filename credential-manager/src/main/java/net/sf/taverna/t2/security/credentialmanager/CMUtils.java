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
package net.sf.taverna.t2.security.credentialmanager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CMException;

import org.apache.log4j.Logger;

import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * Utility methods for Credential Manager and security-related stuff.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public class CMUtils {
	private static Logger logger = Logger.getLogger(CMUtils.class);

	/**
	 * Get the configuration directory where the security stuff will be/is saved
	 * to.
	 */
	public static File getCredentialManagerDefaultDirectory(
			ApplicationConfiguration applicationConfiguration) {
		File home = applicationConfiguration.getApplicationHomeDir();
		File secConfigDirectory = new File(home, "security");
		if (!secConfigDirectory.exists())
			secConfigDirectory.mkdir();
		return secConfigDirectory;
	}

	public static URI resolveUriFragment(URI uri, String realm)
			throws URISyntaxException {
		/*
		 * Little hack to encode the fragment correctly - why does not
		 * java.net.URI expose this quoting or have setFragment()?
		 */
		URI fragment = new URI("http", "localhost", "/", realm);
		fragment = fragment.resolve(fragment.getPath()).relativize(fragment);
		return uri.resolve(fragment);
	}

	public static URI setFragmentForURI(URI uri, String fragment)
			throws URISyntaxException {
		return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
				uri.getPort(), uri.getPath(), uri.getQuery(), fragment);
	}

	public static URI setUserInfoForURI(URI uri, String userinfo)
			throws URISyntaxException {
		return new URI(uri.getScheme(), userinfo, uri.getHost(), uri.getPort(),
				uri.getPath(), uri.getQuery(), uri.getFragment());
	}

	/**
	 * Convert the certificate object into an X509Certificate object.
	 */
	public static X509Certificate convertCertificate(Certificate cert)
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
	public static String getMessageDigestAsFormattedString(byte[] messageBytes,
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

}
