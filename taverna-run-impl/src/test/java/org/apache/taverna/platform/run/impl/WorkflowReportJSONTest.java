/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.platform.run.impl;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.After;
import org.junit.Test;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.Invocation;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.core.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowReportJSONTest extends DummyWorkflowReport {
    
    private final WorkflowReportJSON workflowReportJson = new WorkflowReportJSON();


    @Test
    public void save() throws Exception {
        workflowReportJson.save(wfReport, dataBundle);
        Path path = wfReport.getDataBundle().getRoot().resolve("/workflowrun.json");
        assertTrue("Did not save to expected path "  + path, Files.exists(path));

//        System.out.println(DataBundles.getStringValue(path));
        
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
            WorkflowReport wfReport = workflowReportJson.load(bundle);
            assertEquals(State.COMPLETED, wfReport.getState());
            assertNull(wfReport.getParentReport());
            
            assertEquals(wfBundle.getMainWorkflow().getName(), wfReport.getSubject().getName());
            URI mainWf = new URITools().uriForBean(wfBundle.getMainWorkflow());
            assertEquals(mainWf, wfReport.getSubjectURI());
            
            assertEquals(date(2013,1,2,13,37), wfReport.getCreatedDate());
            assertEquals(date(2013,1,2,14,50), wfReport.getStartedDate());
            assertEquals(date(2013,12,31,0,0), wfReport.getCompletedDate());
            assertNull(wfReport.getCancelledDate());
            assertNull(wfReport.getResumedDate());
            assertNull(wfReport.getPausedDate());
            assertTrue(wfReport.getResumedDates().isEmpty());
            assertTrue(wfReport.getPausedDates().isEmpty());
            
            // wf invocation
            assertEquals(1, wfReport.getInvocations().size());
            Invocation wfInvov = wfReport.getInvocations().first();
            assertEquals("wf0", wfInvov.getName());
            assertEquals("wf0", wfInvov.getId());
            assertNull(wfInvov.getParentId());
            assertNull(wfInvov.getParent());
            assertEquals(0, wfInvov.getIndex().length);
            assertSame(wfReport, wfInvov.getReport());
            assertEquals(State.COMPLETED, wfInvov.getState());

            assertEquals(date(2013,1,2,14,51), wfInvov.getStartedDate());
            assertEquals(date(2013,12,30,23,50), wfInvov.getCompletedDate());

            // wf invocation in/out
            assertEquals(1, wfInvov.getInputs().size());
            assertEquals(1, wfInvov.getOutputs().size());
            
            Path name = wfInvov.getInputs().get("name");
            assertEquals("/inputs/name", name.toString());
            assertEquals("John Doe", DataBundles.getStringValue(name));
            
            Path greeting = wfInvov.getOutputs().get("greeting");
            assertEquals("/outputs/greeting", greeting.toString());
            assertEquals("Hello, John Doe", DataBundles.getStringValue(greeting));
            
            
            // processor reports
            assertEquals(2, wfReport.getProcessorReports().size());
            for (ProcessorReport procRepo : wfReport.getProcessorReports()) {
                Processor processor = procRepo.getSubject();
                assertTrue(wfBundle.getMainWorkflow().getProcessors().containsName(processor.getName()));
                assertEquals(1, procRepo.getJobsQueued());
                assertEquals(2, procRepo.getJobsCompletedWithErrors());
                assertEquals(3, procRepo.getJobsCompleted());
                assertEquals(5, procRepo.getJobsStarted());
                

                assertEquals(date(2013,2,1,00,00), procRepo.getCreatedDate());
                assertEquals(date(2013,2,2,00,00), procRepo.getStartedDate());
                assertEquals(date(2013,7,28,12,0), procRepo.getCompletedDate());
                assertEquals(date(2013,2,5,0,0), procRepo.getPausedDate());
                assertEquals(Arrays.asList(date(2013,2,3,0,0), date(2013,2,5,0,0)),
                        procRepo.getPausedDates());
                assertEquals(date(2013,2,6,0,0), procRepo.getResumedDate());
                assertEquals(Arrays.asList(date(2013,2,4,0,0), date(2013,2,6,0,0)),
                        procRepo.getResumedDates());

                assertEquals(date(2013,7,28,12,0), procRepo.getCompletedDate());
                
                assertEquals(1, procRepo.getInvocations().size());
                Invocation pInvoc = procRepo.getInvocations().first();
                assertEquals(date(2013,2,2,11,00), pInvoc.getStartedDate());
                assertEquals(date(2013,2,2,13,00), pInvoc.getCompletedDate());
                assertEquals(State.COMPLETED, pInvoc.getState());
                assertEquals(wfInvov, pInvoc.getParent());
                assertEquals("wf0", pInvoc.getParentId());                
                if (processor.getName().equals("hello")) {
                    assertEquals("proc-hello0", pInvoc.getName());
                    assertEquals("wf0/proc-hello0", pInvoc.getId());
                    assertEquals(0, pInvoc.getInputs().size());
                    assertEquals(1, pInvoc.getOutputs().size());
                    assertEquals("Hello, ", DataBundles.getStringValue(pInvoc.getOutputs().get("value")));
                } else if (processor.getName().equals("Concatenate_two_strings")) {
                    assertEquals("proc-Concatenate_two_strings0", pInvoc.getName());
                    assertEquals("wf0/proc-Concatenate_two_strings0", pInvoc.getId());
                    assertEquals(2, pInvoc.getInputs().size());
                    assertEquals("Hello, ", DataBundles.getStringValue(pInvoc.getInputs().get("string1")));
                    assertEquals("John Doe", DataBundles.getStringValue(pInvoc.getInputs().get("string2")));

                    assertEquals(1, pInvoc.getOutputs().size());
                    assertEquals("Hello, John Doe", DataBundles.getStringValue(pInvoc.getOutputs().get("output")));                    
                } else {
                    fail("Unknown processor: " + processor.getName());
                }
                
                assertEquals(1, procRepo.getActivityReports().size());
                for (ActivityReport actRepo : procRepo.getActivityReports()) {
                    assertEquals(procRepo, actRepo.getParentReport());
                    assertEquals(State.CANCELLED, actRepo.getState());
                    assertEquals(date(2013,2,20,00,00), actRepo.getCreatedDate());
                    assertEquals(date(2013,2,20,11,00), actRepo.getStartedDate());
                    assertEquals(date(2013,2,21,11,30), actRepo.getCancelledDate());                   
                    // TODO: Test nested workflow
                }
            }
        }
        
    }
    
    @After
    public void closeBundle() throws Exception {
        Path saved = dataBundle.getSource().resolveSibling("workflowrun.bundle.zip");
        DataBundles.closeAndSaveBundle(dataBundle, saved);
        System.out.println("Saved to " + saved);
    }
}
