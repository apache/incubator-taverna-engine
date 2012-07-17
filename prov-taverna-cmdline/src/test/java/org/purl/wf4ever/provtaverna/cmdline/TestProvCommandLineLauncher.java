package org.purl.wf4ever.provtaverna.cmdline;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class TestProvCommandLineLauncher extends LaunchSafely {

	private static final String HELLOANYONE_T2FLOW = "/helloanyone.t2flow";
	private File t2flow;
	private File outDir;

	public TestProvCommandLineLauncher() {
		super(new ProvCommandLineLauncher());
	}

	@Before
	public void writeT2Flow() throws IOException {
		t2flow = File.createTempFile("test", ".t2flow");
		URL resource = getClass().getResource(HELLOANYONE_T2FLOW);
		assertNotNull("Could not find " + HELLOANYONE_T2FLOW, resource);
		FileUtils.copyURLToFile(resource, t2flow);
	}

	@Test
	public void testHelp() throws Exception {
		assertEquals(0, launchSafely("--help"));
		assertTrue(getOut().contains("usage:"));
//		System.out.println(getOut());
	}

	@Before
	public void outputDir() throws IOException {
		outDir = File.createTempFile("test", "");
		outDir.delete();
		//outDir.mkdir();
	}

	@Test
	public void runWithoutProv() throws Exception {
		int status = launchSafely("-outputdir", outDir.getPath(),
				"-inputvalue", "name", "Fred", t2flow.getPath());
		System.err.println(getErr());
		System.out.println(getOut());
		assertEquals(0, status);
		File[] outputs = outDir.listFiles();
		assertEquals(1, outputs.length);
		assertEquals("greeting", outputs[0].getName());
		// FIXME: Output file should have been written with utf-8, not system encoding!
		assertEquals("Hello, Fred", FileUtils.readFileToString(outputs[0]));
	}
	
	@Test
	public void runWithProv() throws Exception {
		int status = launchSafely("-outputdir", outDir.getPath(),
				"-provenance", "-embedded",
				"-inputvalue", "name", "Fred", 
				t2flow.getPath());
		System.err.println(getErr());
		System.out.println(getOut());
		assertEquals(0, status);
		File[] outputs = outDir.listFiles();
		assertEquals(2, outputs.length);
		assertTrue(new File(outDir, "workflowrun.prov.ttl").isFile());
	}
	
}
