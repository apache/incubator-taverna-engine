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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager.KeystoreType;
import net.sf.taverna.t2.security.credentialmanager.JavaTruststorePasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.KeystoreChangedEvent;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmationProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests here should not require Java strong/unlimited cryptography policy to be installed, 
 * although if something goes wrong that is the first thing to be checked for.
 * 
 * Java by default comes with the weak policy 
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
public class CredentialManagerImplTest {
	
	private CredentialManagerImpl credentialManager;
	private DummyMasterPasswordProvider masterPasswordProvider;
	private File credentialManagerDirectory;
	
	private static UsernamePassword usernamePassword;
	private static URI serviceURI;
	
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
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Just in case, add the BouncyCastle provider
		// It gets added from the CredentialManagerImpl constructor as well
		// but we may need some crypto operations before we invoke the Cred. Manager 
		Security.addProvider(new BouncyCastleProvider());

		// Create a test username and password for a service
		serviceURI =  new URI("http://someservice");
		usernamePassword = new UsernamePassword("testuser", "testpasswd");
		
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
		
		keystoreChangedObserver = new Observer<KeystoreChangedEvent>() {
			
			@Override
			public void notify(Observable<KeystoreChangedEvent> sender,
					KeystoreChangedEvent message) throws Exception {
				// TODO Auto-generated method stub
				
			}
		};
		
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
		masterPasswordProvider.setMasterPassword("uber");
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Set an empty list for service username and password providers
		credentialManager.setServiceUsernameAndPasswordProviders(new ArrayList<ServiceUsernameAndPasswordProvider>());

		credentialManager.setJavaTruststorePasswordProviders(new ArrayList<JavaTruststorePasswordProvider>());

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
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#CredentialManagerImpl()}.
	 */
	@Test
	@Ignore
	public void testCredentialManagerImpl() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getUsernameAndPasswordForService(java.net.URI, boolean, java.lang.String)}.
	 * @throws URISyntaxException 
	 * @throws CMException 
	 */
	@Test
	public void testGetUsernameAndPasswordForServiceURI() throws URISyntaxException, CMException {
		// The Credential Manage's Keystore is empty so we should not be able to find anything initially
		assertNull(credentialManager.getUsernameAndPasswordForService(serviceURI, false, ""));
		
		credentialManager.addUsernameAndPasswordForService(usernamePassword,serviceURI);
		
		UsernamePassword testUsernamePassword = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNotNull(testUsernamePassword);
		assertTrue(Arrays.equals(usernamePassword.getPassword(), testUsernamePassword.getPassword()));
		assertTrue(usernamePassword.getUsername().equals(testUsernamePassword.getUsername()));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addUsernameAndPasswordForService(net.sf.taverna.t2.security.credentialmanager.UsernamePassword, java.net.URI)}.
	 * @throws URISyntaxException 
	 * @throws CMException 
	 */
	@Test
	public void testAddUsernameAndPasswordForService() throws CMException, URISyntaxException {

		String alias = credentialManager.addUsernameAndPasswordForService(usernamePassword,serviceURI);
		
		UsernamePassword testUsernamePassword = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNotNull(testUsernamePassword);
		assertTrue(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));
		assertTrue(Arrays.equals(usernamePassword.getPassword(), testUsernamePassword.getPassword()));
		assertTrue(usernamePassword.getUsername().equals(testUsernamePassword.getUsername()));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#deleteUsernameAndPasswordForService(java.net.URI)}.
	 * @throws URISyntaxException 
	 * @throws CMException 
	 */
	@Test
	public void testDeleteUsernameAndPasswordForServiceURI() throws URISyntaxException, CMException {

		// The Credential Manage's Keystore is empty initially so this should 
		// have no effect apart from initializing the Keystore/Truststore
		credentialManager.deleteUsernameAndPasswordForService(serviceURI);
		
		credentialManager.addUsernameAndPasswordForService(usernamePassword,serviceURI);	
		credentialManager.deleteUsernameAndPasswordForService(serviceURI);
		
		assertNull(credentialManager.getUsernameAndPasswordForService(serviceURI, false, ""));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addKeyPair(java.security.Key, java.security.cert.Certificate[])}.
	 * @throws CMException 
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 */
	@Test
	public void testAddKeyPair() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {

		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		assertTrue(credentialManager.hasKeyPair(privateKey, privateKeyCertChain));
		assertTrue(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));

		credentialManager.deleteKeyPair(alias);
		assertFalse(credentialManager.hasKeyPair(privateKey, privateKeyCertChain));
		assertFalse(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#hasKeyPair(java.security.Key, java.security.cert.Certificate[])}.
	 * @throws CMException 
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 */
	@Test
	public void testHasKeyPair() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		assertFalse(credentialManager.hasKeyPair(privateKey, privateKeyCertChain));
		credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		assertTrue(credentialManager.hasKeyPair(privateKey, privateKeyCertChain));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#deleteKeyPair(java.lang.String)}.
	 * @throws CMException 
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 */
	@Test
	public void testDeleteKeyPair() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		// The Credential Manage's Keystore is empty initially so this should 
		// have no effect apart from initializing the Keystore/Truststore
		credentialManager.deleteKeyPair("somealias");
		
		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		credentialManager.deleteKeyPair(alias);
		assertFalse(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#exportKeyPair(java.lang.String, java.io.File, java.lang.String)}.
	 * @throws CMException 
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 */
	@Test
	public void testExportKeyPair() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		File fileToExportTo = new File(credentialManagerDirectory, "test-export-key.p12");
		credentialManager.exportKeyPair(alias, fileToExportTo, privateKeyAndPKCS12KeystorePassword);
		assertTrue(fileToExportTo.exists());
		// Load it back from the file we just saved
		KeyStore ks = credentialManager.loadPKCS12Keystore(fileToExportTo, privateKeyAndPKCS12KeystorePassword);
		Enumeration<String> aliases = ks.aliases();
		Key newPrivateKey = null;
		Certificate[] newPrivateKeyCerts = null;
		while (aliases.hasMoreElements()) {
			// The test-private-key-cert.p12 file contains only one private key
			// and corresponding certificate entry
			alias = aliases.nextElement();
			if (ks.isKeyEntry(alias)) { // is it a (private) key entry?
				newPrivateKey = ks.getKey(alias,
						privateKeyAndPKCS12KeystorePassword.toCharArray());
				newPrivateKeyCerts = ks.getCertificateChain(alias);
				break;
			}
		}
		assertNotNull(newPrivateKey);
		assertNotNull(newPrivateKeyCerts);
		//assertTrue(Arrays.equals(newPrivateKey.getEncoded(), privateKey.getEncoded()));
		assertTrue(newPrivateKey.equals(privateKey));
		assertTrue(Arrays.equals(newPrivateKeyCerts, privateKeyCertChain));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getCertificate(java.lang.String, java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testGetCertificate() throws CMException {
		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		// Get certificate from the Keystore associated with the private key we just inserted
		Certificate privateKeyCertificate = credentialManager.getCertificate(CredentialManager.KeystoreType.KEYSTORE, alias);
		assertNotNull(privateKeyCertificate);
		assertTrue(privateKeyCertChain[0].equals(privateKeyCertificate));
		
		// We should also have some trusted certificates in the Truststore
		// Need to get their aliases
		ArrayList<String> truststoreAliases = credentialManager.getAliases(CredentialManager.KeystoreType.TRUSTSTORE);
		assertTrue(!truststoreAliases.isEmpty());
		// Just get the first one
		Certificate trustedCertificate = credentialManager.getCertificate(CredentialManager.KeystoreType.TRUSTSTORE, truststoreAliases.get(0));
		assertNotNull(trustedCertificate);
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getKeyPairCertificateChain(java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testGetKeyPairCertificateChain() throws CMException {
		String alias = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		Certificate[] keyPairCertificateChain = credentialManager.getKeyPairCertificateChain(alias);
		assertNotNull(keyPairCertificateChain);
		assertTrue(Arrays.equals(privateKeyCertChain, keyPairCertificateChain));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addTrustedCertificate(java.security.cert.X509Certificate)}.
	 * @throws CMException 
	 */
	@Test
	public void testAddTrustedCertificate() throws CMException {
		
		String alias = credentialManager.addTrustedCertificate(trustedCertficate);
		assertTrue(credentialManager.hasTrustedCertificate(trustedCertficate));
		assertTrue(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.TRUSTSTORE, alias));

		credentialManager.deleteTrustedCertificate(alias);
		assertFalse(credentialManager.hasTrustedCertificate(trustedCertficate));
		assertFalse(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.TRUSTSTORE, alias));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getX509CertificateAlias(java.security.cert.X509Certificate)}.
	 * @throws CMException 
	 */
	@Test
	public void testGetX509CertificateAlias() throws CMException {

		String alias = credentialManager.getX509CertificateAlias(trustedCertficate);
		String alias2 = credentialManager.addTrustedCertificate(trustedCertficate);
		assertEquals(alias, alias2);

	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#deleteTrustedCertificate(java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testDeleteTrustedCertificate() throws CMException {
		// The Credential Manage's Truststore is empty initially so this should 
		// have no effect apart from initializing the Keystore/Truststore
		credentialManager.deleteTrustedCertificate("somealias");
		
		String alias = credentialManager.addTrustedCertificate(trustedCertficate);
		credentialManager.deleteTrustedCertificate(alias);
		assertFalse(credentialManager.hasTrustedCertificate(trustedCertficate));
		assertFalse(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.TRUSTSTORE, alias));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#isKeyEntry(java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testIsKeyEntry() throws CMException {
		// The Credential Manage's Keystore/Truststore is empty initially so this should 
		// have no effect apart from initializing them
		// This should throw an exception
		assertFalse(credentialManager.isKeyEntry("somealias"));

		String aliasPassword = credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
		String aliasKeyPair = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		String aliasTrustedCert = credentialManager.addTrustedCertificate(trustedCertficate);

		assertTrue(credentialManager.isKeyEntry(aliasPassword)); // passwords are saves as symmetric key entries
		assertTrue(credentialManager.isKeyEntry(aliasKeyPair));
		assertFalse(credentialManager.isKeyEntry(aliasTrustedCert));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#hasEntryWithAlias(java.lang.String, java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testHasEntryWithAlias() throws CMException {
		
		String aliasTrustedCert = credentialManager.getX509CertificateAlias(trustedCertficate);
		assertFalse(credentialManager.hasEntryWithAlias(KeystoreType.TRUSTSTORE, aliasTrustedCert));
		
		String aliasTrustedCert2 = credentialManager.addTrustedCertificate(trustedCertficate);
		assertTrue(credentialManager.hasEntryWithAlias(KeystoreType.TRUSTSTORE, aliasTrustedCert2));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getAliases(net.sf.taverna.t2.security.credentialmanager.CredentialManager.KeystoreType)}.
	 * @throws CMException 
	 */
	@Test
	public void testGetAliases() throws CMException {
		
		ArrayList<String> keystoreAliases = credentialManager.getAliases(KeystoreType.KEYSTORE);
		ArrayList<String> truststoreAliases = credentialManager.getAliases(KeystoreType.TRUSTSTORE);
		
		// Initially Keystore/Truststore is empty
		assertTrue(keystoreAliases.isEmpty());
		
		String aliasPassword = credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
		String aliasKeyPair = credentialManager.addKeyPair(privateKey, privateKeyCertChain);
		String aliasTrustedCert = credentialManager.addTrustedCertificate(trustedCertficate);
		
		keystoreAliases = credentialManager.getAliases(KeystoreType.KEYSTORE);
		truststoreAliases = credentialManager.getAliases(KeystoreType.TRUSTSTORE);
		
		assertTrue(keystoreAliases.size() == 2);
		assertTrue(truststoreAliases.size() >= 1); // we at least have the one we inserted but could be more copied from Java's defauls truststore
		
		assertTrue(keystoreAliases.contains(aliasPassword));
		assertTrue(keystoreAliases.contains(aliasKeyPair));
		assertTrue(truststoreAliases.contains(aliasTrustedCert));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getServiceURIsForAllUsernameAndPasswordPairs()}.
	 * @throws CMException 
	 * @throws URISyntaxException 
	 */
	@Test
	public void testGetServiceURIsForAllUsernameAndPasswordPairs() throws CMException, URISyntaxException {
		// Initially empty so this
		assertTrue(credentialManager.getServiceURIsForAllUsernameAndPasswordPairs().isEmpty());
		
		credentialManager.addUsernameAndPasswordForService(usernamePassword, serviceURI);
		
		URI serviceURI2 = new URI("http://someservice2");
		UsernamePassword usernamePassword2 = new UsernamePassword("testuser2", "testpasswd2");
		credentialManager.addUsernameAndPasswordForService(usernamePassword2, serviceURI2);
		
		List<URI> serviceURIs = credentialManager.getServiceURIsForAllUsernameAndPasswordPairs();
		assertTrue(credentialManager.getServiceURIsForAllUsernameAndPasswordPairs().size() == 2);
		assertTrue(serviceURIs.contains(serviceURI));
		assertTrue(serviceURIs.contains(serviceURI2));

	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#loadPKCS12Keystore(java.io.File, java.lang.String)}.
	 * @throws CMException 
	 * @throws KeyStoreException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 */
	@Test
	public void testLoadPKCS12Keystore() throws CMException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		KeyStore pkcs12Keystore = credentialManager.loadPKCS12Keystore(new File(privateKeyFileURL.getPath()), privateKeyAndPKCS12KeystorePassword);
		
		Key privateKey2 = null;
		Certificate[] privateKeyCertChain2 = null;
		
		Enumeration<String> aliases = pkcs12Keystore.aliases();
		while (aliases.hasMoreElements()) {
			// The test-private-key-cert.p12 file contains only one private key
			// and corresponding certificate entry
			String alias = aliases.nextElement();
			if (pkcs12Keystore.isKeyEntry(alias)) { // is it a (private) key entry?
				privateKey2 = pkcs12Keystore.getKey(alias,
						privateKeyAndPKCS12KeystorePassword.toCharArray());
				privateKeyCertChain2 = pkcs12Keystore.getCertificateChain(alias);
				break;
			}
		}
		assertNotNull(privateKey2);
		assertNotNull(privateKeyCertChain2);
	}
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addObserver(net.sf.taverna.t2.lang.observer.Observer)}.
	 */
	@Test
	public void testAddObserver() {

		credentialManager.addObserver(keystoreChangedObserver);
		assertEquals(keystoreChangedObserver, credentialManager.getObservers().get(0));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getObservers()}.
	 */
	@Test
	public void testGetObservers() {
		// Initially there are no observers
		assertTrue(credentialManager.getObservers().isEmpty());

		credentialManager.addObserver(keystoreChangedObserver);
		
		assertEquals(keystoreChangedObserver, credentialManager.getObservers().get(0));	
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#removeObserver(net.sf.taverna.t2.lang.observer.Observer)}.
	 */
	@Test
	public void testRemoveObserver() {
		credentialManager.addObserver(keystoreChangedObserver);
		assertTrue(credentialManager.getObservers().size() == 1);	
		credentialManager.removeObserver(keystoreChangedObserver);
		assertTrue(credentialManager.getObservers().size() == 0);	
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#confirmMasterPassword(java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testConfirmMasterPassword() throws CMException {
		credentialManager.confirmMasterPassword("uber");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#changeMasterPassword(java.lang.String)}.
	 * @throws CMException 
	 */
	@Test
	public void testChangeMasterPassword() throws CMException {
		credentialManager.changeMasterPassword("blah");
		credentialManager.confirmMasterPassword("blah");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#initializeSSL()}.
	 */
	@Test
	@Ignore
	public void testInitializeSSL() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getTavernaSSLSocketFactory()}.
	 */
	@Test
	@Ignore
	public void testGetTavernaSSLSocketFactory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#normalizeServiceURI(java.net.URI)}.
	 */
	@Test
	@Ignore
	public void testNormalizeServiceURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setFragmentForURI(java.net.URI, java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testSetFragmentForURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setUserInfoForURI(java.net.URI, java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testSetUserInfoForURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#hasUsernamePasswordForService(java.net.URI)}.
	 */
	@Test
	@Ignore
	public void testHasUsernamePasswordForService() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setMasterPasswordProviders(java.util.List)}.
	 */
	@Test
	public void testSetMasterPasswordProviders() {
		
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		assertTrue(credentialManager.getMasterPasswordProviders().contains(masterPasswordProvider));
		
		// Set it to null and see what happens
		credentialManager.setMasterPasswordProviders(null);		
		assertNull(credentialManager.getMasterPasswordProviders());		
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getMasterPasswordProviders()}.
	 */
	@Test
	public void testGetMasterPasswordProviders() {
		
		assertFalse(credentialManager.getMasterPasswordProviders().isEmpty());
		assertTrue(credentialManager.getMasterPasswordProviders().contains(masterPasswordProvider));	
	}
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setJavaTruststorePasswordProviders(java.util.List)}.
	 */
	@Test
	public void testSetJavaTruststorePasswordProviders() {
		
		List<JavaTruststorePasswordProvider> javaTruststorePasswordProviders = new ArrayList<JavaTruststorePasswordProvider>();
		JavaTruststorePasswordProvider javaTruststorePasswordProvider = new DummyJavaTruststorePasswordProvider();
		javaTruststorePasswordProvider.setJavaTruststorePassword("blah");
		javaTruststorePasswordProviders.add(javaTruststorePasswordProvider);
		
		credentialManager.setJavaTruststorePasswordProviders(javaTruststorePasswordProviders);
		
		assertTrue(credentialManager.getJavaTruststorePasswordProviders().contains(javaTruststorePasswordProvider));
		
		// Set it to null and see what happens
		credentialManager.setJavaTruststorePasswordProviders(null);		
		assertNull(credentialManager.getJavaTruststorePasswordProviders());	
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getJavaTruststorePasswordProviders()}.
	 */
	@Test
	public void testGetJavaTruststorePasswordProviders() {
		
		assertTrue(credentialManager.getJavaTruststorePasswordProviders().isEmpty());
	}
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setServiceUsernameAndPasswordProviders(java.util.List)}.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testSetServiceUsernameAndPasswordProviders() throws URISyntaxException {
		
		List<ServiceUsernameAndPasswordProvider> serviceUsernameAndPasswordProviders = new ArrayList<ServiceUsernameAndPasswordProvider>();
		ServiceUsernameAndPasswordProvider serviceUsernameAndPasswordProvider = new DummyServiceUsernameAndPasswordProvider();
		serviceUsernameAndPasswordProvider.setServiceUsernameAndPassword(new URI("http://someservice"), new UsernamePassword("blah", "blah"));
		serviceUsernameAndPasswordProviders.add(serviceUsernameAndPasswordProvider);
		
		credentialManager.setServiceUsernameAndPasswordProviders(serviceUsernameAndPasswordProviders);
		
		assertTrue(credentialManager.getServiceUsernameAndPasswordProviders().contains(serviceUsernameAndPasswordProvider));
		
		// Set it to null and see what happens
		credentialManager.setServiceUsernameAndPasswordProviders(null);		
		assertNull(credentialManager.getServiceUsernameAndPasswordProviders());	
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getServiceUsernameAndPasswordProviders()}.
	 */
	@Test
	public void testGetServiceUsernameAndPasswordProviders() {
		
		assertTrue(credentialManager.getServiceUsernameAndPasswordProviders().isEmpty());
	}
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setTrustConfirmationProviders(java.util.List)}.
	 * @throws IOException 
	 */
	@Test
	public void testSetTrustConfirmationProviders() throws IOException {
		List<TrustConfirmationProvider> trustConfirmationProviders = new ArrayList<TrustConfirmationProvider>();
		TrustConfirmationProvider trustConfirmationProvider = new TrustAlwaysConfirmationProvider();
		trustConfirmationProviders.add(trustConfirmationProvider);
		
		credentialManager.setTrustConfirmationProviders(trustConfirmationProviders);
		
		assertTrue(credentialManager.getTrustConfirmationProviders().contains(trustConfirmationProvider));
		
		// Set it to null and see what happens
		credentialManager.setTrustConfirmationProviders(null);		
		assertNull(credentialManager.getTrustConfirmationProviders());	
	}
	
	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getTrustConfirmationProviders()}.
	 */
	@Test
	public void testGetTrustConfirmationProviders() {
		
		assertTrue(credentialManager.getTrustConfirmationProviders().isEmpty());
	}
}
