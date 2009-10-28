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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;

/**
 * Provides utility methods for loading a keystore from a file.
 * 
 * @author Alexandra Nenadic
 */
public class CMUtil {
	
	/**
	 * Loads a Bouncy Castle "UBER"-type keystore from a file on the disk and
	 * returns it.
	 */
	public static KeyStore load(File ksFile, String masterPassword) throws CMException {

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
	 * Get the configuration directory where the security stuff will be/is saved to.
	 */
	public static File getSecurityConfigurationDirectory() {
		
		File home = ApplicationRuntime.getInstance().getApplicationHomeDir();
//		File configDirectory = new File(home,"conf");
//		if (!configDirectory.exists()) {
//			configDirectory.mkdir();
//		}
		File secConfigDirectory = new File(home,"security");
		if (!secConfigDirectory.exists()) {
			secConfigDirectory.mkdir();
		}
		return secConfigDirectory;
	}
}
