package uk.org.taverna.platform.run.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class RunTest {

    private static final WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
    private WorkflowBundle wfBundle;
    private Bundle dataBundle;
    private Run run;

    public ExecutionEnvironment mockExecution() throws Exception {
        ExecutionService exService = mock(ExecutionService.class);
        when(exService.createExecution(null,null,null,null,null)).thenReturn("id0");
        ExecutionEnvironment execution = mock(ExecutionEnvironment.class);
        when(execution.getExecutionService()).thenReturn(exService);
        WorkflowReport workflowReport = mock(WorkflowReport.class);
        when(exService.getWorkflowReport(null)).thenReturn(workflowReport);
        when(workflowReport.getState()).thenReturn(State.FAILED);
        return execution;
    }
    
    @Before
    public void makeRun() throws Exception {
        RunProfile runProfile = new RunProfile(mockExecution(), loadWf(), makeDataBundle());
        run = new Run(runProfile);
    }

    @Test
    public void getID() throws Exception {
        assertEquals(4, UUID.fromString(run.getID()).version());
    }
    

    @Test
    public void getState() throws Exception {
        assertEquals(State.FAILED, run.getState());
    }

    
    public WorkflowBundle loadWf() throws ReaderException, IOException {
        wfBundle = workflowBundleIO.readBundle(
                getClass().getResource("/hello_anyone.wfbundle"),
                "application/vnd.taverna.scufl2.workflow-bundle");
        return wfBundle;
    }

    public Bundle makeDataBundle() throws Exception {
        dataBundle = DataBundles.createBundle();
        return dataBundle;
    }

    @After
    public void closeBundle() throws Exception {
        if (dataBundle == null) {
            return;
        }
        Path saved = dataBundle.getSource().resolveSibling(
                "workflowrun.bundle.zip");
        DataBundles.closeAndSaveBundle(dataBundle, saved);
        System.out.println("Saved to " + saved);
    }

}
