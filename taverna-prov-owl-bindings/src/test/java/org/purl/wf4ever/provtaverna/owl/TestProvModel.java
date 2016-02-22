package org.purl.wf4ever.provtaverna.owl;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import org.apache.jena.ontology.Individual;


public class TestProvModel {

    private ProvModel provModel;

    @Before
    public void provModel() {
        provModel = new ProvModel();
    }
    
    @Test
    public void createBundle() throws Exception {
        Individual bundle = provModel.createBundle(uuid());
        assertEquals("Bundle", bundle.getOntClass().getLocalName());
        
    }

    private URI uuid() {
        return URI.create("urn:uuid:" + UUID.randomUUID());
    }
    
    @Test
    public void createEntity() throws Exception {
        Individual ent = provModel.createEntity(URI.create("http://example.com/entity"));
        provModel.model.write(System.out, "TURTLE");
    }
}
