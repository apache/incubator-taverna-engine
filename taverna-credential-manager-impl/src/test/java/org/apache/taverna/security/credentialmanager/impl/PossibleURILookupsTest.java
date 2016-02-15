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

package org.apache.taverna.security.credentialmanager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.JavaTruststorePasswordProvider;
import org.apache.taverna.security.credentialmanager.MasterPasswordProvider;
import org.apache.taverna.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import org.apache.taverna.security.credentialmanager.TrustConfirmationProvider;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 *
 */
public class PossibleURILookupsTest {
	private static final String SIMPLE_URI = "http://www.taverna.org.uk/filename.html";
	private static final String ROOT_URI = "http://www.taverna.org.uk/";
	private static final String NAIVE_ROOT_URI = "http://www.taverna.org.uk";

	private static final String NASTY_URI = "http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2";
	
	private static final String NASTY_URI_FRAGMENT = "http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2#frag1337";
	
	private static final String NASTY_URI_PARENT = "http://www.taverna.org.uk/path1/path2/path3/";

	private static final String NASTY_DOT_DOT_URI = "http://www.taverna.org.uk/path1/path2/path3/path4/../fish.html";

	private static CredentialManagerImpl credentialManager;
	private static File credentialManagerDirectory;
	private static DummyMasterPasswordProvider masterPasswordProvider;
	private static HTTPAuthenticatorServiceUsernameAndPasswordProvider httpAuthProvider;

	@BeforeClass
	public static void setUp() throws CMException, IOException {
		
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
					.setConfigurationDirectoryPath(credentialManagerDirectory.toPath());
		} catch (CMException e) {
			System.out.println(e.getStackTrace());
		}

		// Create the dummy master password provider
		masterPasswordProvider = new DummyMasterPasswordProvider();
		/* Short password to avoid issues with key sizes and Java strong crypto policy*/
		masterPasswordProvider.setMasterPassword("uber");
		List<MasterPasswordProvider> masterPasswordProviders = new ArrayList<MasterPasswordProvider>();
		masterPasswordProviders.add(masterPasswordProvider);
		credentialManager.setMasterPasswordProviders(masterPasswordProviders);
		
		// Put our HTTP authenticator in the list of service username and password providers
		httpAuthProvider = new HTTPAuthenticatorServiceUsernameAndPasswordProvider();
		ArrayList<ServiceUsernameAndPasswordProvider> serviceUsernameAndPasswordProviders = new ArrayList<ServiceUsernameAndPasswordProvider>();
		serviceUsernameAndPasswordProviders.add(httpAuthProvider);
		credentialManager.setServiceUsernameAndPasswordProviders(serviceUsernameAndPasswordProviders);

		// These can be empty
		credentialManager.setJavaTruststorePasswordProviders(new ArrayList<JavaTruststorePasswordProvider>());
		credentialManager.setTrustConfirmationProviders(new ArrayList<TrustConfirmationProvider>());
	}
	
	@AfterClass
	// Clean up the credentialManagerDirectory we created for testing
	public static void cleanUp(){
	
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
	
	@Test
	public void possibleLookupsNoRecursion() throws Exception {
		URI uri = URI.create(NASTY_URI);
		LinkedHashSet<URI> lookups = credentialManager.getPossibleServiceURIsToLookup(uri,
				false);
		assertTrue("Did not contain " + uri, lookups.remove(uri));
		assertTrue("Unexpected lookups:" + lookups, lookups.isEmpty());
	}

	@Test
	public void possibleLookupsDotDot() throws Exception {
		URI uri = URI.create(NASTY_DOT_DOT_URI);
		List<URI> expected = Arrays
				.asList(
						URI.create("http://www.taverna.org.uk/path1/path2/path3/fish.html"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/"),
						URI.create("http://www.taverna.org.uk/path1/path2/"),
						URI.create("http://www.taverna.org.uk/path1/"), 
						URI.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookups() throws Exception {
		URI uri = URI.create(NASTY_URI);
		List<URI> expected = Arrays
				.asList(
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/"),
						URI.create("http://www.taverna.org.uk/path1/path2/"),
						URI.create("http://www.taverna.org.uk/path1/"), URI
								.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}
	

	@Test
	public void possibleLookupsWithFragment() throws Exception {
		URI uri = URI.create(NASTY_URI_FRAGMENT);
		List<URI> expected = Arrays
				.asList(
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2#frag1337"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html#frag1337"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/#frag1337"),
						URI.create("http://www.taverna.org.uk/path1/path2/#frag1337"),
						URI.create("http://www.taverna.org.uk/path1/#frag1337"), URI
								.create("http://www.taverna.org.uk/#frag1337"),
				// And then again without fragment
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/filename.html"),
						URI.create("http://www.taverna.org.uk/path1/path2/path3/"),
						URI.create("http://www.taverna.org.uk/path1/path2/"),
						URI.create("http://www.taverna.org.uk/path1/"), URI
								.create("http://www.taverna.org.uk/")
				
				);

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupDirectory() throws Exception {
		URI uri = URI.create(NASTY_URI_PARENT);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk/path1/path2/path3/"), URI
				.create("http://www.taverna.org.uk/path1/path2/"), URI
				.create("http://www.taverna.org.uk/path1/"), URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupSimple() throws Exception {
		URI uri = URI.create(SIMPLE_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk/filename.html"), URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupRoot() throws Exception {
		URI uri = URI.create(ROOT_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupNaiveRoot() throws Exception {
		URI uri = URI.create(NAIVE_ROOT_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk"), URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(credentialManager
				.getPossibleServiceURIsToLookup(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

}

