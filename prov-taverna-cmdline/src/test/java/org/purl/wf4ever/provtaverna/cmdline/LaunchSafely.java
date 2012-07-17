package org.purl.wf4ever.provtaverna.cmdline;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import net.sf.taverna.raven.launcher.Launchable;

import org.junit.After;
import org.junit.Before;

public class LaunchSafely {

	public LaunchSafely(Launchable launcher) {
		this.launcher = launcher;
	}
	
	private AvoidExit avoidExit;
	private Launchable launcher;
	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;
	private PrintStream oldOut;
	private PrintStream oldErr;

	// Note: Does not work with @BeforeClass in Eclipse!
	@Before
	public void avoidExit() {
		avoidExit = AvoidExit.install();
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

	public int launchSafely(String... args) throws Exception {
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

	@After
	public void allowExit() {
		avoidExit.uninstall();
	}
}
