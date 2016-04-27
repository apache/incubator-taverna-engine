package org.apache.taverna.prov.owl;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.UUID;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WriterGraphRIOT;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.system.RiotLib;
import org.junit.Before;
import org.junit.Test;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.sparql.util.Context;

public class TestTavernaProvModel {

    private TavernaProvModel provModel;

    @Before
    public void tavernaProvModel() {
        provModel = new TavernaProvModel();
    }

    @Test
    public void dummy() throws Exception {
        Individual bundle = provModel.createBundle(uuid());
        assertEquals("Bundle", bundle.getOntClass().getLocalName());

    }

    private URI uuid() {
        return URI.create("urn:uuid:" + UUID.randomUUID());
    }

    
    @Test
    public void createEntity() throws Exception {
        Individual ent = provModel.createEntity(URI
                .create("http://example.com/fred#test"));
        provModel.createEntity(URI.create("http://example.com/test"));
        OntModel model = provModel.model;
        model.write(System.out, "TURTLE", "http://example.com/fred");
        model.write(System.out, "RDF/XML", "http://example.com/fred");

        WriterGraphRIOT writer = RDFDataMgr.createGraphWriter(RDFFormat.TURTLE_BLOCKS);
        writer.write(System.out, model.getBaseModel().getGraph(), RiotLib.prefixMap(model.getGraph()), "http://example.com/fred", new Context());

    }
}
