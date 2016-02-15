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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;

import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.MasterPasswordProvider;
import org.apache.taverna.security.credentialmanager.TrustConfirmationProvider;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HTTPSConnectionAndTrustConfirmationIT {

	
	private static CredentialManagerImpl credentialManager;
	private static DummyMasterPasswordProvider masterPasswordProvider;
	private static File credentialManagerDirectory;
	//private static URL trustedCertficateFileURL = HTTPSConnectionAndTrustConfirmationIT.class.getResource("/security/tomcat_heater_certificate.pem");
	
	// Log4J Logger
	//private static Logger logger = Logger.getLogger(HTTPSConnectionAndTrustConfirmationIT.class);

//	public static void main(String[] args){
//		
//		try {
//			CredentialManagerOld.initialiseSSL();
//			//CredentialManager.getInstance();
//			//HttpsURLConnection.setDefaultSSLSocketFactory(CredentialManager.createTavernaSSLSocketFactory());
//			URL url = new URL ("https://rpc103.cs.man.ac.uk:8443/wsrf/services/cagrid/SecureHelloWorld?wsdl");
//			HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
//			// user should be asked automatically if they want to trust the connection
//			httpsConnection.connect();
//			
//		} catch (CMException e) {
//			logger.error("", e);
//		} catch (MalformedURLException e) {
//			logger.error("", e);
//		} catch (IOException e) {
//			logger.error("", e);
//		}
//		catch(Exception ex){ // anything we did not expect
//			logger.error("", ex);
//		}
//		
//	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Just in case, add the BouncyCastle provider
		// It gets added from the CredentialManagerImpl constructor as well
		// but we may need some crypto operations before we invoke the Cred. Manager 
		Security.addProvider(new BouncyCastleProvider());
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
					.setConfigurationDirectoryPath(credentialManagerDirectory.toPath());
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}

		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		masterPasswordProvider.setMasterPassword("uber");
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Set an empty list for trust confirmation providers
		credentialManager.setTrustConfirmationProviders(new ArrayList<TrustConfirmationProvider>());
	}
	
	@After
	// Clean up the credentialManagerDirectory we created for testing
	public void cleanUp() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException{
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
		
		// Reset the SSLSocketFactory in JVM so we always have a clean start
		SSLContext sc = null;
		sc = SSLContext.getInstance("SSLv3");
		
		// Create a "default" JSSE X509KeyManager.
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509",
				"SunJSSE");
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null, null);
		kmf.init(ks, "blah".toCharArray());
		
		// Create a "default" JSSE X509TrustManager.
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(
				"SunX509", "SunJSSE");
		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(null, null);
		tmf.init(ts);
		
		sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		SSLContext.setDefault(sc);		
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	
	@Test
	public void testTrustConfirmationProvidersTrustAlways() throws IOException, CMException {
		// Initially trust provider list is empty, we only verify by what is in 
		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
		
		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
		credentialManager.initializeSSL();
		
		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
		HttpsURLConnection conn;
		conn = (HttpsURLConnection) url.openConnection();
		try{
			// This should fail
			conn.connect();
			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
		}
		catch(SSLHandshakeException sslex){
			// expected to fail so all is good
			System.out.println(sslex.getStackTrace());
		}
		finally{
			conn.disconnect();
		}
		
		// Add the trust confirmation provider that trusts everyone
		List<TrustConfirmationProvider> trustProviders = new ArrayList<TrustConfirmationProvider>();
		trustProviders.add(new TrustAlwaysTrustConfirmationProvider());
		credentialManager.setTrustConfirmationProviders(trustProviders);
		
		HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
		// This should work now
		conn2.connect();
		System.out.println("Status header: "+ conn2.getHeaderField(0));

		assertEquals("HTTP/1.1 200 OK", conn2.getHeaderField(0));
		conn2.disconnect();
	}
	
	@Test
	public void testTrustConfirmationProvidersTrustNever() throws IOException, CMException {
		// Initially trust provider list is empty, we only verify by what is in 
		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
		
		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
		credentialManager.initializeSSL();
		
		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
		HttpsURLConnection conn;
		conn = (HttpsURLConnection) url.openConnection();
		try{
			// This should fail
			conn.connect();
			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
		}
		catch(SSLHandshakeException sslex){
			// expected to fail so all is good
		}
		finally{
			conn.disconnect();
		}
		
		// Add the trust confirmation provider that trusts no one
		List<TrustConfirmationProvider> trustProviders = new ArrayList<TrustConfirmationProvider>();
		credentialManager.setTrustConfirmationProviders(trustProviders);
		trustProviders = new ArrayList<TrustConfirmationProvider>();
		trustProviders.add(new TrustNeverTrustConfimationProvider());
		credentialManager.setTrustConfirmationProviders(trustProviders);
		
		HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
		try{
			// This should still fail as our trust providers are not trusting anyone
			// and we have not added heater's certificate to Credential Manager's Truststore
			conn2.connect();
			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
		}
		catch(SSLHandshakeException sslex){
			// expected to fail so all is good
		}
		finally{
			conn2.disconnect();
		}
	}
	
	@Test
	public void testTrustConfirmationAddDeleteCertificateDirectly() throws CMException, IOException, CertificateException{
		// Initially trust provider list is empty, we only verify by what is in 
		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
		
		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
		credentialManager.initializeSSL();
		
		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
		HttpsURLConnection conn;
		conn = (HttpsURLConnection) url.openConnection();
		try{
			// This should fail
			conn.connect();
			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
		}
		catch(SSLHandshakeException sslex){
			// expected to fail so all is good
		}
		finally{
			conn.disconnect();
		}
		
		// Add heater's certificate directly to Credential Manager's Truststore
		
		// Load the test trusted certificate (belonging to heater.cs.man.ac.uk)
		X509Certificate trustedCertficate;
		URL trustedCertficateFileURL = getClass().getResource("/security/tomcat_heater_certificate.pem");
		System.out.println("testTrustConfirmationAddDeleteCertificateDirectly: trusted certficate file URL " + trustedCertficateFileURL);
		File trustedCertFile = new File(trustedCertficateFileURL.getPath());		
		FileInputStream inStream = new FileInputStream(trustedCertFile);
		//InputStream inStream = getClass().getClassLoader().getResourceAsStream("security/tomcat_heater_certificate.pem");
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		trustedCertficate = (X509Certificate) certFactory.generateCertificate(inStream);
		try{
			inStream.close();
		}
		catch (Exception e) {
			// Ignore
		}
		String alias = credentialManager.addTrustedCertificate(trustedCertficate);
		
		HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
		// This should work now
		conn2.connect();
		//System.out.println(conn2.getHeaderField(0));

		assertEquals("HTTP/1.1 200 OK", conn2.getHeaderField(0));
		conn2.disconnect();
		
		// Now remove certificate and see if the "trust" changes
		credentialManager.deleteTrustedCertificate(alias);
		HttpsURLConnection conn3;
		conn3 = (HttpsURLConnection) url.openConnection();
		try{
			// This should fail
			conn3.connect();
			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
		}
		catch(SSLHandshakeException sslex){
			// expected to fail so all is good
		}
		finally{
			conn3.disconnect();
		}
	}
}
