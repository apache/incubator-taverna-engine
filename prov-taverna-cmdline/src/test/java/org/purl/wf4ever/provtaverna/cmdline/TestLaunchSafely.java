package org.purl.wf4ever.provtaverna.cmdline;

import static org.junit.Assert.*;
import net.sf.taverna.raven.launcher.Launchable;

import org.junit.Test;

public class TestLaunchSafely extends LaunchSafely {

	public TestLaunchSafely() {
		super(new Launchable() {			
			@Override
			public int launch(String[] args) throws Exception {
				System.out.print("Some output");
				System.err.print("An error");
				System.exit(1); // Did not read Launchable javadoc
				System.out.println("Should not get here");
				return 0; // and not return 0
			}
		});
	}

	@Test
	public void testLaunchSafely() throws Exception {
		assertEquals(1, launchSafely("fred"));
		assertEquals("Some output", getOut());
		assertEquals("An error", getErr());		
	}
	
	@Test(expected = AvoidExit.ExitNotAllowed.class)
	public void exit() throws Exception {
		System.exit(-1);
	}
}
