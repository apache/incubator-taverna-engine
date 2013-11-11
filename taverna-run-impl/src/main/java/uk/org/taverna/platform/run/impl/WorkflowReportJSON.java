package uk.org.taverna.platform.run.impl;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.purl.wf4ever.robundle.manifest.Manifest.PathMixin;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.report.WorkflowReport;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WorkflowReportJSON {
    
    public void save(WorkflowReport wfReport, Path path) throws IOException {
//        ObjectNode objNode = save(wfReport);
        
//        injectContext(objNode);
        
        ObjectMapper om = makeObjectMapper();
//        Files.createFile(path);
        try (Writer w = Files.newBufferedWriter(path,
                Charset.forName("UTF-8"), StandardOpenOption.WRITE,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            om.writeValue(w, wfReport);
        }
    }
    
    protected static ObjectMapper makeObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        om.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        om.disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);        
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        om.addMixInAnnotations(Path.class, PathMixin.class);
        om.setSerializationInclusion(Include.NON_NULL);
        return om;
    }

    private void injectContext(ObjectNode objNode) {
        objNode.with("@context").put("wfprov", "http://purl.org/wf4ever/wfprov#");
        objNode.with("@context").put("wfdesc", "http://purl.org/wf4ever/wfdesc#");
        objNode.with("@context").put("prov", "http://www.w3.org/ns/prov#");
    }

    public void save(WorkflowReport wfReport) throws IOException {
        Path path = DataBundles.getWorkflowRunReport(wfReport.getDataBundle());
        save(wfReport, path);
    }

}
