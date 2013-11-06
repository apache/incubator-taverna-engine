package uk.org.taverna.platform.report;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.purl.wf4ever.robundle.manifest.Manifest.PathMixin;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WorkflowReportJSON {
    
    public void save(WorkflowReport wfReport, Path path) throws IOException {
        ObjectNode objNode = save(wfReport);
        
        injectContext(objNode);
        
        ObjectMapper om = makeObjectMapper();
        try (Writer w = Files.newBufferedWriter(path,
                Charset.forName("UTF-8"), StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            om.writeValue(w, wfReport);
        }
    }

    private ObjectMapper makeObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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

    private ObjectNode save(WorkflowReport wfReport) {
        return JsonNodeFactory.instance.objectNode();        
    }

}
