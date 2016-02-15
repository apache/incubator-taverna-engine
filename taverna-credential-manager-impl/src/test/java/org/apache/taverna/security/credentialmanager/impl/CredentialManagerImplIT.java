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

import static org.junit.Assert.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.KeystoreChangedEvent;
import org.apache.taverna.security.credentialmanager.MasterPasswordProvider;
import org.apache.taverna.security.credentialmanager.TrustConfirmationProvider;
import org.apache.taverna.security.credentialmanager.UsernamePassword;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
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
 * These tests use an existing keystore (in resources/security/t2keystore.ubr) and 
 * truststore (in resources/security/t2truststore.ubr) that are not empty.
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
	
	private static X509Certificate trustedCertficateGoogle;
	private static URL trustedCertficateGoogleFileURL = CredentialManagerImplTest.class.getResource(
			"/security/google-trusted-certificate.pem");
	private static X509Certificate trustedCertficateHeater;
	private static URL trustedCertficateHeaterFileURL = CredentialManagerImplTest.class.getResource(
			"/security/tomcat_heater_certificate.pem");
	
	private static Observer<KeystoreChangedEvent> keystoreChangedObserver;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	@Ignore
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
		File trustedCertFile = new File(trustedCertficateGoogleFileURL.getPath());		
		inStream = new FileInputStream(trustedCertFile);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		trustedCertficateGoogle = (X509Certificate) certFactory.generateCertificate(inStream);
		try{
			inStream.close();
		}
		catch (Exception e) {
			// Ignore
		}
		// Load the test trusted certificate (belonging to heater.cs.man.ac.uk)
		File trustedCertFile2 = new File(trustedCertficateHeaterFileURL.getPath());		
		inStream = new FileInputStream(trustedCertFile2);
		trustedCertficateHeater = (X509Certificate) certFactory.generateCertificate(inStream);
		try{
			inStream.close();
		}
		catch (Exception e) {
			// Ignore
		}	
		
		credentialManager = new CredentialManagerImpl();

//		// The code below sets up the Keystore and Truststore files and loads some data into them
//		// and saves them into a temp directory. These files can later be used for testing the Credential
//		// Manager with non-empty keystores.
//		Random randomGenerator = new Random();
//		String credentialManagerDirectoryPath = System
//				.getProperty("java.io.tmpdir")
//				+ System.getProperty("file.separator")
//				+ "taverna-security-"
//				+ randomGenerator.nextInt(1000000);
//		System.out.println("Credential Manager's directory path: "
//				+ credentialManagerDirectoryPath);
//		credentialManagerDirectory = new File(credentialManagerDirectoryPath);
//		credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory);
//		
//		// Create the dummy master password provider
//		masterPasswordProvider = new DummyMasterPasswordProvider();
//		masterPasswordProvider.setMasterPassword(masterPassword);
//		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
//		masterPasswordProviders.add(masterPasswordProvider);
//		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
//		
//		// Add some stuff into Credential Manager
//		credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
//		credentialManager.addUsernameAndPasswordForService(usernamePassword2, serviceURI2);
//		credentialManager.addUsernameAndPasswordForService(usernamePassword3, serviceURI3);
//		credentialManager.addKeyPair(privateKey, privateKeyCertChain);
//		credentialManager.addTrustedCertificate(trustedCertficate);

		
		// Set up a random temp directory and copy the test keystore files 
		// from resources/security
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
		URL keystoreFileURL = CredentialManagerImplIT.class
				.getResource("/security/t2keystore.ubr");
		File keystoreFile = new File(keystoreFileURL.getPath());
		File keystoreDestFile = new File(credentialManagerDirectory,
				"taverna-keystore.ubr");
		URL truststroreFileURL = CredentialManagerImplIT.class
				.getResource("/security/t2truststore.ubr");
		File truststoreFile = new File(truststroreFileURL.getPath());
		File truststoreDestFile = new File(credentialManagerDirectory,
				"taverna-truststore.ubr");
		FileUtils.copyFile(keystoreFile, keystoreDestFile);
		FileUtils.copyFile(truststoreFile, truststoreDestFile);
		credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory.toPath());
		
		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		masterPasswordProvider.setMasterPassword(masterPassword);
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
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
	
	@AfterClass
	@Ignore
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
	
	@Test
	@Ignore
	public void testCredentialManager() throws CMException, URISyntaxException, IOException{
		
		// There are 3 service username and password entries in the Keystore
		List<URI> serviceList = credentialManager.getServiceURIsForAllUsernameAndPasswordPairs();
		assertTrue(serviceList.size() == 3);
		System.out.println();
		assertTrue(serviceList.contains(serviceURI2));
		
		credentialManager.deleteUsernameAndPasswordForService(serviceURI3);
		assertFalse(credentialManager.hasUsernamePasswordForService(serviceURI3));
		
		// There are 2 private/public key pair entries in the Keystore
		credentialManager.hasKeyPair(privateKey, privateKeyCertChain);
		
		// There are Google's and heater.cs.man.ac's trusted certificates in the Truststore
		credentialManager.hasTrustedCertificate(trustedCertficateGoogle);
		// Open a HTTPS connection to Google
		URL url = new URL("https://code.google.com/p/taverna/");
		HttpsURLConnection conn;
		conn = (HttpsURLConnection) url.openConnection();
		// This should work
		conn.connect();
		assertEquals("HTTP/1.1 200 OK", conn.getHeaderField(0));
		conn.disconnect();
		
		credentialManager.hasTrustedCertificate(trustedCertficateHeater);
		// Open a HTTPS connection to heater
		url = new URL("https://heater.cs.man.ac.uk:7443/");
		conn = (HttpsURLConnection) url.openConnection();
		// This should work
		conn.connect();
		assertEquals("HTTP/1.1 200 OK", conn.getHeaderField(0));
		conn.disconnect();
		
	}
	
	public void generateKeystores() throws Exception{
		
		setUpBeforeCLass();
		
		// The code below sets up the Keystore and Truststore files and loads some data into them
		// and saves them into a temp directory. These files can later be used for testing the Credential
		// Manager with non-empty keystores.
		Random randomGenerator = new Random();
		String credentialManagerDirectoryPath = System
				.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator")
				+ "taverna-security-"
				+ randomGenerator.nextInt(1000000);
		System.out.println("Credential Manager's Keystore and Truststore will be saved to: "
				+ credentialManagerDirectoryPath);
		credentialManagerDirectory = new File(credentialManagerDirectoryPath);
		credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory.toPath());
		
		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
//		masterPasswordProvider.setMasterPassword(masterPassword);
		masterPasswordProvider.setMasterPassword("uber");
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Add some stuff into Credential Manager
		credentialManager.addUsernameAndPasswordForService(usernamePassword, new URI("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));

//		credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
//		credentialManager.addUsernameAndPasswordForService(usernamePassword2, serviceURI2);
//		credentialManager.addUsernameAndPasswordForService(usernamePassword3, serviceURI3);
//		credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		credentialManager.addTrustedCertificate(trustedCertficateHeater);
	}
	
	
}
