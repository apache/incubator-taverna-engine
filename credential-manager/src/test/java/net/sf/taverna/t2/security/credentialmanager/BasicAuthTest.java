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
	protected static final String ALTERNATIVE_PASSWORD = "expectedSomethingElse";
	protected static final String ALTERNATIVE_PASSWORD2 = "somethingDifferent";
	protected static final String REALM = "realm1337";

	protected static final int PORT = 9637;

	private final class CountingAuthenticator extends
			CredentialManagerAuthenticator {
		private int calls;

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			calls++;
			System.out.println("We're using scheme " + getRequestingScheme());
			return super.getPasswordAuthentication();
		}
		
		
		
	}

	public class NullAuthenticator extends Authenticator {
	}

	protected static final String ROLE_NAME = "user";
	protected static final String HTML = "/html/";
	protected final static String USERNAME = "basicUser";
	protected final static String PASSWORD = "basicPassword";
	protected static Server server;
	protected static HashUserRealm userRealm;

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

		SecurityHandler sh = new SecurityHandler();
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
		CredentialManager.getInstance().resetAuthCache();
	}

	@Test()
	public void failsWithoutAuthenticator() throws Exception {
		URL url = new URL("http://localhost:" + PORT + "/test.html");
		URLConnection c = url.openConnection();
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));
	}

	@Test()
	public void worksWithAuthenticator() throws Exception {
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

		assertEquals("Unexpected prompt/realm", REALM, FixedPasswordProvider
				.getRequestingPrompt());
		assertEquals("Unexpected URI", url.toURI(), FixedPasswordProvider
				.getServiceURI());

		assertEquals("Did not invoke authenticator", 1, authenticator.calls);
		assertEquals("Did not invoke our password provider", 1,
				FixedPasswordProvider.getCalls());

		assertEquals("HTTP/1.1 200 OK", c.getHeaderField(0));

		// And test Java's cache:
		URLConnection c2 = url.openConnection();
		c2.connect();
		assertEquals("VM invoke our authenticator again instead of caching", 1,
				authenticator.calls);
		assertEquals("Invoked our password provider again instead of caching",
				1, FixedPasswordProvider.getCalls());
		assertEquals("HTTP/1.1 200 OK", c2.getHeaderField(0));

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
		userRealm.put(USERNAME, ALTERNATIVE_PASSWORD);
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
		assertEquals("Unexpected URI", url.toURI(), FixedPasswordProvider
				.getServiceURI());

		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));

		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals("Should have asked provider as much as authenticator",
				authenticator.calls, FixedPasswordProvider.getCalls());


		// Update provider to now provide the right one
		FixedPasswordProvider.setUsernamePassword(new UsernamePassword(
				USERNAME, ALTERNATIVE_PASSWORD));
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
		userRealm.put(USERNAME, ALTERNATIVE_PASSWORD2);
		// But we'll try with the old one, which we'll this time ask to save in
		// DB
		UsernamePassword usernamePassword = new UsernamePassword(USERNAME,
				ALTERNATIVE_PASSWORD);
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
		assertEquals("Unexpected URI", url.toURI(), FixedPasswordProvider
				.getServiceURI());
		
		assertEquals("HTTP/1.1 401 Unauthorized", c.getHeaderField(0));
		
		assertTrue("Did not invoke authenticator enough times",
				authenticator.calls > 1);
		assertEquals(
				"Asked our provider more than once, not saved in credMan?", 1,
				FixedPasswordProvider.getCalls());

		

		// Expect the old one again
		userRealm.put(USERNAME, ALTERNATIVE_PASSWORD);
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
