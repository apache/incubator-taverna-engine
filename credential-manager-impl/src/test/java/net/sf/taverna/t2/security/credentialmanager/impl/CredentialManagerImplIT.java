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
 * @author Alex Nenadic
 *
 */
public class CredentialManagerImplIT {

	private static CredentialManagerImpl credentialManager;
	private static DummyMasterPasswordProvider masterPasswordProvider;
	private static File credentialManagerDirectory;

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
		masterPasswordProvider.setMasterPassword("uber");
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Set an empty list for trust confirmation providers
		credentialManager.setTrustConfirmationProviders(new ArrayList<TrustConfirmationProvider>());
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
	
//	@Test
//	public void testTrustConfirmationProvidersTrustAlways() throws IOException, CMException {
//		// Initially trust provider list is empty, we only verify by what is in 
//		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
//		
//		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
//		credentialManager.initializeSSL();
//		
//		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
//		HttpsURLConnection conn;
//		conn = (HttpsURLConnection) url.openConnection();
//		try{
//			// This should fail
//			conn.connect();
//			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
//		}
//		catch(SSLHandshakeException sslex){
//			// expected to fail so all is good
//		}
//		finally{
//			conn.disconnect();
//		}
//		
//		// Add the trust confirmation provider that trusts everyone
//		List<TrustConfirmationProvider> trustProviders = new ArrayList<TrustConfirmationProvider>();
//		credentialManager.setTrustConfirmationProviders(trustProviders);
//		trustProviders.add(new TrustAlwaysTrustConfirmationProvider());
//		credentialManager.setTrustConfirmationProviders(trustProviders);
//		
//		HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
//		// This should work now
//		conn2.connect();
//		System.out.println(conn2.getHeaderField(0));
//
//		assertEquals("HTTP/1.1 200 OK", conn.getHeaderField(0));
//		conn2.disconnect();
//	}
//	
//	@Test
//	public void testTrustConfirmationProvidersTrustNever() throws IOException, CMException {
//		// Initially trust provider list is empty, we only verify by what is in 
//		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
//		
//		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
//		credentialManager.initializeSSL();
//		
//		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
//		HttpsURLConnection conn;
//		conn = (HttpsURLConnection) url.openConnection();
//		try{
//			// This should fail
//			conn.connect();
//			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
//		}
//		catch(SSLHandshakeException sslex){
//			// expected to fail so all is good
//		}
//		finally{
//			conn.disconnect();
//		}
//		
//		// Add the trust confirmation provider that trusts no one
//		List<TrustConfirmationProvider> trustProviders = new ArrayList<TrustConfirmationProvider>();
//		credentialManager.setTrustConfirmationProviders(trustProviders);
//		trustProviders = new ArrayList<TrustConfirmationProvider>();
//		trustProviders.add(new TrustNeverTrustConfimationProvider());
//		credentialManager.setTrustConfirmationProviders(trustProviders);
//		
//		HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
//		try{
//			// This should still fail as our trust providers are not trusting anyone
//			// and we have not added heater's certificate to Credential Manager's Truststore
//			conn2.connect();
//			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
//		}
//		catch(SSLHandshakeException sslex){
//			// expected to fail so all is good
//		}
//		finally{
//			conn2.disconnect();
//		}
//	}
//	
//	@Test
//	public void testTrustConfirmationAddCertificateDirectly() throws CMException, IOException{
//		// Initially trust provider list is empty, we only verify by what is in 
//		// Credential Manager's Truststore (and it does not contains the certificate for https://heater.cs.man.ac.uk:7443/)
//		
//		// Do not forget to initialise Taverna's/Credential Manager's SSLSocketFactory
//		credentialManager.initializeSSL();
//		
//		URL url = new URL("https://heater.cs.man.ac.uk:7443/");
//		HttpsURLConnection conn;
//		conn = (HttpsURLConnection) url.openConnection();
//		try{
//			// This should fail
//			conn.connect();
//			fail("Connection to https://heater.cs.man.ac.uk:7443/ should be untrusted at this point.");
//		}
//		catch(SSLHandshakeException sslex){
//			// expected to fail so all is good
//		}
//		finally{
//			conn.disconnect();
//		}
//		
//		// Add heater's certificate directly to Credential Manager's Truststore
//		
//	}
}
