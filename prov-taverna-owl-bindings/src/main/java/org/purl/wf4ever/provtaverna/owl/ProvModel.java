package org.purl.wf4ever.provtaverna.owl;

import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProvModel {
    private static final String CNT = "http://www.w3.org/2011/content#";

    private static final String TAVERNAPROV = "http://ns.taverna.org.uk/2012/tavernaprov/";

    private static final String TAVERNAPROV_TTL = "taverna-prov.ttl";

    private static final OntModelSpec DEFAULT_ONT_MODEL_SPEC = OntModelSpec.OWL_MEM_RDFS_INF;
    
    private static final String PROV = "http://www.w3.org/ns/prov#";
    private static final String PROV_O = "http://www.w3.org/ns/prov-o#";
    private static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
    private static final String PAV = "http://purl.org/pav/";

    private static final String RO = "http://purl.org/wf4ever/ro#";
    private static final String ORE = "http://www.openarchives.org/ore/terms/";
    private static final String FOAF_RDF = "foaf.rdf";
    private static final String PAV_RDF = "pav.rdf";
    private static final String PROV_O_RDF = "prov-o.rdf";
    private static final String PROV_AQ_RDF = "prov-aq.rdf";

    private Resource Bundle = null;
    
    private OntModel model;

    private Object prov;

    private ObjectProperty wasDerivedFrom;

    private ObjectProperty content;

    private OntClass Content;

    private DatatypeProperty byteCount;

    private DatatypeProperty errorMessage;

    private DatatypeProperty sha1;

    private DatatypeProperty sha512;

    private DatatypeProperty stackTrace;

    private OntModel tavernaProv;

    private OntClass Error;

    private OntClass TavernaEngine;

    private OntModel cnt;

    private OntClass ContentAsText;

    private OntClass ContentAsBase64;

    private DatatypeProperty bytes;

    private DatatypeProperty chars;

    private DatatypeProperty characterEncoding;

    private OntClass Activity;

    private DatatypeProperty endedAtTime;

    private DatatypeProperty startedAtTime;

    public ProvModel() {
        this(ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC));
    }
    
    public ProvModel(Model model) {
        OntModel ontModel;
        if (model instanceof OntModel) {
            ontModel = (OntModel) model;
        } else {
            OntModelSpec spec = DEFAULT_ONT_MODEL_SPEC;
            ontModel = ModelFactory.createOntologyModel(spec, model);
        }
        setModel(ontModel);
        loadOntologies();
    }

    public void loadOntologies() {
        loadPROVO();
        loadCnt();
        loadTavernaProv();
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

    protected synchronized void loadPROVO() {
        if (prov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(PROV_O_RDF, PROV_O);    
        
        wasDerivedFrom = ontModel.getObjectProperty(PROV + "wasDerivedFrom");
        
        startedAtTime = ontModel.getDatatypeProperty(PROV + "startedAtTime");
        endedAtTime = ontModel.getDatatypeProperty(PROV + "endedAtTime");
        
        Bundle = ontModel.getOntClass(PROV + "Bundle");
        Activity = ontModel.getOntClass(PROV + "Activity");
        checkNotNull(ontModel, wasDerivedFrom, startedAtTime, endedAtTime, Bundle, Activity);
        prov = ontModel;            
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

    
    private void checkNotNull(Object... possiblyNulls) {
        int i=0;
        for (Object check : possiblyNulls) {
            if (check == null) {
                throw new IllegalStateException("Could not load item #" + i);
            }
            i++;
        }
        
    }
    
    protected OntModel loadOntologyFromClasspath(String classPathUri, String uri) {
        OntModel ontModel = ModelFactory.createOntologyModel();

        // Load from classpath
        InputStream inStream = getClass().getResourceAsStream(classPathUri);
        if (inStream == null) {
            throw new IllegalArgumentException("Can't load " + classPathUri);
        }
//        Ontology ontology = ontModel.createOntology(uri);
        ontModel.read(inStream, uri);
        return ontModel;
    }

    public OntModel getModel() {
        return model;
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    public Individual createBundle(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Bundle);
    }

    public Individual createTavernaEngine(URI uri) {
        return model.createIndividual(uri.toASCIIString(), TavernaEngine);
    }

    public Individual createActivity(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Activity);
    }

    public void setStartedAtTime(Individual activity,
            Calendar time) {
        activity.addLiteral(startedAtTime, time);
        
    }
    
    
}
