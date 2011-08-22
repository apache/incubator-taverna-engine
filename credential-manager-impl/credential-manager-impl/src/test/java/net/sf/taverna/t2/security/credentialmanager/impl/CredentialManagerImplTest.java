/**
 * 
 */
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

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
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
 * @author alex
 *
 */
public class CredentialManagerImplTest {
	
	private CredentialManagerImpl credentialManager;
	private DummyMasterPasswordProvider masterPasswordProvider;
	private File credentialManagerDirectory;
	
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
		URI serviceURI =  new URI("http://someservice");
		UsernamePassword testUsernamePassword = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNull(testUsernamePassword);
		
		testUsernamePassword = new UsernamePassword("testuser", "testpasswd");
		credentialManager.addUsernameAndPasswordForService(testUsernamePassword,serviceURI);
		
		UsernamePassword testUsernamePassword2 = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNotNull(testUsernamePassword2);
		assertTrue(Arrays.equals(testUsernamePassword.getPassword(), testUsernamePassword2.getPassword()));
		assertTrue(testUsernamePassword.getUsername().equals(testUsernamePassword2.getUsername()));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addUsernameAndPasswordForService(net.sf.taverna.t2.security.credentialmanager.UsernamePassword, java.net.URI)}.
	 * @throws URISyntaxException 
	 * @throws CMException 
	 */
	@Test
	public void testAddUsernameAndPasswordForService() throws CMException, URISyntaxException {
		URI serviceURI =  new URI("http://someservice");
		UsernamePassword testUsernamePassword = new UsernamePassword("testuser", "testpasswd");
		String alias = credentialManager.addUsernameAndPasswordForService(testUsernamePassword,serviceURI);
		
		UsernamePassword testUsernamePassword2 = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNotNull(testUsernamePassword2);
		assertTrue(credentialManager.hasEntryWithAlias(CredentialManager.KeystoreType.KEYSTORE, alias));
		assertTrue(Arrays.equals(testUsernamePassword.getPassword(), testUsernamePassword2.getPassword()));
		assertTrue(testUsernamePassword.getUsername().equals(testUsernamePassword2.getUsername()));
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#deleteUsernameAndPasswordForService(java.net.URI)}.
	 * @throws URISyntaxException 
	 * @throws CMException 
	 */
	@Test
	public void testDeleteUsernameAndPasswordForServiceURI() throws URISyntaxException, CMException {
		URI serviceURI =  new URI("http://someservice");

		// The Credential Manage's Keystore is empty initially so this should 
		// have no effect apart from initializing the Keystore/Truststore
		credentialManager.deleteUsernameAndPasswordForService(serviceURI);
		
		UsernamePassword testUsernamePassword = new UsernamePassword("testuser", "testpasswd");
		credentialManager.addUsernameAndPasswordForService(testUsernamePassword,serviceURI);	
		credentialManager.deleteUsernameAndPasswordForService(serviceURI);
		
		UsernamePassword testUsernamePassword2 = credentialManager.getUsernameAndPasswordForService(serviceURI, false, "");
		assertNull(testUsernamePassword2);
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
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#createX509CertificateAlias(java.security.cert.X509Certificate)}.
	 */
	@Test
	public void testCreateX509CertificateAlias() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#deleteTrustedCertificate(java.lang.String)}.
	 */
	@Test
	public void testDeleteTrustedCertificate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#isKeyEntry(java.lang.String)}.
	 */
	@Test
	public void testIsKeyEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#hasEntryWithAlias(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testHasEntryWithAlias() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getAliases(net.sf.taverna.t2.security.credentialmanager.CredentialManager.KeystoreType)}.
	 */
	@Test
	public void testGetAliases() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getServiceURIsForAllUsernameAndPasswordPairs()}.
	 */
	@Test
	public void testGetServiceURIsForAllUsernameAndPasswordPairs() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#loadPKCS12Keystore(java.io.File, java.lang.String)}.
	 */
	@Test
	public void testLoadPKCS12Keystore() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#addObserver(net.sf.taverna.t2.lang.observer.Observer)}.
	 */
	@Test
	public void testAddObserver() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getObservers()}.
	 */
	@Test
	public void testGetObservers() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#removeObserver(net.sf.taverna.t2.lang.observer.Observer)}.
	 */
	@Test
	public void testRemoveObserver() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#exists(java.lang.String)}.
	 */
	@Test
	public void testExists() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#confirmMasterPassword(java.lang.String)}.
	 */
	@Test
	public void testConfirmMasterPassword() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#changeMasterPassword(java.lang.String)}.
	 */
	@Test
	public void testChangeMasterPassword() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#initializeSSL()}.
	 */
	@Test
	public void testInitializeSSL() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#getTavernaSSLSocketFactory()}.
	 */
	@Test
	public void testGetTavernaSSLSocketFactory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#normalizeServiceURI(java.net.URI)}.
	 */
	@Test
	public void testNormalizeServiceURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setFragmentForURI(java.net.URI, java.lang.String)}.
	 */
	@Test
	public void testSetFragmentForURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setUserInfoForURI(java.net.URI, java.lang.String)}.
	 */
	@Test
	public void testSetUserInfoForURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#resetAuthCache()}.
	 */
	@Test
	public void testResetAuthCache() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#hasUsernamePasswordForService(java.net.URI)}.
	 */
	@Test
	public void testHasUsernamePasswordForService() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setConfigurationDirectoryPath(java.io.File)}.
	 */
	@Test
	public void testSetConfigurationDirectoryPath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setMasterPasswordProviders(java.util.List)}.
	 */
	@Test
	public void testSetMasterPasswordProviders() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setJavaTruststorePasswordProviders(java.util.List)}.
	 */
	@Test
	public void testSetJavaTruststorePasswordProviders() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setServiceUsernameAndPasswordProviders(java.util.List)}.
	 */
	@Test
	public void testSetUsernameAndPasswordForServiceProviders() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.sf.taverna.t2.security.credentialmanager.impl.CredentialManagerImpl#setTrustConfirmationProviders(java.util.List)}.
	 */
	@Test
	public void testSetTrustConfirmationProviders() {
		fail("Not yet implemented");
	}

}
