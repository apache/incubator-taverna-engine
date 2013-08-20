package org.purl.wf4ever.provtaverna.owl;

import java.net.URI;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class TavernaProvModel extends WfprovModel {
    protected static final String CNT = "http://www.w3.org/2011/content#";

    protected static final String TAVERNAPROV = "http://ns.taverna.org.uk/2012/tavernaprov/";

    protected static final String TAVERNAPROV_TTL = "taverna-prov.ttl";

    public DatatypeProperty byteCount;

    public DatatypeProperty bytes;

    public DatatypeProperty characterEncoding;
    public DatatypeProperty chars;

    public OntModel cnt;

    public ObjectProperty content;

    public OntClass Content;

    public OntClass ContentAsBase64;
    public OntClass ContentAsText;

    public OntClass Error;

    public DatatypeProperty errorMessage;

    public DatatypeProperty sha1;

    public DatatypeProperty sha512;

    public DatatypeProperty stackTrace;

    public OntClass TavernaEngine;

    public OntModel tavernaProv;

    public Individual createTavernaEngine(URI uri) {
        return model.createIndividual(uri.toASCIIString(), TavernaEngine);
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






   


 
}
