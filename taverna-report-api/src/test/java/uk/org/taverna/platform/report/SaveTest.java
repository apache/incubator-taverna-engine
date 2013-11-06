package uk.org.taverna.platform.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;

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

    
    @SuppressWarnings("deprecation")
    @Before
    public void dummyReport() {
        wfReport = new WorkflowReport(wfBundle.getMainWorkflow());
        wfReport.setCreatedDate(date(2013,1,2,0,0));
        wfReport.setStartedDate(date(2013,1,2,1,0));
        for (Processor p : wfBundle.getMainWorkflow().getProcessors()) {
            ProcessorReport processorReport = new DummyProcessorReport(p);
            processorReport.setCreatedDate(date(2013,2,1,0,0));
            processorReport.setStartedDate(date(2013,2,2,0,0));
            processorReport.setPausedDate(date(2013,2,3,0,0));
            processorReport.setResumedDate(date(2013,2,4,0,0));
            processorReport.setPausedDate(date(2013,2,5,0,0));
            processorReport.setResumedDate(date(2013,2,6,0,0));
            
            wfReport.addChildReport(processorReport);
            for (ProcessorBinding b : scufl2Tools.processorBindingsForProcessor(p, wfBundle.getMainProfile())) {
                ActivityReport activityReport = new ActivityReport(b.getBoundActivity());
                activityReport.setCreatedDate(date(2013,2,20,0,0));
                activityReport.setCancelledDate(date(2013,2,21,11,30));
                processorReport.addChildReport(activityReport);
            }
            processorReport.setFailedDate(date(2013,2,28,23,59));
//            processorReport.setCompletedDate(date(2013,2,28,23,59));
        }
        wfReport.setCompletedDate(date(2013,12,31,0,0));
    }

    TimeZone UTC = TimeZone.getTimeZone("UTC");
    private WorkflowReport wfReport;
    
    private Date date(int year, int month, int date, int hourOfDay, int minute) {
        GregorianCalendar cal = new GregorianCalendar(UTC);
        cal.set(year, month, date, hourOfDay, minute);
        return cal.getTime();
    }


    @Test
    public void save() throws Exception {
        Path path = Files.createTempFile("test", ".json");
        System.out.println(path);
        new WorkflowReportJSON().save(wfReport, path);
        String json = new String(Files.readAllBytes(path), "utf8");
        System.out.println(json);
    }
}
