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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.apache.taverna.robundle.Bundle;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.ExecutionService;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.run.api.RunProfile;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class RunTest extends DummyWorkflowReport {

    private static final WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
    private Run run;

    public ExecutionEnvironment mockExecution() throws Exception {
        ExecutionService exService = mock(ExecutionService.class);
        when(exService.createExecution(null,null,null,null,null)).thenReturn("id0");
        ExecutionEnvironment execution = mock(ExecutionEnvironment.class);
        when(execution.getExecutionService()).thenReturn(exService);
        when(exService.getWorkflowReport(null)).thenReturn(wfReport);
        return execution;
    }
    
    @Before
    public void makeRun() throws Exception {
        RunProfile runProfile = new RunProfile(mockExecution(), wfBundle, dataBundle);
        run = new Run(runProfile);
    }

    @Test
    public void getID() throws Exception {
        assertEquals(4, UUID.fromString(run.getID()).version());
    }
    
    @Test
    public void getBundle() throws Exception {
        Bundle bundle = run.getDataBundle();
        // Contains a copy of workflow
        assertEquals(wfBundle.getGlobalBaseURI(),
                DataBundles.getWorkflowBundle(bundle).getGlobalBaseURI());
        // Contains a run report
        Path runReport = DataBundles.getWorkflowRunReport(bundle);
        assertTrue(Files.exists(runReport));
        JsonNode runReportJson = DataBundles.getWorkflowRunReportAsJson(bundle);
        assertEquals("COMPLETED", runReportJson.get("state").asText());
    }
    

    @Test
    public void getState() throws Exception {
        assertEquals(State.COMPLETED, run.getState());
    }
    
}
