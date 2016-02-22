package org.purl.wf4ever.provtaverna.owl;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.UUID;

import org.junit.Test;

import org.apache.jena.ontology.Individual;


public class TestWfprovModel {
    @Test
    public void dummy() throws Exception {
        ProvModel provModel = new WfprovModel();
        Individual bundle = provModel.createBundle(uuid());
        assertEquals("Bundle", bundle.getOntClass().getLocalName());
        
    }

    private URI uuid() {
        return URI.create("urn:uuid:" + UUID.randomUUID());
    }
}
