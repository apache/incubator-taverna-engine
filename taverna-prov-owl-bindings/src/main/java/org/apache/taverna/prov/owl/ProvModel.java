/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.taverna.prov.owl;

import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;

import org.apache.jena.riot.IO_Jena;
import org.apache.jena.riot.system.IO_JenaWriters;
import org.apache.log4j.Logger;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.LocationMapper;

public class ProvModel {
    
    private static Logger logger = Logger.getLogger(ProvModel.class);

    private static final String EMPTY_PREFIX = "";

    protected static final OntModelSpec DEFAULT_ONT_MODEL_SPEC = OntModelSpec.OWL_MEM_RDFS_INF;

    protected static final String FOAF_0_1 = "http://xmlns.com/foaf/0.1/";

    protected static final String FOAF_RDF = "foaf.rdf";
    protected static final String ORE = "http://www.openarchives.org/ore/terms/";
    protected static final String PAV = "http://purl.org/pav/";
    protected static final String PAV_RDF = "pav.rdf";

    protected static final String PROV = "http://www.w3.org/ns/prov#";
    protected static final String PROV_AQ_RDF = "prov-aq.rdf";
    protected static final String PROV_DICTIONARY = "http://www.w3.org/ns/prov-dictionary#";
    protected static final String PROV_DICTIONARY_TTL = "prov-dictionary.ttl";
    protected static final String PROV_O = "http://www.w3.org/ns/prov-o#";
    protected static final String PROV_O_RDF = "prov-o.rdf";

    protected static final String RO = "http://purl.org/wf4ever/ro#";

    public OntClass Activity;
    public OntClass Association;
    public OntClass Bundle;
    public OntClass Collection;
    public OntClass Communication;
    public OntClass Dictionary;
    public OntClass EmptyCollection;
    public OntClass EmptyDictionary;
    public OntClass End;
    public OntClass Entity;
    public OntClass Generation;
    public OntClass KeyEntityPair;
    public OntClass Plan;
    public OntClass Role;
    public OntClass Start;
    public OntClass Usage;

    public ObjectProperty activity;
    public ObjectProperty agent;
    public ObjectProperty entity;
    public ObjectProperty hadDictionaryMember;
    public ObjectProperty hadMember;
    public ObjectProperty hadPlan;
    public ObjectProperty hadRole;
    public ObjectProperty pairEntity;
    public ObjectProperty qualifiedAssociation;
    public ObjectProperty qualifiedCommunication;
    public ObjectProperty qualifiedEnd;
    public ObjectProperty qualifiedGeneration;
    public ObjectProperty qualifiedStart;
    public ObjectProperty qualifiedUsage;
    public ObjectProperty used;
    public ObjectProperty wasAssociatedWith;
    public ObjectProperty wasDerivedFrom;
    public ObjectProperty wasGeneratedBy;
    public ObjectProperty wasInformedBy;

    public DatatypeProperty atTime;
    public DatatypeProperty endedAtTime;
    public DatatypeProperty pairKey;
    public DatatypeProperty startedAtTime;

    public OntModel model;
    
    private static boolean jenaFileManagerInitialized = false;
    
    protected OntModel prov;
    protected OntModel provDict;
    
    public ProvModel() {
        this(ModelFactory.createOntologyModel(DEFAULT_ONT_MODEL_SPEC));
    }

    public ProvModel(Model model) {
        String defaultPrefix = model.getNsPrefixURI(EMPTY_PREFIX);
        OntModel ontModel;
        if (model instanceof OntModel) {
            ontModel = (OntModel) model;
        } else {
            OntModelSpec spec = DEFAULT_ONT_MODEL_SPEC;
            ontModel = ModelFactory.createOntologyModel(spec, model);
        }
        setModel(ontModel);
        resetJena();
        initializeJenaFileManager();
        loadOntologies();
        
        if (defaultPrefix != null) {
            // Restore the defaultPrefix (:)
            model.setNsPrefix(EMPTY_PREFIX, defaultPrefix);
        } else {
            model.removeNsPrefix(EMPTY_PREFIX);
        }
    }

    private void initializeJenaFileManager() {
        if (! jenaFileManagerInitialized) {
            // Only initialize once to avoid adding the same locators
            // (but no need to synchronize, the occassional extra should be ok)
            jenaFileManagerInitialized = true;
            // So that it can find our location-mapping.n3
            // and the OWLs in classpath /org.apache.taverna.prov.owl/
            FileManager.get().addLocatorClassLoader(getClass().getClassLoader());
            
            Model mapping = ModelFactory.createDefaultModel();
            InputStream mappingStream = getClass().getResourceAsStream("/location-mapping.n3");
            mapping.read(mappingStream, "", "N3");
            
			FileManager.get().setLocationMapper(new LocationMapper(mapping));	
			
			OntDocumentManager.getInstance().setFileManager(FileManager.get());
        }
    }

    public void resetJena() {
		IO_Jena.resetJena();
    }

    public void addKeyPair(Individual dictionary, long position,
            Individual listItem) {
        dictionary.addProperty(hadMember, listItem);
        Individual keyPair = model.createIndividual(KeyEntityPair);
        keyPair.addProperty(pairEntity, listItem);
        keyPair.addLiteral(pairKey, position);
        dictionary.addProperty(hadDictionaryMember, keyPair);

    }

    protected void checkNotNull(OntModel model, Object... possiblyNulls) {
        int i = 0;
        for (Object check : possiblyNulls) {
            if (check == null) {
                throw new IllegalStateException("Could not load term #" + i
                        + " from ontology");
            }
            i++;
        }

    }

    public Individual createActivity(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Activity);
    }

    public Individual createBundle(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Bundle);
    }

    public Individual createDictionary(URI uri) {
        Individual artifact = createEntity(uri);
        artifact.addRDFType(Collection);
        artifact.addRDFType(Dictionary);
        return artifact;
    }

    public Individual createEntity(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Entity);
    }

    public Individual createPlan(URI planUri) {
        return model.createIndividual(planUri.toString(), Plan);
    }

    public Individual createRole(URI uri) {
        return model.createIndividual(uri.toASCIIString(), Role);
    }

    public OntModel getModel() {
        return model;
    }

    public void loadOntologies() {
        loadPROVO();
        loadProvDictionary();
        model.setNsPrefixes(prov);
    }

    protected OntModel loadOntologyFromClasspath(String classPathUri, String uri) {
        
        OntModel ontModel = ModelFactory.createOntologyModel();

        // Load from classpath
        InputStream inStream = getClass().getResourceAsStream(classPathUri);
        if (inStream == null) {
            throw new IllegalArgumentException("Can't load " + classPathUri);
        }
        // Ontology ontology = ontModel.createOntology(uri);
        if (classPathUri.endsWith(".ttl")) {
            ontModel.read(inStream, uri, "TURTLE");
        } else {
            ontModel.read(inStream, uri);
        }
        return ontModel;
    }

    protected synchronized void loadProvDictionary() {
        if (provDict != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(PROV_DICTIONARY_TTL,
                PROV_DICTIONARY);

        hadDictionaryMember = ontModel.getObjectProperty(PROV
                + "hadDictionaryMember");
        pairEntity = ontModel.getObjectProperty(PROV + "pairEntity");
        pairKey = ontModel.getDatatypeProperty(PROV + "pairKey");

        Dictionary = ontModel.getOntClass(PROV + "Dictionary");
        EmptyDictionary = ontModel.getOntClass(PROV + "EmptyDictionary");
        KeyEntityPair = ontModel.getOntClass(PROV + "KeyEntityPair");

        checkNotNull(ontModel, hadDictionaryMember, pairEntity, pairKey,
                Dictionary, EmptyDictionary, KeyEntityPair);

        provDict = ontModel;
    }

    protected synchronized void loadPROVO() {
        if (prov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(PROV_O_RDF, PROV_O);
        ontModel.setNsPrefix("prov", PROV_O);
        wasDerivedFrom = ontModel.getObjectProperty(PROV + "wasDerivedFrom");
        wasAssociatedWith = ontModel.getObjectProperty(PROV
                + "wasAssociatedWith");
        qualifiedAssociation = ontModel.getObjectProperty(PROV
                + "qualifiedAssociation");
        wasGeneratedBy = ontModel.getObjectProperty(PROV + "wasGeneratedBy");
        qualifiedGeneration = ontModel.getObjectProperty(PROV
                + "qualifiedGeneration");
        used = ontModel.getObjectProperty(PROV + "used");
        qualifiedUsage = ontModel.getObjectProperty(PROV + "qualifiedUsage");
        wasInformedBy = ontModel.getObjectProperty(PROV + "wasInformedBy");
        qualifiedCommunication = ontModel.getObjectProperty(PROV
                + "qualifiedCommunication");
        qualifiedStart = ontModel.getObjectProperty(PROV + "qualifiedStart");
        qualifiedEnd = ontModel.getObjectProperty(PROV + "qualifiedEnd");
        hadMember = ontModel.getObjectProperty(PROV + "hadMember");

        agent = ontModel.getObjectProperty(PROV + "agent");
        entity = ontModel.getObjectProperty(PROV + "entity");
        activity = ontModel.getObjectProperty(PROV + "activity");
        hadPlan = ontModel.getObjectProperty(PROV + "hadPlan");
        hadRole = ontModel.getObjectProperty(PROV + "hadRole");

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
        Role = ontModel.getOntClass(PROV + "Role");

        Generation = ontModel.getOntClass(PROV + "Generation");
        Usage = ontModel.getOntClass(PROV + "Usage");
        Communication = ontModel.getOntClass(PROV + "Communication");
        Collection = ontModel.getOntClass(PROV + "Collection");
        EmptyCollection = ontModel.getOntClass(PROV + "EmptyCollection");

        checkNotNull(ontModel, wasDerivedFrom, wasAssociatedWith,
                qualifiedAssociation, wasGeneratedBy, qualifiedGeneration,
                used, qualifiedUsage, wasInformedBy, qualifiedCommunication,
                agent, entity, activity, hadPlan, hadMember, hadRole,
                startedAtTime, endedAtTime, atTime, qualifiedStart,
                qualifiedEnd,

                Bundle, Entity, Activity, Association, Plan, Role, Generation,
                Usage, Communication, Start, End, Collection, EmptyCollection);
        prov = ontModel;
    }

    public void setEmptyDictionary(Individual dictionary) {
        dictionary.addRDFType(EmptyCollection);
        dictionary.addRDFType(EmptyDictionary);
    }

    public Individual setEndedAtTime(Individual endedActivity, Calendar time) {
        if (time == null) {
            logger.warn("Unknown end time");
            return null;
        }
        return setEndedAtTime(endedActivity, model.createTypedLiteral(time));
    }

    public Individual setEndedAtTime(Individual endedActivity, Literal time) {
        if (time == null) {
            logger.warn("Unknown end time");
            return null;
        }
        endedActivity.addLiteral(endedAtTime, time);
        Individual end = model.createIndividual(End);
        endedActivity.setPropertyValue(qualifiedEnd, end);
        end.addLiteral(atTime, time);
        return end;
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    public void setRole(Individual involvement, Individual role) {
        involvement.addProperty(hadRole, role);

    }

    public Individual setStartedAtTime(Individual startedActivity, Calendar time) {
        if (time == null) {
            logger.warn("Unknown start time");
            return null;
        }
        return setStartedAtTime(startedActivity, model.createTypedLiteral(time));
    }

    public Individual setStartedAtTime(Individual startedActivity, Literal time) {
        if (time == null) {
            logger.warn("Unknown start time");
            return null;
        }
        startedActivity.addLiteral(startedAtTime, time);
        Individual start = model.createIndividual(Start);
        startedActivity.setPropertyValue(qualifiedStart, start);
        start.addLiteral(atTime, time);
        return start;
    }

    public Individual setUsed(Individual activity, Individual usedEntity) {
        activity.addProperty(used, usedEntity);
        Individual usage = model.createIndividual(Usage);
        activity.addProperty(qualifiedUsage, usage);
        usage.addProperty(entity, usedEntity);
        return usage;
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

    public void setWasDerivedFrom(Individual derived, Individual original) {
        derived.addProperty(wasDerivedFrom, original);
    }

    public Individual setWasGeneratedBy(Individual generated,
            Individual generatingActivity) {
        generated.setPropertyValue(wasGeneratedBy, generatingActivity);
        Individual generation = model.createIndividual(Generation);
        generated.setPropertyValue(qualifiedGeneration, generation);
        generation.setPropertyValue(activity, generatingActivity);
        return generation;

    }

    public Individual setWasInformedBy(Individual informed, Individual informer) {
        informed.setPropertyValue(wasInformedBy, informer);
        Individual communication = model.createIndividual(Communication);
        informed.setPropertyValue(qualifiedCommunication, communication);
        communication.setPropertyValue(activity, informer);
        return communication;
    }

}
