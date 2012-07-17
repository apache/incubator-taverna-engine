package org.purl.wf4ever.provtaverna.cmdline;

import org.junit.Test;

public class TestAvoidExit {

	@Test
	public void cantExit() throws Exception {
		AvoidExit avoidExit = AvoidExit.install();
		try {
			System.exit(0);
		} catch (SecurityException ex) {
			System.out.println("OK");
		}
		System.out.println("Uninstalling");
		avoidExit.uninstall();
		System.out.println("Uninstalled");
	}

}
