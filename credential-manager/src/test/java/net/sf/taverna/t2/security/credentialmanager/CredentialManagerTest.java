package net.sf.taverna.t2.security.credentialmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

public class CredentialManagerTest {
	private static final String SIMPLE_URI = "http://www.taverna.org.uk/filename.html";
	private static final String ROOT_URI = "http://www.taverna.org.uk/";
	private static final String NAIVE_ROOT_URI = "http://www.taverna.org.uk";

	private static final String NASTY_URI = "http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2";
	private static final String NASTY_URI_PARENT = "http://www.taverna.org.uk/path1/path2/path3/";

	private static final String NASTY_DOT_DOT_URI = "http://www.taverna.org.uk/path1/path2/path3/path4/../fish.html";

	@Test
	public void possibleLookupsNoRecursion() throws Exception {
		URI uri = URI.create(NASTY_URI);
		LinkedHashSet<URI> lookups = CredentialManager.possibleLookups(uri,
				false);
		assertTrue("Did not contain " + uri, lookups.remove(uri));
		assertTrue("Unexpected lookups:" + lookups, lookups.isEmpty());
	}

	@Test
	public void possibleLookupsDotDot() throws Exception {
		URI uri = URI.create(NASTY_DOT_DOT_URI);
		List<URI> expected = Arrays
				.asList(
						URI
								.create("http://www.taverna.org.uk/path1/path2/path3/fish.html"),
						URI
								.create("http://www.taverna.org.uk/path1/path2/path3/"),
						URI.create("http://www.taverna.org.uk/path1/path2/"),
						URI.create("http://www.taverna.org.uk/path1/"), URI
								.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookups() throws Exception {
		URI uri = URI.create(NASTY_URI);
		List<URI> expected = Arrays
				.asList(
						URI
								.create("http://www.taverna.org.uk/path1/path2/path3/filename.html?query=1&query2=2"),
						URI
								.create("http://www.taverna.org.uk/path1/path2/path3/filename.html"),
						URI
								.create("http://www.taverna.org.uk/path1/path2/path3/"),
						URI.create("http://www.taverna.org.uk/path1/path2/"),
						URI.create("http://www.taverna.org.uk/path1/"), URI
								.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

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

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupSimple() throws Exception {
		URI uri = URI.create(SIMPLE_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk/filename.html"), URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupRoot() throws Exception {
		URI uri = URI.create(ROOT_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

	@Test
	public void possibleLookupNaiveRoot() throws Exception {
		URI uri = URI.create(NAIVE_ROOT_URI);
		List<URI> expected = Arrays.asList(URI
				.create("http://www.taverna.org.uk"), URI
				.create("http://www.taverna.org.uk/"));

		ArrayList<URI> lookups = new ArrayList<URI>(CredentialManager
				.possibleLookups(uri, true));

		assertEquals("Did not match expected URIs", expected, lookups);
	}

}
