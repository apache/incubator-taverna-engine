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

package org.apache.taverna.security.credentialmanager;

import java.net.Authenticator;
import java.net.URI;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.apache.taverna.lang.observer.Observer;

/**
 * Provides a wrapper for Taverna's Keystore and Truststore and implements
 * methods for managing user's credentials (passwords, private/proxy key pairs)
 * and credentials of trusted services and CAs' (i.e. their public key
 * certificates).
 * <p>
 * Keystore and Truststore are Bouncy Castle UBER-type keystores saved as files
 * called "taverna-keystore.ubr" and "taverna-truststore.ubr" respectively. In
 * the case of the Workbench, they are located in a directory called "security"
 * inside the taverna.home directory. This location can be changed, e.g. in the
 * case of the server and command line tool you may want to pass in the location
 * of the Credential Manager's files.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public interface CredentialManager {

	public static final String KEYSTORE_FILE_NAME = "taverna-keystore.ubr";
	public static final String TRUSTSTORE_FILE_NAME = "taverna-truststore.ubr";

	public static final String UTF_8 = "UTF-8";

	public static final String PROPERTY_TRUSTSTORE = "javax.net.ssl.trustStore";
	public static final String PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String PROPERTY_KEYSTORE = "javax.net.ssl.keyStore";
	public static final String PROPERTY_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	public static final String PROPERTY_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
	public static final String PROPERTY_KEYSTORE_PROVIDER = "javax.net.ssl.keyStoreProvider";
	public static final String PROPERTY_TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";
	public static final String PROPERTY_TRUSTSTORE_PROVIDER = "javax.net.ssl.trustStoreProvider";
	
	// Existence of the file with this name in the Credential Manager folder 
	// indicates the we have deleted the revoked certificates from some of our services -
	// BioCatalogue, BiodiversityCatalogue, heater.
	public static final String CERTIFICATES_REVOKED_INDICATOR_FILE_NAME = "certificates_revoked";

	/*
	 * ASCII NUL character - for separating the username from the rest of the
	 * string when saving it in the Keystore. Seems like a good separator as it
	 * will highly unlikely feature in a username.
	 */
	public static final char USERNAME_AND_PASSWORD_SEPARATOR_CHARACTER = '\u0000';

	/*
	 * Constants denoting which of the two Credential Manager's keystores
	 * (Keystore or Truststore) we are currently performing an operation on (in
	 * cases when the same operation can be done on both).
	 */
	public static enum KeystoreType {
		KEYSTORE, TRUSTSTORE
	};

	/*
	 * Existence of this file in the Credential Manager folder indicates the
	 * user has set the master password so do not use the default password
	 */
	public static final String USER_SET_MASTER_PASSWORD_INDICATOR_FILE_NAME = "user_set_master_password";

	/*
	 * Default password for Truststore - needed as the Truststore needs to be
	 * populated before the Workbench starts up to initiate the SSLSocketFactory
	 * and to avoid popping up a dialog to ask the user for it.
	 */
	// private static final String TRUSTSTORE_PASSWORD = "Tu/Ap%2_$dJt6*+Rca9v";

	/**
	 * Set the directory where Credential Manager's Keystore and Truststore
	 * files will be read from. If this method is not used, the directory will
	 * default to <TAVERNA_HOME>/security somewhere in user's home directory.
	 * 
	 * If you want to use this method to change the location of Credential
	 * Manager's configuration directory then make sure you call it before any
	 * other method on Credential Manager.
	 * 
	 * @param credentialManagerDirectory
	 * @throws CMException
	 */
	void setConfigurationDirectoryPath(Path credentialManagerDirectory)
			throws CMException;

	/**
	 * Checks if the Keystore contains a username and password for the given
	 * service URI.
	 */
	boolean hasUsernamePasswordForService(URI serviceURI) throws CMException;

	/**
	 * Get a username and password pair for the given service's URI, or null if
	 * it does not exit.
	 * <p>
	 * If the username and password are not available in the Keystore, it will
	 * invoke implementations of the {@link ServiceUsernameAndPasswordProvider}
	 * interface asking the user (typically through the UI) or resolving
	 * hard-coded credentials.
	 * <p>
	 * If the parameter <code>useURIPathRecursion</code> is true, then the
	 * Credential Manager will also attempt to look for stored credentials for
	 * each of the parent fragments of the URI.
	 * 
	 * @param serviceURI
	 *            The URI of the service for which we are providing the username
	 *            and password
	 * 
	 * @param useURIPathRecursion
	 *            Whether to look for any username and passwords stored in the
	 *            Keystore for the parent fragments of the service URI (for
	 *            example, we are looking for the credentials for service
	 *            http://somehost/some-fragment but we already have credentials
	 *            stored for http://somehost which can be reused)
	 * 
	 * @param requestingMessage
	 *            The message to be presented to the user when asking for the
	 *            username and password, normally useful for UI providers that
	 *            pop up dialogs, can be ignored otherwise
	 * 
	 * @return username and password pair for the given service
	 * 
	 * @throws CMException
	 *             if anything goes wrong during Keystore lookup, etc.
	 */
	UsernamePassword getUsernameAndPasswordForService(URI serviceURI,
			boolean useURIPathRecursion, String requestingMessage)
			throws CMException;

	/**
	 * Insert a username and password pair for the given service URI in the
	 * Keystore.
	 * <p>
	 * Effectively, this method inserts a new secret key entry in the Keystore,
	 * where key contains <USERNAME>"\000"<PASSWORD> string, i.e. password is
	 * prepended with the username and separated by a \000 character (which
	 * hopefully will not appear in the username).
	 * <p>
	 * Username and password string is saved in the Keystore as byte array using
	 * SecretKeySpec (which constructs a secret key from the given byte array
	 * but does not check if the given bytes indeed specify a secret key of the
	 * specified algorithm).
	 * <p>
	 * An alias used to identify the username and password entry is constructed
	 * as "password#"<SERVICE_URL> using the service URL this username/password
	 * pair is to be used for.
	 * 
	 * @param usernamePassword
	 *            The {@link UsernamePassword} to store
	 * @param serviceURI
	 *            The (possibly normalized) URI to store the credentials under
	 * @return TODO
	 * @throws CMException
	 *             If the credentials could not be stored
	 * 
	 * @return the alias under which this username and password entry was saved
	 *         in the Keystore
	 */
	String addUsernameAndPasswordForService(UsernamePassword usernamePassword,
			URI serviceURI) throws CMException;

	/**
	 * Delete a username and password pair for the given service URI from the
	 * Keystore.
	 */
	void deleteUsernameAndPasswordForService(URI serviceURI) throws CMException;

	/**
	 * Checks if the Keystore contains the given key pair entry (private key and
	 * its corresponding public key certificate chain).
	 */
	public boolean hasKeyPair(Key privateKey, Certificate[] certs)
			throws CMException;

	/**
	 * Insert a new key entry containing private key and the corresponding
	 * public key certificate chain in the Keystore.
	 * 
	 * An alias used to identify the keypair entry is constructed as:
	 * "keypair#"<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#"<
	 * CERT_SERIAL_NUMBER>
	 * 
	 * @return the alias under which this key entry was saved in the Keystore
	 */
	String addKeyPair(Key privateKey, Certificate[] certs) throws CMException;

	/**
	 * Delete a key pair entry from the Keystore given its alias.
	 */
	void deleteKeyPair(String alias) throws CMException;

	/**
	 * Delete a key pair entry from the Keystore given its private and public
	 * key parts.
	 */
	void deleteKeyPair(Key privateKey, Certificate[] certs) throws CMException;

	/**
	 * Create a Keystore alias that would be used for adding the given key pair
	 * (private and public key) entry to the Keystore. The alias is cretaed as
	 * "keypair#"<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#"<
	 * CERT_SERIAL_NUMBER>
	 * 
	 * @param privateKey
	 *            private key
	 * @param certs
	 *            public key's certificate chain
	 * @return
	 */
	String createKeyPairAlias(Key privateKey, Certificate certs[]);

	/**
	 * Export a key entry containing private key and public key certificate
	 * chain from the Keystore to a PKCS #12 file.
	 */
	void exportKeyPair(String alias, Path exportFile, String pkcs12Password)
			throws CMException;

	/**
	 * Get certificate entry from the Keystore or Truststore. If the given alias
	 * name identifies a trusted certificate entry, the certificate associated
	 * with that entry is returned from the Truststore. If the given alias name
	 * identifies a key pair entry, the first element of the certificate chain
	 * of that entry is returned from the Keystore.
	 */
	Certificate getCertificate(KeystoreType ksType, String alias)
			throws CMException;

	/**
	 * Get certificate chain for the key pair entry from the Keystore given its
	 * alias.
	 * <p>
	 * This method works for the Keystore only as the Truststore does not
	 * contain key pair entries, but trusted certificate entries only.
	 */
	Certificate[] getKeyPairsCertificateChain(String alias) throws CMException;

	/**
	 * Get the private key part of a key pair entry from the Keystore given its
	 * alias.
	 * <p>
	 * This method works for the Keystore only as the Truststore does not
	 * contain key pair entries, but trusted certificate entries only.
	 */
	Key getKeyPairsPrivateKey(String alias) throws CMException;

	/**
	 * Checks if the Truststore contains the given public key certificate.
	 */
	boolean hasTrustedCertificate(Certificate cert) throws CMException;

	/**
	 * Insert a trusted certificate entry in the Truststore with an alias
	 * constructed as:
	 * 
	 * "trustedcert#<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#
	 * "<CERT_SERIAL_NUMBER>
	 * 
	 * @return the alias under which this trusted certificate entry was saved in
	 *         the Keystore
	 */
	String addTrustedCertificate(X509Certificate cert) throws CMException;

	/**
	 * Delete a trusted certificate entry from the Truststore given its alias.
	 */
	void deleteTrustedCertificate(String alias) throws CMException;

	/**
	 * Delete a trusted certificate entry from the Truststore given the
	 * certificate.
	 */
	void deleteTrustedCertificate(X509Certificate cert) throws CMException;

	/**
	 * Create a Truststore alias that would be used for adding the given trusted
	 * X509 certificate to the Truststore. The alias is cretaed as
	 * "trustedcert#"<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#"<
	 * CERT_SERIAL_NUMBER>
	 * 
	 * @param cert
	 *            certificate to generate the alias for
	 * @return the alias for the given certificate
	 */
	String createTrustedCertificateAlias(X509Certificate cert);

	/**
	 * Check if the given alias identifies a key entry in the Keystore.
	 */
	boolean isKeyEntry(String alias) throws CMException;

	/**
	 * Check if the Keystore/Truststore contains an entry with the given alias.
	 */
	boolean hasEntryWithAlias(KeystoreType ksType, String alias)
			throws CMException;

	/**
	 * Get all the aliases from the Keystore/Truststore or null if there was
	 * some error while accessing it.
	 */
	ArrayList<String> getAliases(KeystoreType ksType) throws CMException;

	/**
	 * Get service URIs associated with all username/password pairs currently in
	 * the Keystore.
	 * 
	 * @see #hasUsernamePasswordForService(URI)
	 */
	List<URI> getServiceURIsForAllUsernameAndPasswordPairs() throws CMException;

	/**
	 * Load a PKCS12-type keystore from a file using the supplied password.
	 */
	KeyStore loadPKCS12Keystore(Path pkcs12File, String pkcs12Password)
			throws CMException;

	/**
	 * Add an observer of the changes to the Keystore or Truststore.
	 */
	void addObserver(Observer<KeystoreChangedEvent> observer);

	/**
	 * Get all current observers of changes to the Keystore or Truststore.
	 */
	List<Observer<KeystoreChangedEvent>> getObservers();

	/**
	 * Remove an observer of the changes to the Keystore or Truststore.
	 */
	void removeObserver(Observer<KeystoreChangedEvent> observer);

	/**
	 * Checks if Keystore's master password is the same as the one provided.
	 * 
	 * @param password
	 * @return
	 * @throws CMException
	 */
	boolean confirmMasterPassword(String password) throws CMException;

	/**
	 * Change the Keystore and the Truststore's master password to the one
	 * provided. The Keystore and Truststore both use the same password.
	 */
	void changeMasterPassword(String newPassword) throws CMException;

	/**
	 * Reset the JVMs cache for authentication like HTTP Basic Auth.
	 * <p>
	 * Note that this method uses undocumented calls to
	 * <code>sun.net.www.protocol.http.AuthCacheValue</code> which might not be
	 * valid in virtual machines other than Sun Java 6. If these calls fail,
	 * this method will log the error and return <code>false</code>.
	 * 
	 * @return <code>true</code> if the VMs cache could be reset, or
	 *         <code>false</code> otherwise.
	 */
	boolean resetAuthCache();

	/**
	 * Set the default SSLContext to use Credential Manager's Keystore and
	 * Truststore for managing SSL connections from Taverna and also set
	 * HttpsURLConnection's default SSLSocketFactory to use the one from the
	 * just configured SSLContext, i.e. backed by Credential Manager's Keystore
	 * and Truststore.
	 * 
	 * @throws CMException
	 */
	void initializeSSL() throws CMException;

	/**
	 * Get Taverna's SSLSocketFactory backed by Credential Manager's Keystore
	 * and Truststore.
	 * 
	 * @return
	 * @throws CMException
	 */
	SSLSocketFactory getTavernaSSLSocketFactory() throws CMException;
        
        public Authenticator getAuthenticator();

}
