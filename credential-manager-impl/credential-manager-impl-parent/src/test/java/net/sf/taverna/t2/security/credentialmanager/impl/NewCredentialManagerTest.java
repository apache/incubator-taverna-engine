package net.sf.taverna.t2.security.credentialmanager.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class NewCredentialManagerTest {
	
	private CredentialManagerImpl credentialManager;
	private DummyMasterPasswordProvider masterPasswordProvider;
	private File credentialManagerDirectory;
	
	@Before
	// Creates a new empty Credential Manager/keystores
	public void setUp() {
		
		try {
			credentialManager = new CredentialManagerImpl();
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}
	    Random randomGenerator = new Random();
		String credentialManagerDirectoryPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "taverna-security-" + randomGenerator.nextInt(1000000);
		System.out.println("Credential Manager's directory path: " + credentialManagerDirectoryPath);
		credentialManagerDirectory = new File(credentialManagerDirectoryPath);
		try {
			credentialManager.setConfigurationDirectoryPath(credentialManagerDirectory);
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}
		
		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
	}
	
	@After
	// Clean up the credentialManagerDirectory we created for testing
	public void cleanUp(){
		assertTrue(credentialManagerDirectory.exists());
		assertFalse(credentialManagerDirectory.listFiles().length == 0); // something was created there
		
		try {
			FileUtils.deleteDirectory(credentialManagerDirectory);
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		
		assertFalse(credentialManagerDirectory.exists());

	}
	
	@Test
	public void testMasterPasword(){		
		try {
			credentialManager.confirmMasterPassword(masterPasswordProvider.getMasterPassword(false));
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}
	}
	
}
