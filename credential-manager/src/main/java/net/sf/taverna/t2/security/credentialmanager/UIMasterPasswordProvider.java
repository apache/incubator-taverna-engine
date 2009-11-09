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

import java.awt.GraphicsEnvironment;
import java.io.File;

import javax.swing.JFrame;

/**
 * A UI pop-up that asks user for a master password for Credential Manager.
 * 
 * @author Alex Nenadic
 *
 */
public class UIMasterPasswordProvider implements MasterPasswordProviderSPI{

	public int canProvidePassword() {
		// Low priority - if password was provided e.g. on the command line 
		// then use that provider and do not pop up this one
		return 1;
	}

	public String getPassword() {

		// Check if this Taverna run is headless (i.e. Taverna Server or Taverna 
		// from command line) - do not do anything here if it is as we do not want 
		// any windows popping up even if they could
		if (GraphicsEnvironment.isHeadless()){
			return null;
		}

		File secConfigDirectory = CMUtil.getSecurityConfigurationDirectory();
		
		// Get the Keystore file
		File keystoreFile = new File(secConfigDirectory,"t2keystore.ubr"); 

		// Get the Truststore file
		File truststoreFile = new File(secConfigDirectory,"t2truststore.ubr"); 
		
		if (keystoreFile.exists() || truststoreFile.exists()){
			// Ask user to provide a master password for Credential Manager
			GetMasterPasswordDialog getPasswordDialog = new GetMasterPasswordDialog("Enter master password for Credential Manager");
			getPasswordDialog.setLocationRelativeTo(null);
			getPasswordDialog.setVisible(true);
			return getPasswordDialog.getPassword();
		}
		else{
			// Ask user to set the master password for Credential Manager (only the first time)
			SetMasterPasswordDialog setPasswordDialog = new SetMasterPasswordDialog((JFrame) null, "Set master password", true, "Set master password for Credential Manager");
			setPasswordDialog.setLocationRelativeTo(null);
			setPasswordDialog.setVisible(true);
			return setPasswordDialog.getPassword();
		}
	}

}
