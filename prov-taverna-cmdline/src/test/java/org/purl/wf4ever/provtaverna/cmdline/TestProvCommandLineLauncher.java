package org.purl.wf4ever.provtaverna.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class TestProvCommandLineLauncher extends LaunchSafely {

	private static final String HELLOANYONE_T2FLOW = "/helloanyone.t2flow";
	private static final String BROKEN_T2FLOW = "/broken.t2flow";
	
	private File outDir;

	public TestProvCommandLineLauncher() {
		super(new ProvCommandLineLauncher());
	}

	public File writeT2Flow(String workflowResource) throws IOException {
		File t2flow = File.createTempFile("test", ".t2flow");
		URL resource = getClass().getResource(workflowResource);
		assertNotNull("Could not find " + workflowResource, resource);
		FileUtils.copyURLToFile(resource, t2flow);
		return t2flow;
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
	public void helloWithoutProv() throws Exception {
	    File t2flow = writeT2Flow(HELLOANYONE_T2FLOW);
		int status = launchSafely("-outputdir", outDir.getPath(),
				"-inputvalue", "name", "Fred", 
				t2flow.toURI().getPath());
//		System.err.println(getErr());
//		System.out.println(getOut());
		assertEquals(0, status);
		File[] outputs = outDir.listFiles();
		assertEquals(1, outputs.length);
		assertEquals("greeting", outputs[0].getName());
		// FIXME: Output file should have been written with utf-8, not system encoding!
		assertEquals("Hello, Fred", FileUtils.readFileToString(outputs[0]));
	}
	
	@Test
	public void helloWithProv() throws Exception {
	    File t2flow = writeT2Flow(HELLOANYONE_T2FLOW);
	    int status = launchSafely("-outputdir", outDir.getPath(),
				"-provenance", "-embedded",
				"-inputvalue", "name", "Fred", 
				t2flow.toURI().getPath());
		System.err.println(getErr());
		System.out.println(getOut());
		assertEquals(0, status);
		File[] outputs = outDir.listFiles();
		assertEquals(1, outputs.length);
		assertEquals("greeting", outputs[0].getName());
		
		Path zip = outDir.toPath().resolveSibling(outDir.getName() + ".robundle.zip");
		assertTrue(Files.isRegularFile(zip));
		
		try (FileSystem zipFs = FileSystems.newFileSystem(zip, getClass().getClassLoader())) {
		
    		Path provFile = zipFs.getPath("workflowrun.prov.ttl");
    		String prov = new String(Files.readAllBytes(provFile), "utf-8");
    		
    		// FIXME: Test actual content
    		assertTrue(prov.contains("@prefix prov:"));
//    		System.out.println(prov);
    		assertTrue(prov.contains("<outputs/greeting.txt>"));
    		
    		Path intermediates = zipFs.getPath("intermediates");
    		assertTrue(Files.isDirectory(intermediates));
		}
		
	}
	
	@Test
    public void brokenWithoutProv() throws Exception {
        File t2flow = writeT2Flow(BROKEN_T2FLOW);
        int status = launchSafely("-outputdir", outDir.getPath(),                
                t2flow.toURI().getPath());
//      System.err.println(getErr());
//      System.out.println(getOut());
        assertEquals(0, status);
        File[] outputs = outDir.listFiles();
        assertEquals(1, outputs.length);
        assertEquals("listC", outputs[0].getName());
    }
    
    @Test
    public void brokenWithProv() throws Exception {
        File t2flow = writeT2Flow(BROKEN_T2FLOW);
        int status = launchSafely("-outputdir", outDir.getPath(),
                "-provenance", "-embedded",
                t2flow.toURI().getPath());
        System.err.println(getErr());
        System.out.println(getOut());
        assertEquals(0, status);
        File[] outputs = outDir.listFiles();
        assertEquals(1, outputs.length);
        assertEquals("listC", outputs[0].getName());
        
        Path zip = outDir.toPath().resolveSibling(outDir.getName() + ".robundle.zip");
        assertTrue(Files.isRegularFile(zip));
        
        try (FileSystem zipFs = FileSystems.newFileSystem(zip, getClass().getClassLoader())) {
        
            Path provFile = zipFs.getPath("workflowrun.prov.ttl");
            String prov = new String(Files.readAllBytes(provFile), "utf-8");
            
            // FIXME: Test actual content
            assertTrue(prov.contains("@prefix prov:"));
//          System.out.println(prov);
            assertTrue(prov.contains("<outputs/listC/0.err>"));
  
            // FIXME: This is actually what we want to ensure - that the 
            // nested workflow is included in the provenance
            
//            assertTrue(prov.contains("Merge_String_List_to_a_String"));
            
            Path intermediates = zipFs.getPath("intermediates");
            assertTrue(Files.isDirectory(intermediates));
            
            
        }
    }
	
	
}
