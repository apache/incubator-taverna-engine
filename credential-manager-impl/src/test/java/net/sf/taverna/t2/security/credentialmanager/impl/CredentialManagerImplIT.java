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

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.credentialmanager.JavaTruststorePasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.KeystoreChangedEvent;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmationProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests here require Java strong/unlimited cryptography policy to be installed
 * so they are part of integration tests.
 * 
 * Java strong/unlimited cryptography policy is required to use the Credential Manager and
 * the full security capabilities in Taverna. Java by default comes with the weak policy 
 * that disables the use of certain cryto algorithms and bigger key sizes. Although 
 * it is claimed that as of Java 6 the default policy is strong, we have seen otherwise, 
 * so make sure you install it.
 * 
 * For Java 6, strong/unlimited cryptography policy can be downloaded 
 * (together with the installation instructions) from:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 * 
 * These tests use an existing keystore (in resources/security/taverna-keystore.ubr) and 
 * truststore (in resources/security/taverna-truststore.ubr) that are not empty.
 * 
 * @author Alex Nenadic
 *
 */
public class CredentialManagerImplIT {

	private static CredentialManagerImpl credentialManager;
	// Master password for Credential Manager's Keystore and Truststore
	private static String masterPassword = "(cl%ZDxu66AN/{vNXbLF";  
	private static DummyMasterPasswordProvider masterPasswordProvider;
	private static File credentialManagerDirectory;
	
	private static UsernamePassword usernamePassword;
	private static URI serviceURI;
	private static UsernamePassword usernamePassword2;
	private static URI serviceURI2;
	private static UsernamePassword usernamePassword3;
	private static URI serviceURI3;
	
	private static Key privateKey;
	private static Certificate[] privateKeyCertChain;
	private static URL privateKeyFileURL = CredentialManagerImplTest.class.getResource(
			"/security/test-private-key-cert.p12");
	private static final String privateKeyAndPKCS12KeystorePassword = "test"; // password for the test PKCS#12 keystore in resources
	
	private static X509Certificate trustedCertficate;
	private static URL trustedCertficateFileURL = CredentialManagerImplTest.class.getResource(
			"/security/google-trusted-certificate.pem");

	private static Observer<KeystoreChangedEvent> keystoreChangedObserver;

	/**
	 * @throws java.lang.Exception
	 */
	//@BeforeClass
	//@Ignore
	public static void setUpBeforeCLass() throws Exception {

		Security.addProvider(new BouncyCastleProvider());
		
		// Create some test username and passwords for services
		serviceURI =  new URI("http://someservice");
		usernamePassword = new UsernamePassword("testuser", "testpasswd");
		serviceURI2 =  new URI("http://someservice2");
		usernamePassword2 = new UsernamePassword("testuser2", "testpasswd2");
		serviceURI3 =  new URI("http://someservice3");
		usernamePassword3 = new UsernamePassword("testuser3", "testpasswd3");
		
		// Load the test private key and its certificate
		File privateKeyCertFile = new File(privateKeyFileURL.getPath());
		KeyStore pkcs12Keystore = java.security.KeyStore.getInstance("PKCS12", "BC"); // We have to use the BC provider here as the certificate chain is not loaded if we use whichever provider is first in Java!!!
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
						privateKeyAndPKCS12KeystorePassword.toCharArray());
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
		try{
			inStream.close();
		}
		catch (Exception e) {
			// Ignore
		}
		
		credentialManager = new CredentialManagerImpl();

		// Set up a temporary "security" directory and copy the Keystore and Truststore files there
		// so that we are working with existing keystores, not empty fresh ones
		Random randomGenerator = new Random();
		String credentialManagerDirectoryPath = System
				.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ "taverna-security-"
				+ randomGenerator.nextInt(1000000);
		System.out.println("Credential Manager's directory path: "
				+ credentialManagerDirectoryPath);
		credentialManagerDirectory = new File(credentialManagerDirectoryPath);
		if (!credentialManagerDirectory.exists()) {
			credentialManagerDirectory.mkdir();
		}
		URL keystoreFileURL = CredentialManagerImplIT.class.getResource("/security/taverna-keystore.ubr");
		File keystoreFile = new File(keystoreFileURL.getPath());
		File keystoreDestFile = new File(credentialManagerDirectory, "taverna-keystore.ubr");
		URL truststroreFileURL = CredentialManagerImplIT.class.getResource("/security/taverna-truststore.ubr");
		File truststoreFile = new File(truststroreFileURL.getPath());
		File truststoreDestFile = new File(credentialManagerDirectory, "taverna-truststore.ubr");
		FileUtils.copyFile(keystoreFile, keystoreDestFile);
		FileUtils.copyFile(truststoreFile, truststoreDestFile);
		credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory);
		
		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		masterPasswordProvider.setMasterPassword(masterPassword);
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Add some stuff into Credential Manager
		credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
		credentialManager.addUsernameAndPasswordForService(usernamePassword2, serviceURI2);
		credentialManager.addUsernameAndPasswordForService(usernamePassword3, serviceURI3);
		credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		
		// Now start a new Credential Manager that will pick up the new directory with the
		// preloaded keystores	
		
		credentialManager = new CredentialManagerImpl();
		credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory);
		
		// Continue setting up Credential Manager ...
		
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);

		// Set an empty list for trust confirmation providers
		credentialManager.setTrustConfirmationProviders(new ArrayList<TrustConfirmationProvider>());
		
		keystoreChangedObserver = new Observer<KeystoreChangedEvent>() {		
			@Override
			public void notify(Observable<KeystoreChangedEvent> sender,
					KeystoreChangedEvent message) throws Exception {
				// TODO Auto-generated method stub
			}
		};
		credentialManager.addObserver(keystoreChangedObserver);
	}
	
	//@AfterClass
	//@Ignore
	// Clean up the credentialManagerDirectory we created for testing
	public static void cleanUp(){

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
	
	//@Test
	//@Ignore
	public void testCredentialManager() throws CMException, URISyntaxException{
		
		// There are 9 service username and password entries in the Keystore
		List<URI> serviceList = credentialManager.getServiceURIsForAllUsernameAndPasswordPairs();
		assertTrue(serviceList.size() == 9);
		System.out.println();
		assertTrue(serviceList.contains(new URI("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-DigestPassword-Timestamp?wsdl")));
		
		
		String alias = credentialManager.addUsernameAndPasswordForService(usernamePassword,serviceURI);
		
		UsernamePassword testUsernamePassword = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNotNull(testUsernamePassword);
		assertTrue(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));
		assertTrue(Arrays.equals(usernamePassword.getPassword(), testUsernamePassword.getPassword()));
		assertTrue(usernamePassword.getUsername().equals(testUsernamePassword.getUsername()));
		assertTrue(credentialManager.getServiceURIsForAllUsernameAndPasswordPairs().size() == 10);

		
		// Get username and password for service http://heater.cs.man.ac.uk:7070/axis/services/HelloService-DigestPassword-Timestamp?wsdl
		UsernamePassword usernameAndPassword = credentialManager
				.getUsernameAndPasswordForService(
						new URI(
								"https://heater.cs.man.ac.uk:7443/axis/services/HelloService-PlaintextPassword?wsdl"),
						true, "");
		assertNotNull(usernameAndPassword);
		assertEquals(usernameAndPassword.getPassword(), "testpasswd".toCharArray());
		assertEquals(usernameAndPassword.getUsername(), "testuser");
		
		// There are 2 private/public key pair entries in the Keystore

	}
}
