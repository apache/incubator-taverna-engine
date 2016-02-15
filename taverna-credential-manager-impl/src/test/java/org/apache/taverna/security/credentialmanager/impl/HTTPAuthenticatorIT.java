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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.security.credentialmanager.JavaTruststorePasswordProvider;
import org.apache.taverna.security.credentialmanager.MasterPasswordProvider;
import org.apache.taverna.security.credentialmanager.ServiceUsernameAndPasswordProvider;
import org.apache.taverna.security.credentialmanager.TrustConfirmationProvider;
import org.apache.taverna.security.credentialmanager.UsernamePassword;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * 
 * Based on org.apache.taverna.security.credentialmanager.FixedPasswordProvider from the
 * Taverna 2 codebase. 
 * 
 *
 */
public class HTTPAuthenticatorIT {

	protected static final String WRONG_PASSWORD = "wrongOne";
	protected final static String PASSWORD = "basicPassword";
	protected static final String PASSWORD2 = "password2";
	protected static final String PASSWORD3 = "password3";
	protected static final String PASSWORD4 = "password4";
	protected static final String REALM = "realm1";
	protected static final String REALM2 = "realm2";
	protected final static String USERNAME = "basicUser";

	protected static final int PORT = 9638;

	private final class CountingAuthenticator extends
			CredentialManagerAuthenticator {
		
		public CountingAuthenticator(CredentialManager credManager) {
			super(credManager);
		}

		private int calls;

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			calls++;
			return super.getPasswordAuthentication();
		}
	}

	public class NullAuthenticator extends Authenticator {
	}

	protected static final String ROLE_NAME = "user";
	protected static final String HTML = "/html/";
	protected static Server server;
	protected static HashUserRealm userRealm;
	private static SecurityHandler sh;
	
	private static CredentialManagerImpl credentialManager;
	private static File credentialManagerDirectory;
	private static DummyMasterPasswordProvider masterPasswordProvider;
	private static HTTPAuthenticatorServiceUsernameAndPasswordProvider httpAuthProvider;

	@BeforeClass
	public static void startCredentialManager() throws CMException, IOException {
		
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

	@BeforeClass
	public static void jettyServer() throws Exception {

		server = new Server();

		Connector connector = new SelectChannelConnector();
		connector.setPort(PORT);
		server.setConnectors(new Connector[] { connector });
		ConstraintMapping cm = new ConstraintMapping();
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[] { ROLE_NAME });
		constraint.setAuthenticate(true);
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		sh = new SecurityHandler();
		userRealm = new HashUserRealm(REALM);
		userRealm.put(USERNAME, PASSWORD);
		userRealm.addUserToRole(USERNAME, ROLE_NAME);
		sh.setUserRealm(userRealm);
		sh.setConstraintMappings(new ConstraintMapping[] { cm });

		WebAppContext webappcontext = new WebAppContext();
		webappcontext.setContextPath("/");

		URL htmlRoot = HTTPAuthenticatorIT.class.getResource(HTML);
		assertNotNull("Could not find " + HTML, htmlRoot);
		webappcontext.setWar(htmlRoot.toExternalForm());
		
		webappcontext.addHandler(sh);

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { webappcontext,
				new DefaultHandler() });

		server.setHandler(handlers);
		server.start();
	}


	@AfterClass
	public static void shutdownJetty() throws Exception {
		server.stop();
	}

	@Before
	@After
	public void resetAuthenticator() throws CMException {
		Authenticator.setDefault(new NullAuthenticator());
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.resetCalls();
	}
	
	@Before
	public void resetAuthCache() throws CMException {
		credentialManager.resetAuthCache();
	}
	
	@Before
	public void resetUserRealmPassword() {
		userRealm.put(USERNAME, PASSWORD);
		userRealm.setName(REALM);
	}

	@Test()
	public void failsWithoutAuthenticator() throws Exception {
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));
	}

	@Test()
	public void withAuthenticator() throws Exception {
		assertEquals("Unexpected calls to password provider", 0,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		// Set the authenticator to our Credential Manager-backed one that also
		// counts calls to itself
		CountingAuthenticator authenticator = new CountingAuthenticator(credentialManager);
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
//		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
//				USERNAME, PASSWORD));
		
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), new UsernamePassword(
				USERNAME, PASSWORD));
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}
		System.out.println(c.getHeaderField(0));
		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));


		assertEquals("Unexpected prompt/realm", REALM, httpAuthProvider.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());

		// And test Java's cache:
		URLConnection c2 = url.openConnection();
		c2.connect();
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
		assertEquals("JVM invoked our authenticator again instead of caching", 1,
				authenticator.calls);
		assertEquals("Invoked our password provider again instead of caching",
				1, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());

	}
	
	@Test()
	public void withAuthenticatorResetJava() throws Exception {
		assertTrue("Could not reset JVMs authCache, ignore on non-Sun JVM", 
				credentialManager.resetAuthCache());
		
		assertEquals("Unexpected calls to password provider", 0,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator(credentialManager);
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
//		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
//				USERNAME, PASSWORD));
		
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), new UsernamePassword(
				USERNAME, PASSWORD));
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());

		assertEquals("Unexpected prompt/realm", REALM, httpAuthProvider.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());

		
		
		// And without Java's cache:
		assertTrue("Could not reset VMs authCache, ignore on non-Sun VM", 
				credentialManager.resetAuthCache());
		
		URLConnection c2 = url.openConnection();
		c2.connect();
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
		assertEquals("Did not invoke our authenticator again", 2,
				authenticator.calls);
		assertEquals("Did not invoke our password provider again",
				2, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());

	}
	
	
	@Test()
	public void differentRealm() throws Exception {
		
		assertEquals("Unexpected calls to password provider", 0,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator(credentialManager);
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
		// Different password in case resetAuthCache() did not run
		UsernamePassword userPassword = new UsernamePassword(
				USERNAME, PASSWORD4);
		userRealm.put(USERNAME, PASSWORD4);
//		userPassword.setShouldSave(true);
		//FixedPasswordProvider.setUsernamePassword(userPassword);
		
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), userPassword);
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, httpAuthProvider.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());

		
		// different realm should be treated as a second connection, and not even use saved credentials
		
		credentialManager.resetAuthCache();
		userRealm.setName(REALM2);
		
		URLConnection c2 = url.openConnection();
		c2.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
		
		assertEquals("Did not invoke authenticator again", 2,
				authenticator.calls);
		assertEquals("Did not invoke provider again",
				2, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
	
		assertEquals("Unexpected prompt/realm", REALM2, httpAuthProvider
				.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM2, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());	
	}
	

	@Test()
	public void wrongPasswordDontSave() throws Exception {
		assertEquals("Unexpected calls to password provider", 0,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator(credentialManager);
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);

		// Make the server expect different password so our cache is no longer
		// valid
		userRealm.put(USERNAME, PASSWORD2);
		// But we'll try with the old one, which we'll this time ask to save in
		// DB
		UsernamePassword usernamePassword = new UsernamePassword(USERNAME,
				PASSWORD);
		assertFalse("Should not be set to save by default", usernamePassword
				.isShouldSave());
		//FixedPasswordProvider.setUsernamePassword(usernamePassword);

		URL url = new URL("http://localhost:" + PORT + "/test.html");
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), usernamePassword);
		URLConnection c = url.openConnection();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, httpAuthProvider
				.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));

		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals("Should have asked provider as much as authenticator",
				authenticator.calls, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());


		// Update provider to now provide the right one
//		HTTPAuthenticatorServiceUsernameAndPasswordProvider.setUsernamePassword(new UsernamePassword(
//				USERNAME, PASSWORD2));
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), new UsernamePassword(
				USERNAME, PASSWORD2));
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.resetCalls();
		authenticator.calls = 0;

		URLConnection c2 = url.openConnection();
		try {
			c2.getContent();
		} catch (Exception ex) {
		}
		assertEquals("Did not call authenticator again with cache pw invalid",
				1, authenticator.calls);
		assertEquals(
				"id not called our password provider once",
				1, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
	}

	@Test()
	public void saveToDatabase() throws Exception {
		assertEquals("Unexpected calls to password provider", 0,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator(credentialManager);
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);

		// Make the server expect different password so our cache is no longer
		// valid (In case CredManager.resetAuthCache() did not succeed on non-Sun VMs)
		userRealm.put(USERNAME, PASSWORD3);
		// But we'll try with the old one, which we'll this time ask to save in
		// DB
		UsernamePassword usernamePassword = new UsernamePassword(USERNAME,
				PASSWORD2);
		usernamePassword.setShouldSave(true);
		//HTTPAuthenticatorServiceUsernameAndPasswordProvider.setUsernamePassword(usernamePassword);

		URL url = new URL("http://localhost:" + PORT + "/test.html");
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), usernamePassword);
		URLConnection c = url.openConnection();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, httpAuthProvider
				.getRequestMessage());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, HTTPAuthenticatorServiceUsernameAndPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));
		
		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals(
				"Asked our provider more than once, not saved in credMan?", 1,
				HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());

		

		// Expect the old one again
		userRealm.put(USERNAME, PASSWORD2);
		// We'll now set our provider to give an invalid password, but we should
		// not be asked
		// as the old one (now correct agian) is stored in DB
//		HTTPAuthenticatorServiceUsernameAndPasswordProvider.setUsernamePassword(new UsernamePassword(
//				USERNAME, WRONG_PASSWORD));
		httpAuthProvider.setServiceUsernameAndPassword(url.toURI(), new UsernamePassword(
				USERNAME, WRONG_PASSWORD));
		
		HTTPAuthenticatorServiceUsernameAndPasswordProvider.resetCalls();
		authenticator.calls = 0;

		URLConnection c2 = url.openConnection();
		try {
			c2.getContent();
		} catch (Exception ex) {
		}
		assertEquals("Did not call authenticator again with cache pw invalid",
				1, authenticator.calls);
		assertEquals(
				"Called our password provider instead of using credMan saved one",
				0, HTTPAuthenticatorServiceUsernameAndPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
	}

}
