package net.sf.taverna.t2.security.credentialmanager.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;

import org.junit.Test;

public class ExistingCredentialManagerTest {
	
	private DummyMasterPasswordProvider masterPasswordProvider;
	private File credentialManagerDirectory;
	
	@Test
	// Tests an existing Credential Manager/keystores
	public void testExtistingCredentialManager(){
		
		CredentialManagerImpl credentialManager = null;
		try {
			credentialManager = new CredentialManagerImpl();
		} catch (CMException e) {
			System.out.println(e);
			e.printStackTrace();
		}

		URL credentialManagerDirectory = getClass().getResource("/security/");
		String credentialManagerDirectoryPath = credentialManagerDirectory.getPath();
		System.out.println("Credential Manager's directory path: " + credentialManagerDirectoryPath);
		
		try {
			credentialManager.setConfigurationDirectoryPath(new File(credentialManagerDirectoryPath));
		} catch (CMException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
//		
//		// Test initilization
//		assertFalse(credentialManager.isInitialized());
//		try {
//			credentialManager.initialize();
//		} catch (CMException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertTrue(credentialManager.isInitialized());
	}

}
