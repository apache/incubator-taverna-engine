package uk.org.taverna.platform.run.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.report.Invocation;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.common.URITools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowReportJSONTest extends DummyWorkflowReport {
    
    private final WorkflowReportJSON workflowReportJson = new WorkflowReportJSON();


    @Test
    public void save() throws Exception {
        workflowReportJson.save(wfReport, dataBundle);
        Path path = wfReport.getDataBundle().getRoot().resolve("/workflowrun.json");
        assertTrue("Did not save to expected path "  + path, Files.exists(path));

        JsonNode json;
        try (InputStream jsonIn = Files.newInputStream(path)) {
            json = new ObjectMapper().readTree(jsonIn);
        }
        assertEquals("COMPLETED", json.get("state").asText());
        assertEquals("2013-01-02T13:37:00.000+0000", json.get("createdDate").asText());
        assertEquals("2013-01-02T14:50:00.000+0000", json.get("startedDate").asText());
        assertEquals("2013-12-31T00:00:00.000+0000", json.get("completedDate").asText());
        String wfId = wfBundle.getGlobalBaseURI().toString();
        assertEquals(wfId + "workflow/Hello_Anyone/", 
                json.get("subject").asText());
        
        // workflow invocation
        JsonNode wfInvoc = json.get("invocations").get(0);
        assertEquals("wf0", wfInvoc.get("id").asText());
        assertEquals("wf0", wfInvoc.get("name").asText());
        
        assertEquals("2013-01-02T14:51:00.000+0000", wfInvoc.get("startedDate").asText());
        assertEquals("2013-12-30T23:50:00.000+0000", wfInvoc.get("completedDate").asText());

        String inputsName = wfInvoc.get("inputs").get("name").asText();
        assertEquals("/inputs/name", inputsName);
        String outputsGreeting = wfInvoc.get("outputs").get("greeting").asText();
        assertEquals("/outputs/greeting", outputsGreeting);
        assertEquals(
                "John Doe",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(inputsName)));        
        assertEquals(
                "Hello, John Doe",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(outputsGreeting)));        

        // NOTE: This assumes alphabetical ordering when constructing
        // processor reports - which generally is given as
        // Workflow.getProcessors() is sorted.
        JsonNode proc0 = json.get("processorReports").get(0);
        assertEquals(wfId + "workflow/Hello_Anyone/processor/Concatenate_two_strings/",
                proc0.get("subject").asText());
        assertEquals("COMPLETED", proc0.get("state").asText());
        assertEquals("2013-02-01T00:00:00.000+0000", proc0.get("createdDate").asText());
        assertEquals("2013-02-02T00:00:00.000+0000", proc0.get("startedDate").asText());
        assertEquals("2013-02-03T00:00:00.000+0000", proc0.get("pausedDates").get(0).asText());
        assertEquals("2013-02-05T00:00:00.000+0000", proc0.get("pausedDates").get(1).asText());
        assertEquals("2013-02-05T00:00:00.000+0000", proc0.get("pausedDate").asText());

        assertEquals("2013-02-04T00:00:00.000+0000", proc0.get("resumedDates").get(0).asText());
        assertEquals("2013-02-06T00:00:00.000+0000", proc0.get("resumedDates").get(1).asText());
        assertEquals("2013-02-06T00:00:00.000+0000", proc0.get("resumedDate").asText());

        assertEquals("2013-07-28T12:00:00.000+0000", proc0.get("completedDate").asText());

        // processor invocations
        JsonNode pInvoc0 = proc0.get("invocations").get(0);
        assertEquals("proc-Concatenate_two_strings0", pInvoc0.get("name").asText());
        assertEquals("wf0/proc-Concatenate_two_strings0", pInvoc0.get("id").asText());
        assertEquals("wf0", pInvoc0.get("parent").asText());

        String inputString1 = pInvoc0.get("inputs").get("string1").asText();
        assertTrue(inputString1.startsWith("/intermediates/"));
        assertEquals(
                "Hello, ",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(inputString1)));        
        
        String inputString2 = pInvoc0.get("inputs").get("string2").asText();
        assertEquals("/inputs/name", inputString2);
        String output = pInvoc0.get("outputs").get("output").asText();
        assertTrue(output.startsWith("/intermediates/"));
        assertEquals(
                "Hello, John Doe",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(output)));        

        // Activity reports
        JsonNode act0 = proc0.get("activityReports").get(0);
        assertEquals("CANCELLED", act0.get("state").asText());
        assertEquals(wfId + "profile/taverna-2.4.0/activity/Concatenate_two_strings/", 
                act0.get("subject").asText());
        
        
        // activity invocation
        JsonNode aInvoc0 = act0.get("invocations").get(0);

        assertEquals("act-Concatenate_two_strings0", aInvoc0.get("name").asText());
        assertEquals("wf0/proc-Concatenate_two_strings0/act-Concatenate_two_strings0", aInvoc0.get("id").asText());
        assertEquals("wf0/proc-Concatenate_two_strings0", aInvoc0.get("parent").asText());

        String actInputString1 = aInvoc0.get("inputs").get("string1").asText();
        assertTrue(actInputString1.startsWith("/intermediates/"));
        assertEquals(
                "Hello, ",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(actInputString1)));        
        
        String actInputString2 = aInvoc0.get("inputs").get("string2").asText();
        assertEquals("/inputs/name", actInputString2);
        String actOutput = pInvoc0.get("outputs").get("output").asText();
        assertTrue(actOutput.startsWith("/intermediates/"));
        assertEquals(
                "Hello, John Doe",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(actOutput)));        

        
        
        
        JsonNode proc1 = json.get("processorReports").get(1);
        assertEquals(wfId + "workflow/Hello_Anyone/processor/hello/",
                proc1.get("subject").asText());
        assertEquals("COMPLETED", proc1.get("state").asText());
        assertEquals("2013-02-01T00:00:00.000+0000", proc1.get("createdDate").asText());
        assertEquals("2013-02-02T00:00:00.000+0000", proc1.get("startedDate").asText());
        // etc.

        JsonNode pInvoc1 = proc1.get("invocations").get(0);

        String value = pInvoc1.get("outputs").get("value").asText();
        assertTrue(value.startsWith("/intermediates/"));
        assertEquals(
                "Hello, ",
                DataBundles.getStringValue(wfReport.getDataBundle().getRoot()
                        .resolve(value)));        
        assertEquals(inputString1, value);
    }
    
    @Test
    public void load() throws Exception {
        URI bundleUri = getClass().getResource("/workflowrun.bundle.zip").toURI();
        Path bundlePath = Paths.get(bundleUri);
        try (Bundle bundle = DataBundles.openBundle(bundlePath)) {
            WorkflowReport report = workflowReportJson.load(bundle);
            assertEquals(State.COMPLETED, report.getState());
            assertNull(report.getParentReport());
            
            assertEquals(wfBundle.getMainWorkflow().getName(), report.getSubject().getName());
            URI mainWf = new URITools().uriForBean(wfBundle.getMainWorkflow());
            assertEquals(mainWf, report.getSubjectURI());
            
            assertEquals(date(2013,1,2,13,37), report.getCreatedDate());
            assertEquals(date(2013,1,2,14,50), report.getStartedDate());
            assertEquals(date(2013,12,31,0,0), report.getCompletedDate());
            assertNull(report.getCancelledDate());
            assertNull(report.getResumedDate());
            assertNull(report.getPausedDate());
            assertTrue(report.getResumedDates().isEmpty());
            assertTrue(report.getPausedDates().isEmpty());
            
            // wf invocation
            assertEquals(1, report.getInvocations().size());
            Invocation inv = report.getInvocations().first();
            assertEquals("wf0", inv.getName());
            assertEquals("wf0", inv.getId());
            assertNull(inv.getParentId());
            assertNull(inv.getParent());
            assertEquals(0, inv.getIndex().length);
            assertSame(report, inv.getReport());
            assertEquals(State.COMPLETED, inv.getState());

            assertEquals(date(2013,1,2,14,51), inv.getStartedDate());
            assertEquals(date(2013,12,30,23,50), inv.getCompletedDate());

            // wf invocation in/out
            assertEquals(1, inv.getInputs().size());
            assertEquals(1, inv.getOutputs().size());
            
            Path name = inv.getInputs().get("name");
            assertEquals("/inputs/name", name.toString());
            assertEquals("John Doe", DataBundles.getStringValue(name));
            
            Path greeting = inv.getOutputs().get("greeting");
            assertEquals("/outputs/greeting", greeting.toString());
            assertEquals("Hello, John Doe", DataBundles.getStringValue(greeting));
        }
        
    }
    
    @After
    public void closeBundle() throws Exception {
        Path saved = dataBundle.getSource().resolveSibling("workflowrun.bundle.zip");
        DataBundles.closeAndSaveBundle(dataBundle, saved);
        System.out.println("Saved to " + saved);
    }
}
