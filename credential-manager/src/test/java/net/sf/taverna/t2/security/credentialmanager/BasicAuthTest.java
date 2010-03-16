package net.sf.taverna.t2.security.credentialmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

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

public class BasicAuthTest {

	protected static final String WRONG_PASSWORD = "wrongOne";
	protected final static String PASSWORD = "basicPassword";
	protected static final String PASSWORD2 = "password2";
	protected static final String PASSWORD3 = "password3";
	protected static final String PASSWORD4 = "password4";
	protected static final String REALM = "realm1";
	protected static final String REALM2 = "realm2";
	protected final static String USERNAME = "basicUser";

	protected static final int PORT = 9637;

	private final class CountingAuthenticator extends
			CredentialManagerAuthenticator {
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

	@BeforeClass
	public static void startCredentialManager() throws CMException, IOException {
		/* Short password to avoid issues with key sizes */
		CredentialManager cm = CredentialManager.getInstance("f");
		assertEquals(cm, CredentialManager.getInstance());
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

		URL htmlRoot = BasicAuthTest.class.getResource(HTML);
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
		FixedPasswordProvider.resetCalls();
	}
	
	@Before
	public void resetAuthCache() throws CMException {
		CredentialManager.getInstance().resetAuthCache();
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
				FixedPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator();
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
				USERNAME, PASSWORD));
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				FixedPasswordProvider.getCalls());

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, FixedPasswordProvider
				.getServiceURI().toASCIIString());

		// And test Java's cache:
		URLConnection c2 = url.openConnection();
		c2.connect();
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
		assertEquals("VM invoke our authenticator again instead of caching", 1,
				authenticator.calls);
		assertEquals("Invoked our password provider again instead of caching",
				1, FixedPasswordProvider.getCalls());

	}
	
	@Test()
	public void withAuthenticatorResetJava() throws Exception {
		assertTrue("Could not reset VMs authCache, ignore on non-Sun VM", 
				CredentialManager.getInstance().resetAuthCache());
		
		assertEquals("Unexpected calls to password provider", 0,
				FixedPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator();
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
				USERNAME, PASSWORD));
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				FixedPasswordProvider.getCalls());

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, FixedPasswordProvider
				.getServiceURI().toASCIIString());

		
		
		// And without Java's cache:
		assertTrue("Could not reset VMs authCache, ignore on non-Sun VM", 
				CredentialManager.getInstance().resetAuthCache());
		
		URLConnection c2 = url.openConnection();
		c2.connect();
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
		assertEquals("Did not invoke our authenticator again", 2,
				authenticator.calls);
		assertEquals("Did not invoke our password provider again",
				2, FixedPasswordProvider.getCalls());

	}
	
	
	@Test()
	public void differentRealm() throws Exception {
		
		assertEquals("Unexpected calls to password provider", 0,
				FixedPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator();
		assertEquals("Unexpected calls to authenticator", 0,
				authenticator.calls);
		Authenticator.setDefault(authenticator);
		// Different password in case resetAuthCache() did not run
		UsernamePassword userPassword = new UsernamePassword(
				USERNAME, PASSWORD4);
		userRealm.put(USERNAME, PASSWORD4);
//		userPassword.setShouldSave(true);
		FixedPasswordProvider.setUsernamePassword(userPassword);
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();

		c.connect();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, FixedPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				FixedPasswordProvider.getCalls());

		
		// different realm should be treated as a second connection, and not even use saved credentials
		
		CredentialManager.getInstance().resetAuthCache();
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
				2, FixedPasswordProvider.getCalls());
	
		assertEquals("Unexpected prompt/realm", REALM2, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM2, FixedPasswordProvider
				.getServiceURI().toASCIIString());	
	}
	

	@Test()
	public void wrongPasswordDontSave() throws Exception {
		assertEquals("Unexpected calls to password provider", 0,
				FixedPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator();
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
		FixedPasswordProvider.setUsernamePassword(usernamePassword);

		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, FixedPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));

		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals("Should have asked provider as much as authenticator",
				authenticator.calls, FixedPasswordProvider.getCalls());


		// Update provider to now provide the right one
		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
				USERNAME, PASSWORD2));
		FixedPasswordProvider.resetCalls();
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
				1, FixedPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
	}

	@Test()
	public void saveToDatabase() throws Exception {
		assertEquals("Unexpected calls to password provider", 0,
				FixedPasswordProvider.getCalls());
		CountingAuthenticator authenticator = new CountingAuthenticator();
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
		FixedPasswordProvider.setUsernamePassword(usernamePassword);

		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();
		try {
			c.getContent();
		} catch (Exception ex) {
		}

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI().toASCIIString() + "#" + REALM, FixedPasswordProvider
				.getServiceURI().toASCIIString());
		
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));
		
		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals(
				"Asked our provider more than once, not saved in credMan?", 1,
				FixedPasswordProvider.getCalls());

		

		// Expect the old one again
		userRealm.put(USERNAME, PASSWORD2);
		// We'll now set our provider to give an invalid password, but we should
		// not be asked
		// as the old one (now correct agian) is stored in DB
		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
				USERNAME, WRONG_PASSWORD));
		FixedPasswordProvider.resetCalls();
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
				0, FixedPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));
	}

}
