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

import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.security.credentialmanager.JavaTruststorePasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Stian Soiland-Reyes
 * @author Alex Nenadic
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
					.setConfigurationDirectoryPath(credentialManagerDirectory);
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

