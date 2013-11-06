package uk.org.taverna.platform.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SaveTest {

    public class DummyProcessorReport extends ProcessorReport {
        public DummyProcessorReport(Processor processor) {
            super(processor);
        }

        @Override
        public int getJobsQueued() {
            return 1;
        }

        @Override
        public int getJobsStarted() {
            return 5;
        }

        @Override
        public int getJobsCompleted() {
            return 3;
        }

        @Override
        public int getJobsCompletedWithErrors() {
            return 2;
        }
    }

    private static final Scufl2Tools scufl2Tools = new Scufl2Tools();
    private static final WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
    private WorkflowBundle wfBundle;

    @Before
    public void loadWf() throws ReaderException, IOException {
        wfBundle = workflowBundleIO.readBundle(getClass().getResource("/hello_anyone.wfbundle"), 
                "application/vnd.taverna.scufl2.workflow-bundle");
    }

    
    @Before
    public void dummyReport() throws Exception {
        wfReport = new WorkflowReport(wfBundle.getMainWorkflow());
        Bundle dataBundle = DataBundles.createBundle();
        wfReport.setDataBundle(dataBundle);
        wfReport.setCreatedDate(date(2013,1,2,13,37));
        wfReport.setStartedDate(date(2013,1,2,14,50));        
        Invocation wfInvocation = new Invocation("wf0", null, wfReport);
        wfReport.addInvocation(wfInvocation);
        
        Path name = DataBundles.getPort(DataBundles.getInputs(dataBundle), "name");
        DataBundles.setStringValue(name, "John Doe");
        wfInvocation.getInputs().put("name", name);
        
        Path greeting = DataBundles.getPort(DataBundles.getOutputs(dataBundle), "greeting");
        DataBundles.setStringValue(greeting, "Hello, John Doe");
        wfInvocation.getOutputs().put("greeting", greeting);
        
        Path helloValue = DataBundles.getIntermediate(dataBundle, UUID.randomUUID());
        Path concatenateOutput = DataBundles.getIntermediate(dataBundle, UUID.randomUUID());
        
        for (Processor p : wfBundle.getMainWorkflow().getProcessors()) {
            ProcessorReport processorReport = new DummyProcessorReport(p);
            wfReport.addProcessorReport(processorReport);

            processorReport.setCreatedDate(date(2013,2,1,0,0));
            processorReport.setStartedDate(date(2013,2,2,0,0));
            processorReport.setPausedDate(date(2013,2,3,0,0));
            processorReport.setResumedDate(date(2013,2,4,0,0));
            processorReport.setPausedDate(date(2013,2,5,0,0));
            processorReport.setResumedDate(date(2013,2,6,0,0));
            
            Invocation pInvocation = new Invocation("proc-" + p.getName() + "0", wfInvocation, processorReport);
            if (p.getName().equals("hello")) {
                pInvocation.getOutputs().put("value", helloValue);
                DataBundles.setStringValue(helloValue, "Hello, ");
            } else if (p.getName().equals("Concatenate_two_strings")) {
                pInvocation.getInputs().put("string1", helloValue);
                pInvocation.getInputs().put("string2", name);
                pInvocation.getOutputs().put("output", concatenateOutput);
                DataBundles.setStringValue(concatenateOutput, "Hello, John Doe");
            } else {
                throw new Exception("Unexpected processor " + p);
            }
            processorReport.addInvocation(pInvocation);
            
            for (ProcessorBinding b : scufl2Tools.processorBindingsForProcessor(p, wfBundle.getMainProfile())) {
                ActivityReport activityReport = new ActivityReport(b.getBoundActivity());
                processorReport.addActivityReport(activityReport);
                activityReport.setCreatedDate(date(2013,2,20,0,0));
                activityReport.setCancelledDate(date(2013,2,21,11,30));
                Invocation aInvocation = new Invocation("act-" + p.getName() + "0", pInvocation, activityReport);
                activityReport.addInvocation(aInvocation);
                aInvocation.getInputs().putAll(pInvocation.getInputs());
                aInvocation.getOutputs().putAll(pInvocation.getOutputs());
            }
//            processorReport.setFailedDate(date(2013,7,28,23,59));
            // In the summer to check that daylight saving does not sneak in
            processorReport.setCompletedDate(date(2013,7,28,12,00));
        }
        wfReport.setCompletedDate(date(2013,12,31,0,0));
    }

    TimeZone UTC = TimeZone.getTimeZone("UTC");
    private WorkflowReport wfReport;
    
    private Date date(int year, int month, int date, int hourOfDay, int minute) {
        GregorianCalendar cal = new GregorianCalendar(UTC);
        cal.setTimeInMillis(0);
        cal.set(year, month-1, date, hourOfDay, minute);
        return cal.getTime();
    }


    @Test
    public void save() throws Exception {
        new WorkflowReportJSON().save(wfReport);
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
    public void testName() throws Exception {
        
    }
    
    @After
    public void closeBundle() throws Exception {
        Path saved = wfReport.getDataBundle().getSource().resolveSibling("workflowrun.bundle.zip");
        DataBundles.closeAndSaveBundle(wfReport.getDataBundle(), saved);
        System.out.println("Saved to " + saved);
    }
}
