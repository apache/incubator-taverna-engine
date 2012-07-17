package org.purl.wf4ever.provtaverna.cmdline;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProvCommandLineLauncher {

	private AvoidExit avoidExit;
	private ProvCommandLineLauncher launcher;
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;
	private PrintStream oldOut;
	private PrintStream oldErr;

	// Note: Does not work with @BeforeClass in Eclipse!
	@Before
	public void avoidExit() {
		avoidExit = AvoidExit.install();
	}

	@Test(expected = AvoidExit.ExitNotAllowed.class)
	public void exit() throws Exception {
		System.exit(-1);
	}

	@Before
	public void makeLauncher() {
		launcher = new ProvCommandLineLauncher();
	}

	public void captureErrOut() {
		oldOut = System.out;
		oldErr = System.err;
		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(out, true, "utf-8"));
			System.setErr(new PrintStream(err, true, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void restoreErrOut() {
		System.setErr(oldErr);
		System.setOut(oldOut);
	}

	public String getOut() {
		try {
			return out.toString("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getErr() {
		try {
			return err.toString("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public int launchSafely(String... args) {
		captureErrOut();
		// This AvoidExit magic would not have been needed if the Launchable
		// return type had actually been used rather than calling System.exit
		// manually
		try {
			return launcher.launch(args);
		} catch (AvoidExit.ExitNotAllowed ex) {
			return ex.getStatus();
		} finally {
			restoreErrOut();
		}
	}

	@Test
	public void testLaunchSafely() throws Exception {
		assertEquals(0, launchSafely("executeworkflow", "--help"));
		System.out.println(getOut());
		assertTrue(getOut().contains("usage:"));
	}

	@After
	public void allowExit() {
		avoidExit.uninstall();
	}
}
