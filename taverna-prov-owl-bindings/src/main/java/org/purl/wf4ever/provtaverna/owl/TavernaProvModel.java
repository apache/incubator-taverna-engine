package org.purl.wf4ever.provtaverna.owl;

import java.net.URI;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

public class TavernaProvModel extends WfprovModel {
    protected static final String CNT = "http://www.w3.org/2011/content#";

    protected static final String TAVERNAPROV = "http://ns.taverna.org.uk/2012/tavernaprov/";

    protected static final String TAVERNAPROV_TTL = "taverna-prov.ttl";

    public ObjectProperty content;

    public OntClass Content;
    public OntClass ContentAsBase64;
    public OntClass ContentAsText;
    public OntClass Error;
    public OntClass TavernaEngine;
    
    public OntModel cnt;
    public OntModel tavernaProv;
    
    public DatatypeProperty byteCount;
    public DatatypeProperty bytes;
    public DatatypeProperty characterEncoding;
    public DatatypeProperty chars;
    public DatatypeProperty errorMessage;
    public DatatypeProperty sha1;
    public DatatypeProperty sha512;
    public DatatypeProperty stackTrace;
    
    public Individual createTavernaEngine(URI uri) {
        Individual engine = model.createIndividual(uri.toASCIIString(), TavernaEngine);
        return engine;
    }
    
    private void loadCnt() {
        if (cnt != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath("content.owl", CNT);    
        
        bytes = ontModel.getDatatypeProperty(CNT + "bytes");
        chars = ontModel.getDatatypeProperty(CNT + "chars");
        characterEncoding = ontModel.getDatatypeProperty(CNT + "characterEncoding");
        
        ContentAsText = ontModel.getOntClass(CNT + "ContentAsText");
        ContentAsBase64 = ontModel.getOntClass(CNT + "ContentAsBase64");
        checkNotNull(ontModel, bytes, chars, characterEncoding, ContentAsText, ContentAsBase64);
        cnt = ontModel;
        
    }

    @Override
    public void loadOntologies() {
        super.loadOntologies();
        loadCnt();
        loadTavernaProv();
        model.setNsPrefixes(tavernaProv);
       
    }

    protected synchronized void loadTavernaProv() {
        if (tavernaProv != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(TAVERNAPROV_TTL, TAVERNAPROV);    
        
        content = ontModel.getObjectProperty(TAVERNAPROV + "content");        
        
        byteCount = ontModel.getDatatypeProperty(TAVERNAPROV + "byteCount");
        sha1 = ontModel.getDatatypeProperty(TAVERNAPROV + "sha1");
        sha512 = ontModel.getDatatypeProperty(TAVERNAPROV + "sha512");
        stackTrace = ontModel.getDatatypeProperty(TAVERNAPROV + "stackTrace");
        errorMessage = ontModel.getDatatypeProperty(TAVERNAPROV + "errorMessage");
        
        Content = ontModel.getOntClass(TAVERNAPROV + "Content");
        Error = ontModel.getOntClass(TAVERNAPROV + "Error");
        TavernaEngine = ontModel.getOntClass(TAVERNAPROV + "TavernaEngine");
        
        checkNotNull(ontModel, content, Content, byteCount,sha1, sha512, stackTrace, errorMessage, Content, Error, TavernaEngine);
        tavernaProv = ontModel;            
    }


    public Individual setContent(Individual entity, URI uri) {
        Individual cont = model.createIndividual(uri.toASCIIString(), Content);
        entity.setPropertyValue(content, cont);
        return cont;
    }


    public Individual createError(URI errorURI) {
        return model.createIndividual(errorURI.toASCIIString(), Error);
    }









 
}
