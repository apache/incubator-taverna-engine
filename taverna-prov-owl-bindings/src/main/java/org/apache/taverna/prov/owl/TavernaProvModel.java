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
