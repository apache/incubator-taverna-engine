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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests here require Java strong/unlimited cryptography policy to be installed
 * so they are part of integration tests.
 * 
 * Java strong/unlimited cryptography policy is required to use Credential Manager and
 * use the full security capabilities in Taverna. 
 * 
 * For Java 6, you can get it (together with the installation instructions) from:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 * 
 * @author Alex Nenadic
 *
 */
public class CredentialManagerImplIT {

	private CredentialManagerImpl credentialManager;
	private DummyMasterPasswordProvider masterPasswordProvider;
	private File credentialManagerDirectory;
	
	private static UsernamePassword usernamePassword;
	private static URI serviceURI;
	
	private static Key privateKey;
	private static Certificate[] privateKeyCertChain;
	private static URL privateKeyFileURL = CredentialManagerImplTest.class.getResource(
			"/security/test-private-key-cert.p12");
	private static final String privateKeyAndPKCS12KeystorePassword = "testcert";
	
	private static X509Certificate trustedCertficate;
	private static URL trustedCertficateFileURL = CredentialManagerImplTest.class.getResource(
			"/security/google-trusted-certificate.pem");

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Create a test username and password for a service
		serviceURI =  new URI("http://someservice");
		usernamePassword = new UsernamePassword("testuser", "testpasswd");
		
		// Load the test private key and its certificate
		File privateKeyCertFile = new File(privateKeyFileURL.getPath());
		KeyStore pkcs12Keystore = java.security.KeyStore.getInstance("PKCS12");
		FileInputStream inStream = new FileInputStream(privateKeyCertFile);
		pkcs12Keystore.load(inStream, privateKeyAndPKCS12KeystorePassword.toCharArray());
		// KeyStore pkcs12Keystore = credentialManager.loadPKCS12Keystore(privateKeyCertFile, privateKeyPassword);
		Enumeration<String> aliases = pkcs12Keystore.aliases();
		while (aliases.hasMoreElements()) {
			// The test-private-key-cert.p12 file contains only one private key
			// and corresponding certificate entry
			String alias = aliases.nextElement();
			if (pkcs12Keystore.isKeyEntry(alias)) { // is it a (private) key entry?
				privateKey = pkcs12Keystore.getKey(alias,
						"testcert".toCharArray());
				privateKeyCertChain = pkcs12Keystore.getCertificateChain(alias);
				break;
			}
		}
		inStream.close();
		
		// Load the test trusted certificate (belonging to *.Google.com)
		File trustedCertFile = new File(trustedCertficateFileURL.getPath());		
		inStream = new FileInputStream(trustedCertFile);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		trustedCertficate = (X509Certificate) certFactory.generateCertificate(inStream);
		inStream.close();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		try {
			credentialManager = new CredentialManagerImpl();
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}
		Random randomGenerator = new Random();
		String credentialManagerDirectoryPath = System
				.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ "taverna-security-"
				+ randomGenerator.nextInt(1000000);
		System.out.println("Credential Manager's directory path: "
				+ credentialManagerDirectoryPath);
		credentialManagerDirectory = new File(credentialManagerDirectoryPath);
		try {
			credentialManager
					.setConfigurationDirectoryPath(credentialManagerDirectory);
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}

		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		masterPasswordProvider.setMasterPassword("^kjhf565%£228fg2£@6#"); // set it to something long (longer than 7 characters) so we can test that the strong srypto policy is installed
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Set an empty list for service username and password providers
		credentialManager.setServiceUsernameAndPasswordProviders(new ArrayList<ServiceUsernameAndPasswordProvider>());
	}

	@After
	// Clean up the credentialManagerDirectory we created for testing
	public void cleanUp(){
//		assertTrue(credentialManagerDirectory.exists());
//		assertFalse(credentialManagerDirectory.listFiles().length == 0); // something was created there
	
		if (credentialManagerDirectory.exists()){
			try {
				FileUtils.deleteDirectory(credentialManagerDirectory);				
				System.out.println("Deleting Credential Manager's directory: "
						+ credentialManagerDirectory.getAbsolutePath());
			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}	
		}
	}
//	/**
//	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#exportKeyPair(java.lang.String, java.io.File, java.lang.String)}.
//	 * @throws CMException 
//	 * @throws KeyStoreException 
//	 * @throws NoSuchAlgorithmException 
//	 * @throws UnrecoverableKeyException 
//	 */
//	@Test
//	public void testExportKeyPair() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
//		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
//		File fileToExportTo = new File(credentialManagerDirectory, "test-export-key.p12");
//		credentialManager.exportKeyPair(alias, fileToExportTo, privateKeyAndPKCS12KeystorePassword);
//		assertTrue(fileToExportTo.exists());
//		// Load it back from the file we just saved
//		KeyStore ks = credentialManager.loadPKCS12Keystore(fileToExportTo, privateKeyAndPKCS12KeystorePassword);
//		Enumeration<String> aliases = ks.aliases();
//		Key newPrivateKey = null;
//		Certificate[] newPrivateKeyCerts = null;
//		while (aliases.hasMoreElements()) {
//			// The test-private-key-cert.p12 file contains only one private key
//			// and corresponding certificate entry
//			alias = aliases.nextElement();
//			if (ks.isKeyEntry(alias)) { // is it a (private) key entry?
//				newPrivateKey = ks.getKey(alias,
//						privateKeyAndPKCS12KeystorePassword.toCharArray());
//				newPrivateKeyCerts = ks.getCertificateChain(alias);
//				break;
//			}
//		}
//		assertNotNull(newPrivateKey);
//		assertNotNull(newPrivateKeyCerts);
//		//assertTrue(Arrays.equals(newPrivateKey.getEncoded(), privateKey.getEncoded()));
//		assertTrue(newPrivateKey.equals(privateKey));
//		assertTrue(Arrays.equals(newPrivateKeyCerts, privateKeyCertChain));
//	}
	
}
