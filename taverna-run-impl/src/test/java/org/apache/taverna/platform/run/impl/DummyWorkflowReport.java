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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.Before;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.Invocation;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;


public class DummyWorkflowReport {
    
    private static TimeZone UTC = TimeZone.getTimeZone("UTC");

    protected Bundle dataBundle;
    
    protected WorkflowReport wfReport;

    protected static final Scufl2Tools scufl2Tools = new Scufl2Tools();
    protected static final WorkflowBundleIO workflowBundleIO = new WorkflowBundleIO();
    protected WorkflowBundle wfBundle;

    @Before
    public void loadWf() throws ReaderException, IOException {
        wfBundle = workflowBundleIO.readBundle(getClass().getResource("/hello_anyone.wfbundle"), 
                "application/vnd.taverna.scufl2.workflow-bundle");
    }

    protected Date date(int year, int month, int date, int hourOfDay, int minute) {
        GregorianCalendar cal = new GregorianCalendar(UTC);
        cal.setTimeInMillis(0);
        cal.set(year, month-1, date, hourOfDay, minute);
        return cal.getTime();
    }
    
    @Before
    public void dummyReport() throws Exception {
        wfReport = new WorkflowReport(wfBundle.getMainWorkflow());
        dataBundle = DataBundles.createBundle();
        wfReport.setDataBundle(dataBundle);
        wfReport.setCreatedDate(date(2013,1,2,13,37));
        wfReport.setStartedDate(date(2013,1,2,14,50));        
        Invocation wfInvocation = new Invocation("wf0", null, wfReport);
        wfInvocation.setStartedDate(date(2013,1,2,14,51));
        wfInvocation.setCompletedDate(date(2013,12,30,23,50));

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
            ProcessorReport processorReport = new ProcessorReport(p);
            processorReport.setJobsQueued(1);
            processorReport.setJobsStarted(5);
            processorReport.setJobsCompleted(3);
            processorReport.setJobsCompletedWithErrors(2);
                        
            wfReport.addProcessorReport(processorReport);

            processorReport.setCreatedDate(date(2013,2,1,0,0));
            processorReport.setStartedDate(date(2013,2,2,0,0));
            processorReport.setPausedDate(date(2013,2,3,0,0));
            processorReport.setResumedDate(date(2013,2,4,0,0));
            processorReport.setPausedDate(date(2013,2,5,0,0));
            processorReport.setResumedDate(date(2013,2,6,0,0));
            
            Invocation pInvocation = new Invocation("proc-" + p.getName() + "0", wfInvocation, processorReport);

            pInvocation.setStartedDate(date(2013,2,2,11,0));
            pInvocation.setCompletedDate(date(2013,2,2,13,0));

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
                activityReport.setStartedDate(date(2013,2,20,11,00));
                activityReport.setCancelledDate(date(2013,2,21,11,30));
                Invocation aInvocation = new Invocation("act-" + p.getName() + "0", pInvocation, activityReport);

                aInvocation.setStartedDate(date(2013,2,20,11,30));
//                aInvocation.setCompletedDate(date(2013,2,20,12,0));

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

}
