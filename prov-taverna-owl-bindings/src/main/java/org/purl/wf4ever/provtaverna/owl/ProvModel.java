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
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ProvModel {

    protected static final OntModelSpec DEFAULT_ONT_MODEL_SPEC = OntModelSpec.OWL_MEM_RDFS_INF;
    
    protected static final String PROV = "http://www.w3.org/ns/prov#";
    protected static final String PROV_O = "http://www.w3.org/ns/prov-o#";
    protected static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";
    protected static final String PAV = "http://purl.org/pav/";

    protected static final String RO = "http://purl.org/wf4ever/ro#";
    protected static final String ORE = "http://www.openarchives.org/ore/terms/";
    protected static final String FOAF_RDF = "foaf.rdf";
    protected static final String PAV_RDF = "pav.rdf";
    protected static final String PROV_O_RDF = "prov-o.rdf";
    protected static final String PROV_AQ_RDF = "prov-aq.rdf";

    public Resource Bundle;
    
    public OntModel model;

    public Object prov;

    public ObjectProperty wasDerivedFrom;

    public OntClass Activity;

    public OntClass Association;
    
    public DatatypeProperty endedAtTime;

    public DatatypeProperty startedAtTime;

    public ObjectProperty wasAssociatedWith;

    public ObjectProperty qualifiedAssociation;

    public ObjectProperty agent;

    public ObjectProperty entity;

    public ObjectProperty activity;

    public ObjectProperty hadPlan;

    public OntClass Entity;

    public OntClass Plan;

    public ObjectProperty wasGeneratedBy;

    public ObjectProperty qualifiedGeneration;

    public OntClass Generation;

    public OntClass Usage;

    public ObjectProperty used;

    public ObjectProperty qualifiedUsage;

    public ObjectProperty wasInformedBy;

    private ObjectProperty qualifiedCommunication;

    private OntClass Communication;

    private OntClass Start;

    private OntClass End;

    private ObjectProperty qualifiedStart;

    private ObjectProperty qualifiedEnd;

    private DatatypeProperty atTime;

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
    }
    

    protected synchronized void loadPROVO() {
        if (prov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(PROV_O_RDF, PROV_O);    
        
        wasDerivedFrom = ontModel.getObjectProperty(PROV + "wasDerivedFrom");
        wasAssociatedWith = ontModel.getObjectProperty(PROV + "wasAssociatedWith");
        qualifiedAssociation = ontModel.getObjectProperty(PROV + "qualifiedAssociation");
        wasGeneratedBy = ontModel.getObjectProperty(PROV + "wasGeneratedBy");
        qualifiedGeneration = ontModel.getObjectProperty(PROV + "qualifiedGeneration");
        used = ontModel.getObjectProperty(PROV + "used");
        qualifiedUsage = ontModel.getObjectProperty(PROV + "qualifiedUsage");
        wasInformedBy = ontModel.getObjectProperty(PROV + "wasInformedBy");
        qualifiedCommunication = ontModel.getObjectProperty(PROV + "qualifiedCommunication");
        qualifiedStart = ontModel.getObjectProperty(PROV + "qualifiedStart");
        qualifiedEnd = ontModel.getObjectProperty(PROV + "qualifiedEnd");
        
        
        
        agent = ontModel.getObjectProperty(PROV + "agent");
        entity = ontModel.getObjectProperty(PROV + "entity");
        activity = ontModel.getObjectProperty(PROV + "activity");
        hadPlan = ontModel.getObjectProperty(PROV + "hadPlan");
        
        startedAtTime = ontModel.getDatatypeProperty(PROV + "startedAtTime");
        endedAtTime = ontModel.getDatatypeProperty(PROV + "endedAtTime");
        atTime = ontModel.getDatatypeProperty(PROV + "atTime");
        
        Bundle = ontModel.getOntClass(PROV + "Bundle");
        Entity = ontModel.getOntClass(PROV + "Entity");
        Activity = ontModel.getOntClass(PROV + "Activity");
        Start = ontModel.getOntClass(PROV + "Start");
        End = ontModel.getOntClass(PROV + "End");

        
        Association = ontModel.getOntClass(PROV + "Association");
        Plan = ontModel.getOntClass(PROV + "Plan");
        Generation = ontModel.getOntClass(PROV + "Generation");
        Usage = ontModel.getOntClass(PROV + "Usage");
        Communication = ontModel.getOntClass(PROV + "Communication");

        
        checkNotNull(ontModel, 
                wasDerivedFrom, wasAssociatedWith, qualifiedAssociation,
                wasGeneratedBy, qualifiedGeneration,
                used, qualifiedUsage,
                wasInformedBy, qualifiedCommunication,
                agent, entity, activity, hadPlan,
                
                startedAtTime, endedAtTime, atTime,   
                qualifiedStart, qualifiedEnd,
                
                Bundle, Entity, Activity, Association, Plan, 
                Generation, Usage, Communication, Start, End);
        prov = ontModel;            
    }

    
    protected void checkNotNull(Object... possiblyNulls) {
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
        if (classPathUri.endsWith(".ttl")) {
            ontModel.read(inStream, uri, "TURTLE");
        } else {
            ontModel.read(inStream, uri);
        }
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


    public Individual createActivity(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Activity);
    }

    public Individual createEntity(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Entity);
    }

    
    public Individual setEndedAtTime(Individual endedActivity,
            Calendar time) {
        return setEndedAtTime(endedActivity, model.createTypedLiteral(time));
    }

    public Individual setEndedAtTime(Individual endedActivity,
            Literal time) {
        endedActivity.addLiteral(endedAtTime, time);
        Individual end = model.createIndividual(End);
        endedActivity.setPropertyValue(qualifiedEnd, end);        
        end.addLiteral(atTime, time);
        return end;
    }
    
    public Individual setStartedAtTime(Individual startedActivity,
            Calendar time) {
        return setStartedAtTime(startedActivity, model.createTypedLiteral(time));
    }

    public Individual setStartedAtTime(Individual startedActivity,
            Literal time) {
        startedActivity.addLiteral(startedAtTime, time);
        Individual start = model.createIndividual(Start);
        startedActivity.setPropertyValue(qualifiedStart, start);
        start.addLiteral(atTime, time);
        return start;
    }
    
    public Individual setWasAssociatedWith(Individual activity,
            Individual associatedAgent, Individual plan) {
        activity.setPropertyValue(wasAssociatedWith, associatedAgent);
        Individual association = model.createIndividual(Association);
        activity.setPropertyValue(qualifiedAssociation, association);
        association.setPropertyValue(agent, associatedAgent);
        if (plan != null) {
            association.setPropertyValue(hadPlan, plan);
        }
        return association;
    }

    public Individual createPlan(URI planUri) {
        return model.createIndividual(planUri.toString(), Plan);
    }

    public Individual setWasGeneratedBy(Individual generated,
            Individual generatingActivity) {
        generated.setPropertyValue(wasGeneratedBy, generatingActivity);
        Individual generation = model.createIndividual(Generation);
        generated.setPropertyValue(qualifiedGeneration, generation);
        generation.setPropertyValue(activity, generatingActivity);
        return generation;
        
    }
    public Individual setWasInformedBy(Individual informed,
            Individual informer) {
        informed.setPropertyValue(wasInformedBy, informer);
        Individual communication = model.createIndividual(Communication);
        informed.setPropertyValue(qualifiedCommunication, communication);
        communication.setPropertyValue(activity, informer);
        return communication;
    }
    
    public void setWasDerivedFrom(Individual derived, Individual original) {
        derived.addProperty(wasDerivedFrom, original);
    }

    
}
