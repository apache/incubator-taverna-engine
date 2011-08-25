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
package net.sf.taverna.t2.security.credentialmanager;

import java.io.File;
import java.net.URI;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.lang.observer.Observer;

/**
 * Provides a wrapper for Taverna's Keystore and Truststore and implements
 * methods for managing user's credentials (passwords, private/proxy key pairs) 
 * and credentials of trusted services and CAs' (i.e. their public key certificates).
 * 
 * Keystore and Truststore are Bouncy Castle UBER-type keystores saved as files called 
 * "t2keystore.ubr" and "t2truststore.ubr" respectively. In the case of
 * the Workbench, they are located in a directory called "security" inside the 
 * taverna.home directory. This location can be changed, e.g. in the case of the 
 * server and command line tool you may want to pass in the location of the 
 * Credential Manager's files.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 */
public interface CredentialManager {
	
	public static final String KEYSTORE_FILE_NAME = "taverna-keystore.ubr";
	public static final String TRUSTSTORE_FILE_NAME = "taverna-truststore.ubr";
	
	//public static final String KEYSTORE_FILE_NAME = "t2keystore.ubr";
	//public static final String TRUSTSTORE_FILE_NAME = "t2truststore.ubr";
	
	public static final String UTF_8 = "UTF-8";

	public static final String PROPERTY_TRUSTSTORE = "javax.net.ssl.trustStore";
	public static final String PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	public static final String PROPERTY_KEYSTORE = "javax.net.ssl.keyStore";
	public static final String PROPERTY_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	public static final String PROPERTY_KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
	public static final String PROPERTY_KEYSTORE_PROVIDER = "javax.net.ssl.keyStoreProvider";
	public static final String PROPERTY_TRUSTSTORE_TYPE = "javax.net.ssl.trustStoreType";
	public static final String PROPERTY_TRUSTSTORE_PROVIDER = "javax.net.ssl.trustStoreProvider";
	
	// ASCII NUL character - for separating the username from the rest of the string 
	// when saving it in the Keystore. Seems like a good separator as it will highly 
	// unlikely feature in a username.
	public static final char USERNAME_AND_PASSWORD_SEPARATOR_CHARACTER = '\u0000';	
	
	// Constants denoting which of the two Credential Manager's keystores (Keystore or Truststore) 
	// we are currently performing an operation on (in cases when the same operation can be done on both).
	public static enum KeystoreType {KEYSTORE, TRUSTSTORE};
	
	/**
	 * Checks if the Keystore contains a username and password for the given service URI.
	 */
	public boolean hasUsernamePasswordForService(URI serviceURI)
			throws CMException;
	
	/**
	 * Get a username and password pair for the given service's URI, 
	 * or null if it does not exit.
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
	 * @param serviceURI The URI of the service for which we are providing the username and password
	 * 
	 * @param useURIPathRecursion Whether to look for any username and passwords stored in the Keystore 
	 * for the parent fragments of the service URI (for example, we are looking for the credentials for service 
	 * http://somehost/some-fragment but we already have credentials stored for http://somehost which can be reused) 
	 * 
	 * @param requestingMessage The message to be presented to the user when asking for the username and password, 
	 * normally useful for UI providers that pop up dialogs, can be ignored otherwise
	 * 
	 * @return username and password pair for the given service
	 * 
	 * @throws CMException if anything goes wrong during Keystore lookup, etc.
	 */
	public UsernamePassword getUsernameAndPasswordForService(
			URI serviceURI, boolean useURIPathRecursion, String requestingMessage)
			throws CMException;

	/**
	 * Insert a username and password pair for the given service URI in the Keystore.
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
	 * <p>
	 * 
	 * @param usernamePassword
	 *            The {@link UsernamePassword} to store
	 * @param serviceURI
	 *            The (possibly normalized) URI to store the credentials under
	 * @return TODO
	 * @throws CMException
	 *             If the credentials could not be stored
	 *             
	 * @return the alias under which this username and password entry was saved in the Keystore
	 */
	public String addUsernameAndPasswordForService(
			UsernamePassword usernamePassword, URI serviceURI)
			throws CMException;

	/**
	 * Delete a username and password pair for the given service URI from the
	 * Keystore.
	 */
	public void deleteUsernameAndPasswordForService(URI serviceURI)
			throws CMException;
	
	/**
	 * Checks if the Keystore contains the given key pair entry (private key and its
	 * corresponding public key certificate chain).
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
	public String addKeyPair(Key privateKey, Certificate[] certs)
			throws CMException;

	/**
	 * Delete a key pair entry from the Keystore given its alias.
	 */
	public void deleteKeyPair(String alias) throws CMException;

	/**
	 * Delete a key pair entry from the Keystore given its private and public key parts.
	 */
	public void deleteKeyPair(Key privateKey, Certificate[] certs)
			throws CMException;
	
	/**
	 * Create a Keystore alias that would be used for adding the given 
	 * key pair (private and public key) entry to the Keystore. The alias is cretaed as 
	 * "keypair#"<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#"<CERT_SERIAL_NUMBER>
	 * 
	 * @param privateKey private key
	 * @param certs public key's certificate chain
	 * @return
	 */
	public String createKeyPairAlias(Key privateKey, Certificate certs[]);
	
	/**
	 * Export a key entry containing private key and public key certificate
	 * chain from the Keystore to a PKCS #12 file.
	 */
	public abstract void exportKeyPair(String alias, File exportFile,
			String pkcs12Password) throws CMException;

	/**
	 * Get certificate entry from the Keystore or Truststore. If the given alias
	 * name identifies a trusted certificate entry, the certificate associated
	 * with that entry is returned from the Truststore. If the given alias name
	 * identifies a key pair entry, the first element of the certificate chain
	 * of that entry is returned from the Keystore.
	 */
	public Certificate getCertificate(KeystoreType ksType, String alias)
			throws CMException;

	/**
	 * Get certificate chain for the key pair entry from the Keystore given its alias. 
	 * <p>
	 * This method works for the Keystore only as the Truststore does not contain key pair
	 * entries, but trusted certificate entries only.
	 */
	public Certificate[] getKeyPairCertificateChain(String alias)
			throws CMException;

	/**
	 * Checks if the Truststore contains the given public key certificate.
	 */
	public boolean hasTrustedCertificate(Certificate cert)
			throws CMException;
	
	/**
	 * Insert a trusted certificate entry in the Truststore with an alias
	 * constructed as:
	 * 
	 * "trustedcert#<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#
	 * "<CERT_SERIAL_NUMBER>
	 * 
	 * @return the alias under which this trusted certificate entry was saved in the Keystore
	 */
	public String  addTrustedCertificate(X509Certificate cert)
			throws CMException;

	/**
	 * Delete a trusted certificate entry from the Truststore given its alias.
	 */
	public void deleteTrustedCertificate(String alias)
			throws CMException;

	/**
	 * Delete a trusted certificate entry from the Truststore given the certificate.
	 */
	public void deleteTrustedCertificate(X509Certificate cert)
			throws CMException;
	
	/**
	 * Create a Truststore alias that would be used for adding the given 
	 * trusted X509 certificate to the Truststore. The alias is cretaed as 
	 * "trustedcert#"<CERT_SUBJECT_COMMON_NAME>"#"<CERT_ISSUER_COMMON_NAME>"#"<
	 * CERT_SERIAL_NUMBER>
	 * 
	 * @param cert certificate to generate the alias for
	 * @return the alias for the given certificate
	 */
	public String createTrustedCertificateAlias(X509Certificate cert);
	
	/**
	 * Check if the given alias identifies a key entry in the Keystore.
	 */
	public boolean isKeyEntry(String alias) throws CMException;

	/**
	 * Check if the Keystore/Truststore contains an entry with the given alias.
	 */
	public boolean hasEntryWithAlias(KeystoreType ksType, String alias)
			throws CMException;

	/**
	 * Get all the aliases from the Keystore/Truststore or null if there was some error
	 * while accessing it.
	 */
	public ArrayList<String> getAliases(KeystoreType ksType)
			throws CMException;

	/**
	 * Get service URIs associated with all username/password pairs currently in
	 * the Keystore.
	 * 
	 * @see #hasUsernamePasswordForService(URI)
	 */
	public List<URI> getServiceURIsForAllUsernameAndPasswordPairs()
			throws CMException;

	/**
	 * Load a PKCS12-type keystore from a file using the supplied password.
	 */
	public KeyStore loadPKCS12Keystore(File pkcs12File,
			String pkcs12Password) throws CMException;

	/**
	 * Add an observer of the changes to the Keystore or Truststore.
	 */
	public void addObserver(Observer<KeystoreChangedEvent> observer);

	/**
	 * Get all current observers of changes to the Keystore or Truststore.
	 */
	public List<Observer<KeystoreChangedEvent>> getObservers();

	/**
	 * Remove an observer of the changes to the Keystore or Truststore.
	 */
	public void removeObserver(Observer<KeystoreChangedEvent> observer);

	/**
	 * Change the Keystore and the Truststore's master password to the one provided.
	 * The Keystore and Truststore both use the same password.
	 */
	public void changeMasterPassword(String newPassword)
			throws CMException;

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
	public boolean resetAuthCache();

}