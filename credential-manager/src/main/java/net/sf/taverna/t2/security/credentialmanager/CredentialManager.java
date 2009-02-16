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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;
import net.sf.taverna.t2.security.agents.SecurityAgentManager;

import org.apache.log4j.Logger;

/**
 * Provides a wrapper for the user's Keystore and Truststore and implements
 * methods for managing user's credentials (passwords and private key pairs) and
 * trusted services' public key certificates.
 * 
 * @author Alexandra Nenadic
 */

public class CredentialManager {
	
	/**
	 * Log4J Logger
	 */
	private static Logger logger = Logger.getLogger(CredentialManager.class);
	
	/**
	 * Indicator as to whether the Credential Manager has been initialised (i.e.
	 * whether Keystore and Truststore have been loaded).
	 */
	private static boolean isInitialised;

	/** Master password for the Keystore and Truststore. */
	private static String masterPassword;

	/** Keystore file */
	private static File keystoreFile;

	/**
	 * Keystore containing user's passwords, private keys and public key
	 * certificate chains.
	 */
	private static KeyStore keystore;

	/** Truststore file. */
	private static File truststoreFile;

	/** Truststore containing trusted certificates of CA authorities and services. */
	private static KeyStore truststore;

	/**
	 * Service URLs file containing lists of service URLs associated with
	 * private key aliases.
	 */
	private static File serviceURLsFile;

	/**
	 * A map of service URLs associated with private key aliases, i.e. aliases
	 * are keys in the hasmap and lists of URLs are hashmap values.
	 */
	private static HashMap<String, Vector<String>> serviceURLs;

	/**
	 * Constants denoting which of the two keystores we are performing
	 * operations on.
	 */
	public static final String KEYSTORE = "Keystore";
	public static final String TRUSTSTORE = "Truststore";

	/** CredentialManager singleton */
	private static CredentialManager INSTANCE;

	/**
	 * Returns a CredentialManager singleton
	 * 
	 * @return
	 * @throws CMException
	 */
	public static CredentialManager getInstance() throws CMException {
		synchronized (CredentialManager.class) {
			if (INSTANCE == null)
				INSTANCE = new CredentialManager();
		}
		return INSTANCE;
	}

	/**
	 * Overrides the Object’s clone method to prevent the singleton object to be
	 * cloned.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	private File getConfigurationDirectory() {
		File home = ApplicationRuntime.getInstance().getApplicationHomeDir();
		File configDirectory = new File(home,"conf");
		if (!configDirectory.exists()) {
			configDirectory.mkdir();
		}
		File secConfigDirectory = new File(configDirectory,"security");
		if (!secConfigDirectory.exists()) {
			secConfigDirectory.mkdir();
		}
		logger.info("Using config directory:"+secConfigDirectory.getAbsolutePath());
		return secConfigDirectory;
	}

	/**
	 * Credential Manager constructor. The constructor is private
	 * to suppress unauthorized calls to it.
	 */
	private CredentialManager() throws CMException {

		// Get the Bouncy Castle provider
		try {
			Provider bcProv = Security.getProvider("BC");

			if (bcProv == null) {
				//FIXME: Probably won't work with Raven.
				Class<?> bcProvClass = Class
						.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
				bcProv = (Provider) bcProvClass.newInstance();

				// Add BC as a security provider
				Security.addProvider(bcProv);
				
				logger.info("Added Bouncy Castle security provider.");
			}
		} 
		catch (Exception ex) {
			
			// No sign of the provider
			String exMessage = "Failed to load Bouncy Castle provider.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw new CMException(exMessage);
		}

		// Get the Keystore file
		/*String thisURL = CredentialManager.class.getResource(
				"CredentialManager.class").getPath(); // path to this class
		final String sep = System.getProperty("file.separator");
		// Directory this class is located in
		String thisDir = thisURL.substring(0, thisURL.lastIndexOf(sep) + 1);
		String keystoreFilePath = thisDir
				+ res.getString("CredentialManager.Keystore");		
		keystoreFile = new File (keystoreFilePath);*/
		File configDirectory = getConfigurationDirectory();
		keystoreFile = new File(configDirectory,"t2keystore.ubr"); 

		// Get the service URLs file (where lists of service urls for private
		// keys are kept)
		/*String serviceURLsFilePath = thisDir
				+ res.getString("CredentialManager.ServiceURLsFile"); 
		serviceURLsFile = new File (serviceURLsFilePath);*/
		serviceURLsFile = new File(configDirectory,"t2serviceURLs.txt"); 

		// Get the Truststore file
		/*String truststoreFilePath = thisDir + res.getString("CredentialManager.Truststore"); 
		truststoreFile = new File (truststoreFilePath);*/
		truststoreFile = new File(configDirectory,"t2truststore.ubr"); 

		// Credential Manager is not initialised yet 
		// (until we load the keystores and service URLs hashmap)
		isInitialised = false;
	}

	/**
	 * Initialises Credential Manager
	 * 
	 * @param mPassword -
	 *            master password for the Keystore and the Truststore (the same
	 *            master password is used for both)
	 * @throws CMException -
	 *             if Credential Manager could not be initialised, e.g. master
	 *             password is null or Keystore/Truststore could not be loaded
	 */
	public void init(String mPassword) throws CMException {

		// Set the master password for the Keystore/Truststore
		if (mPassword != null)
			masterPassword = mPassword;
		else {
			String exMessage = "Master password cannot be null.";
			throw new CMException(exMessage);
		}

		// Load the Keystore
		try {
			keystore = loadKeystore(keystoreFile, masterPassword);
			logger.info("Loaded the Keystore.");

		} 
		catch (CMException cme) {
			logger.error("Credential Manager: " + cme.getMessage(), cme);
			throw new CMException("Problem with loading the Keystore: "
					+ cme.getMessage());
		}
		
		// Load service URLs associated with private key aliases from a file.
		try {
			loadServiceURLs();
			logger.info("Loaded the Service URLs for private key pairs.");

		} 
		catch (CMException cme) {
			logger.error("Credential Manager: " + cme.getMessage(), cme);
			throw new CMException(
					"Problem with loading the private key entries' service URLs from a file: "
							+ cme.getMessage());
		}

		// Load the Truststore
		try {
			truststore = loadKeystore(truststoreFile, masterPassword);
			logger.info("Loaded the Truststore.");

		} 
		catch (CMException cme) {
			logger.error("Credential Manager: " + cme.getMessage(), cme);
			throw new CMException("Problem with loading the Truststore: "
					+ cme.getMessage());
		}

		// Credential Manager is now initialised
		isInitialised = true;
	}

	
	/**
	 * Loads a Bouncy Castle "UBER"-type keystore from a file on the disk and
	 * returns it.
	 * 
	 * @param ksFile - the file containing the keystore
	 * @param masterPassword - masterpassword for the keystore
	 * @throws CMException - if the keystore could not be loaded for some reason
	 */
	public static KeyStore loadKeystore(File ksFile, String masterPassword) throws CMException {

		KeyStore keystore = null;
		try {
			
			// Create a keystore instance as Bouncy Castle "UBER"-type keystore
			keystore = KeyStore.getInstance("UBER", "BC");
		} 
		catch (KeyStoreException ex) {
			
			// The requested keystore type is not available from the provider
			String exMessage = "Failed to insantiate the keystore. Reason: Requested keystore type is not available from the provider.";
			throw new CMException(exMessage);
		} 
		catch (NoSuchProviderException ex) {
			
			// The crypto provider has not been configured
			String exMessage = "Failed to insantiate the keystore. Reason: the crypto provider has not been configured.";
			throw new CMException(exMessage);
		}

		if (ksFile.exists()) { // If the file exists, open it

			// Try to load the keystore as Bouncy Castle "UBER"-type
			// keystore
			FileInputStream fis = null;

			try {
				
				// Get the file
				fis = new FileInputStream(ksFile);
				// Load the keystore from the file
				keystore.load(fis, masterPassword.toCharArray());
			} 
			catch (Exception ex) {
				
				String exMessage = "Failed to load the keystore. Possible reason: incorrect password or corrupted file.";
				throw new CMException(exMessage);
			} 
			finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		} 
		else { // Otherwise create an empty keystore

			FileOutputStream fos = null;
			try {

				keystore.load(null, null);

				// Immediatelly save the new (empty) keystore to the file
				fos = new FileOutputStream(ksFile);
				keystore.store(fos, masterPassword.toCharArray());
				
			} 
			catch (Exception ex) {

				String exMessage = "Failed to create the new keystore.";
				throw new CMException(exMessage);
			} 
			finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
		return keystore;
	}
	
	
	/**
	 * Loads lists of service URLs associated with private key aliases from a
	 * file and populates the serviceURLs hasmap.
	 */
	public void loadServiceURLs() throws CMException {
		
		serviceURLs = new HashMap<String, Vector<String>>();

		try {
			// Create an empty map with aliases as keys
			for (Enumeration<String> e = keystore.aliases(); e
					.hasMoreElements();) {
				String element = (String) e.nextElement();
				// We want only key pair entry aliases (and not password entry aliases)
				if (element.startsWith("keypair")) 
					serviceURLs.put(element, new Vector<String>());
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to get private key aliases when loading service URLs.";
			throw (new CMException(exMessage));
		}

		// If Service URLs file exists - load the URL lists from the file
		if (serviceURLsFile.exists()) {
			BufferedReader serviceURLsReader = null;

			try {
				serviceURLsReader = new BufferedReader(new FileReader(
						serviceURLsFile));

				String line = serviceURLsReader.readLine();
				while (line != null) {

					// Line consists of an URL-encoded URL and alias
					// separated by a blank character,
					// i.e. line=<ENCODED_URL>" "<ALIAS>
					// One alias can have more than one URL asociated with it
					// (i.e. more
					// than one line in the file can exist for the same alias).
					String alias = line.substring(line.indexOf(' ') + 1);
					String url = line.substring(0, line.indexOf(' '));
					// URLs were encoded before storing them in a file
					url = URLDecoder.decode(url, "UTF-8");

					Vector<String> urlsList = (Vector<String>) serviceURLs
							.get(alias); // get URL list for the current
											// alias (it can be empty)
					if (urlsList == null) {
						urlsList = new Vector<String>();
					}
					urlsList.add(url); // add the new URL to the list of URLs
										// for this alias

					serviceURLs.put(alias, urlsList); // put the updated list
														// back to the map

					line = serviceURLsReader.readLine();
				}
			} 
			catch (Exception ex) {
				String exMessage = "Failed to read the service URLs file.";
				throw (new CMException(exMessage));
			} 
			finally {
				if (serviceURLsReader != null) {
					try {
						serviceURLsReader.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	
	/**
	 * Checks if Credential Manager is initialised.
	 * 
	 * @return true - if Credential Manager is initialised, false otherwise
	 */
	public boolean isInitialised() {
		return isInitialised;
	}

	
	/**
	 * Checks if Keystore/Truststore file already exists on disk.
	 * 
	 * @param ksType -
	 *            indicates if the keystore in question is the Keystore or the
	 *            Truststore
	 * @return true if the file exists, false otherwise
	 */
	public boolean exists(String ksType) {

		if (ksType.equals(KEYSTORE))
			return keystoreFile.exists();
		else if (ksType.equals(TRUSTSTORE)) {
			return truststoreFile.exists();
		} else
			return false;
	}

	
	/**
	 * Saves the Keystore back to the file it was originally loaded from.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @throws CMException -
	 *             if keystore could not be saved for some reason  
 	 * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void saveKeystore(String ksType) throws CMException,
			CMNotInitialisedException {
		
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		FileOutputStream fos = null;
		try {
			if (ksType.equals(KEYSTORE)) {
				synchronized (keystore) {
					fos = new FileOutputStream(keystoreFile);
					keystore.store(fos, masterPassword.toCharArray());
					return;
				}
			} 
			else if (ksType.equals(TRUSTSTORE)) {
				synchronized (truststore) {
					fos = new FileOutputStream(truststoreFile);
					truststore.store(fos, masterPassword.toCharArray());
					return;
				}
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to save the " + ksType + ".";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		} 
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		// Keystore type not recognised
		String exMessage = "Keystore type " + ksType + " not supported.";
		throw (new CMException(exMessage));
	}

	
	/**
	 * Adds the service URLs associated with a private key entry to the serviceURLs
	 * hashmap and then saves (updates) the service URLs file.
	 * 
	 * @param sURLs -
	 *            list of URLs to be saved
	 * @param alias -
	 *            alias of the private key related to the list of URLs
	 * @throws CMException -
	 *             if the file with URLs and aliases could not be accessed for
	 *             some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised        
	 */
	public void addServiceURLs(String alias, Vector<String> sURLs)
			throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		// Add service url list to the serviceURLs hashmap (overwrites previous
		// value, if any)
		serviceURLs.put(alias, sURLs);

		// Save the updated hashmap to the file
		saveServiceURLs();
	}

	
	/**
	 * Gets a map of service URL lists for a list of private key entry aliases.
	 * 
	 * @param aliasList -
	 *            list of private key entry aliases related to the list of URLs
	 * @return - a map where aliases are keys and their asociated URL lists are values
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised           
	 */
	public HashMap<String, Vector<String>> getServiceURLs() throws CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		return serviceURLs;
	}
	

	/**
	 * Deletes the service URLs associated with a private key entry.
	 * 
	 * @param alias -
	 *            alias of the private key related to the list of URLs
	 * @throws CMException -
	 *             if the file with URLs and aliases could not be accessed for
	 *             some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
     */
	public void deleteServiceURLs(String alias) throws CMException,
			CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		// Remove service URL list from the serviceURLs hashmap
		serviceURLs.remove(alias);

		// Save the updated serviceURLs hashmap to the file
		saveServiceURLs();

	}

	
	/**
	 * Saves the content of serviceURLs map to a file. Overwrites previous
	 * content of the file.
	 * 
	 * @throws CMException
	 * @throws CMNotInitialisedException
	 */
	public void saveServiceURLs() throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		synchronized (serviceURLsFile) {

			// If file already exists
			if (serviceURLsFile.exists()) {
				// Delete the previous contents of the file
				serviceURLsFile.delete();
			}

			// Create a new empty file
			try {
				serviceURLsFile.createNewFile();
			} 
			catch (IOException ex) {
				String exMessage = "Failed to create a new service URLs' file.";
				logger.error("Credential Manager: " + exMessage, ex);
				throw (new CMException(exMessage));
			}

			BufferedWriter serviceURLsWriter = null;

			try {

				// Open the file for writing
				serviceURLsWriter = new BufferedWriter((new FileWriter(
						serviceURLsFile, false)));

				// Write the serviceURLs hashmap to the file
				for (String alias : serviceURLs.keySet()) { // for all aliases
					
					// for all urls associated with the alias
					for (Enumeration<String> e = ((Vector<String>) serviceURLs
							.get(alias)).elements(); e.hasMoreElements();) {
						String url = e.nextElement();

						// Each line of the file contains an encoded service URL
						// with its associated alias appended and separated from
						// the URL by a blank character ' ', 
						// i.e. line=<ENCODED_URL>" "<ALIAS>
						// Service URLs are encoded before saving to make sure
						// they do not contain blank characters
						// that are used as delimiters.
						String encodedURL = URLEncoder.encode((String) url,
								"UTF-8");
						StringBuffer line = new StringBuffer(encodedURL + " "
								+ alias);
						serviceURLsWriter.append(line);
						serviceURLsWriter.newLine();
					}
				}
			} 
			catch (FileNotFoundException ex) {
				// Should not happen
			} 
			catch (IOException ex) {
				String exMessage = "Failed to save the service URLs to the file.";
				logger.error("Credential Manager: " + exMessage, ex);
				throw (new CMException(exMessage));
			} 
			finally {
				if (serviceURLsWriter != null) {
					try {
						serviceURLsWriter.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	
	/**
	 * Gets the key entry containing a password and related username from the
	 * Keystore.
	 * 
	 * @param alias -
	 *            Keystore alias of the entry
	 * @return password as SecretKeySpec object (password is actually prepended
	 *         with the related username and saved as a raw key in the
	 *         SecretKeySpec)
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
     */
	public SecretKeySpec getPasswordEntry(String alias) throws CMException,
			CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (keystore) {
				SecretKeySpec skspecPassword = (((SecretKeySpec) keystore
						.getKey(alias, null)));
				return skspecPassword;
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to get the password entry from the Keystore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Inserts a new key entry containing a password with prepended related
	 * username in the Keystore.
	 * 
	 * Password is saved in the Keystore as SecretKeySpec (which extends
	 * SecretKeySpec that constructs a secret key from the given byte array but
	 * does not check if the given bytes indeed specify a secret key of the
	 * specified algorithm).
	 * 
	 * @param alias -
	 *            Keystore alias of the entry
	 * @param passwordKey -
	 *            password prepended with the related username (separated with a
	 *            suitable separator) as SecretKeySpec to be inserted in the
	 *            Keystore
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void insertPasswordEntry(String alias, SecretKeySpec passwordKey)
			throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (keystore) {
				keystore.setKeyEntry(alias, passwordKey, null, null);
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to insert the password entry in the Keystore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Inserts a new key entry containing private key and public key certificate
	 * (chain) in the Keystore.
	 * 
	 * @param alias -
	 *            Keystore alias of the entry
	 * @param privateKey -
	 *            private key to be inserted in the Keystore
	 * @param certs -
	 *            public key certificate chain (containing one or more
	 *            certificates)
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void insertKeyPairEntry(String alias, Key privateKey,
			Certificate[] certs) throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (keystore) {
				keystore.setKeyEntry(alias, privateKey, null, certs);
			}
		} catch (Exception ex) {
			String exMessage = "Failed to insert the key pair entry in the Keystore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Exports a key entry containing private key and public key certificate
	 * (chain) from the Keystore to a PKCS #12 file.
	 * 
	 * @param alias -
	 *            Keystore alias of the entry
	 * @param exportFile -
	 *            file to export to
	 * @param pkcs12Password -
	 *            password to be used to protect (encrypt) the PKCS #12 file
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason or PKCS #12
	 *             file could not be written
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void exportKeyPairEntry(String alias, File exportFile,
			String pkcs12Password) throws CMException,
			CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		FileOutputStream fos = null;

		// Export the key pair
		try {

			// Get the private key for the alias
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, null);

			// Get the related public key's certificate chain
			Certificate[] certChain = getCertificateChain(alias);

			// Create a new PKCS #12 keystore
			KeyStore newPkcs12 = KeyStore.getInstance("PKCS12", "BC");
			newPkcs12.load(null, null);

			// Place the private key and certificate chain into the PKCS #12
			// keystore.
			// Construct a new alias as "<SUBJECT_COMMON_NAME>'s <ISSUER_ORGANISATION> ID"

			String sDN = ((X509Certificate) certChain[0])
					.getSubjectX500Principal().getName(X500Principal.RFC2253);
			CMX509Util.parseDN(sDN);
			String sCN = CMX509Util.getCN();

			String iDN = ((X509Certificate) certChain[0])
					.getIssuerX500Principal().getName(X500Principal.RFC2253);
			CMX509Util.parseDN(iDN);
			String iCN = CMX509Util.getCN();

			String pkcs12Alias = sCN + "'s " + iCN + " ID";
			newPkcs12.setKeyEntry(pkcs12Alias, privateKey, new char[0],
					certChain);

			// Store the new PKCS #12 keystore on the disk
			fos = new FileOutputStream(exportFile);
			newPkcs12.store(fos, pkcs12Password.toCharArray());
			fos.close();
		} 
		catch (Exception ex) {
			String exMessage = "Failed to export the key pair from the Keystore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		} 
		finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	
	/**
	 * Gets certificate entry from the Keystore or Truststore.
	 * If the given alias name identifies a trusted certificate entry, the
	 * certificate associated with that entry is returned from the Truststore.
	 * If the given alias name identifies a key pair entry, the first element of
	 * the certificate chain of that entry is returned from the Keystore.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @param alias -
	 *            Keystore/Truststore alias of the entry
	 * @return - if the given alias identifies a trusted certificate entry, the
	 *         certificate associated with that entry is returned from the
	 *         Truststore. If the given alias name identifies a key (pair)
	 *         entry, the first element of the certificate chain (i.e. user's
	 *         public key certificate) of that entry is returned from the
	 *         Keystore. Note that the alias passed should not identify a secret
	 *         key entry.
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public Certificate getCertificate(String ksType, String alias)
			throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			if (ksType.equals(KEYSTORE)) {
				synchronized (keystore) {
					return keystore.getCertificate(alias);
				}
			} else if (ksType.equals(TRUSTSTORE)) {
				synchronized (truststore) {
					return truststore.getCertificate(alias);
				}
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to fetch certificate from the " + ksType + ".";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}

		// Keystore type not recognised
		String exMessage = "Keystore type " + ksType + " not supported.";
		throw (new CMException(exMessage));
	}

	
	/**
	 * Gets certificate chain for the key pair entry from the Keystore. This
	 * method works for Keystore only as Truststore does not contain key pair
	 * entries, but trusted certificate entries only.
	 * 
	 * @param alias -
	 *            Keystore alias of the key pair entry
	 * @return certificate chain (ordered with the user's public key certificate
	 *         first and the root certificate authority's last)
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public Certificate[] getCertificateChain(String alias) throws CMException,
			CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (keystore) {
				return keystore.getCertificateChain(alias);
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to fetch certificate chain for the keypair from the Keystore";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Inserts a trusted certificate entry in the Truststore.
	 * 
	 * 
	 * @param alias -
	 *            Truststore alias of the entry
	 * @param cert -
	 *            trusted certificate to import
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void insertTrustedCertificateEntry(String alias, Certificate cert)
			throws CMException, CMNotInitialisedException {
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (truststore) {
				truststore.setCertificateEntry(alias, cert);
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to insert trusted certificate entry in the Truststore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Checks if a given entry is a key entry in the Keystore.
	 * 
	 * @param alias -
	 *            Keystore alias of the entry
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
	 * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public boolean isKeyEntry(String alias) throws CMException,
			CMNotInitialisedException {
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			synchronized (keystore) {
				return keystore.isKeyEntry(alias);
			}
		} catch (Exception ex) {
			String exMessage = "Failed to access the key entry in the Keystore.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	
	/**
	 * Deletes an entry from a keystore.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @param alias -
	 *            keystore alias of the entry
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public void deleteEntry(String ksType, String alias) throws CMException,
			CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			if (ksType.equals(KEYSTORE)) {
				synchronized (keystore) {
					keystore.deleteEntry(alias);
				}

				// If this was key pair rather than password entry - remove the
				// associated URLs from the serviceURLsFile as well
				if (alias.startsWith("keypair"))
					deleteServiceURLs(alias);
				return;
			} 
			else if (ksType.equals(TRUSTSTORE)) {
				synchronized (truststore) {
					truststore.deleteEntry(alias);
				}
				return;
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to delete the entry from the " + ksType + ".";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}
	}

	/**
	 * Checks if a keystore contains an entry with the given alias.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @param alias -
	 *            keystore alias of the entry
	 * @return true if the keystore contains an entry with the given alias,
	 *         false otherwise
	 * @throws CMException -
	 *             if keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised   
	 */
	public boolean containsAlias(String ksType, String alias)
			throws CMException, CMNotInitialisedException {
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			if (ksType.equals(KEYSTORE))
				synchronized (keystore) {
					return keystore.containsAlias(alias);
			}
			else if (ksType.equals(TRUSTSTORE))
				synchronized (truststore) {
					return truststore.containsAlias(alias);
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to access the " + ksType + " to check if an alias exists.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}

		// Keystore type not recognised
		String exMessage = "Keystore type " + ksType + " not supported.";
		throw (new CMException(exMessage));
	}

	
	/**
	 * Gets all the aliases from a keystore.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @return list of all the alias names of this Keystore/Truststore.
	 * @throws CMException -
	 *             if the keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public Enumeration<String> getAliases(String ksType) throws CMException,
			CMNotInitialisedException {
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		try {
			if (ksType.equals(KEYSTORE))
				synchronized (keystore) {
					return keystore.aliases();
			}
			else if (ksType.equals(TRUSTSTORE))
				synchronized (truststore) {
					return truststore.aliases();
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to access the " + ksType + " to get the aliases.";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}

		// Keystore type not recognised
		String exMessage = "Keystore type " + ksType + " not supported.";
		throw (new CMException(exMessage));
	}

	
	/**
	 * Gets the creation date of an entry in a keystore.
	 * 
	 * Note that not all keystores support 'creation date' property, but Bouncy
	 * Castle 'UBER'-type keystores do.
	 * 
	 * @param ksType -
	 *            if the keystore in question is the Keystore or the Truststore
	 * @param alias -
	 *            keystore alias of the entry
	 * @return creation date of the entry
	 * @throws CMException -
	 *             if the keystore could not be accessed for some reason
     * @throws CMNotInitialisedException - if Credential Manager was not properly initialised
	 */
	public Date getCreationDate(String ksType, String alias) throws CMException, CMNotInitialisedException {

		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}
		
		try {
			if (ksType.equals(KEYSTORE))
				synchronized (keystore) {
					return keystore.getCreationDate(alias);
			}
			else if (ksType.equals(TRUSTSTORE))
				synchronized (truststore) {
					return truststore.getCreationDate(alias);
			}
		} 
		catch (Exception ex) {
			String exMessage = "Failed to get the creation date for the entry from the " + ksType + ".";
			logger.error("Credential Manager: " + exMessage, ex);
			throw (new CMException(exMessage));
		}

		// Keystore type not recognised
		String exMessage = "Keystore type " + ksType + " not supported.";
		throw (new CMException(exMessage));

	}

	
	/**
	 * Allows user to change the Keystore/Truststore password. 
	 * TODO This method is not tested yet!!!
	 */
	public void changeMasterPassword(String newPassword) throws CMException,
			CMNotInitialisedException {
		
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}
		
		masterPassword = newPassword;
		saveKeystore(KEYSTORE);
		saveKeystore(TRUSTSTORE);
	}

	
	/**
	 * Compares the user supplied password with the (previously entered) master
	 * password. This method can be used before some other methods on the
	 * Credential Manager are invoked, and it is up to the calling component 
	 * to ask user for the password and verify that it matches with the master 
	 * password as there is no way for the Credential Manager to know when to 
	 * ask for password.
	 */
	public boolean compareMasterPassword(String password) {
		return masterPassword.equals(password);
	}

	
	/**
	 * Gets a Security Agent Manager instance.
	 */
	public SecurityAgentManager getSecurityAgentManager() throws CMNotInitialisedException{
		
		if (!isInitialised) {
			throw new CMNotInitialisedException(
					"Credential Manager not initialised.");
		}

		return new SecurityAgentManager(keystore, serviceURLs, truststore);
	}
}
