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

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

public class WfprovModel extends ProvModel {

    private static final String DCTERMS = "http://purl.org/dc/terms/";
    private static final String WFPROV_OWL = "wfprov.owl";
    private static final String WFPROV = "http://purl.org/wf4ever/wfprov#";

    private static final String WFDESC_OWL = "wfdesc.owl";
    private static final String WFDESC = "http://purl.org/wf4ever/wfdesc#";
    
    protected OntModel wfdesc;
    protected OntModel wfprov;
    
    protected OntClass Process;
    protected OntClass Workflow;
    protected OntClass WorkflowRun;

    protected ObjectProperty wasEnactedBy;
    protected ObjectProperty describedByProcess;
    protected ObjectProperty describedByWorkflow;
    protected OntClass ProcessRun;
    protected ObjectProperty wasPartOfWorkflowRun;
    protected ObjectProperty hasPart;
    protected OntModel dcterms;
    protected ObjectProperty hasSubProcess;
    protected OntClass Artifact;
    protected ObjectProperty wasOutputFrom;
    protected ObjectProperty usedInput;
    protected OntClass Output;
    protected OntClass Input;
    protected ObjectProperty describedByParameter;

    
    @Override
    public void loadOntologies() {
        super.loadOntologies();
        loadDcTerms();
        loadWfDesc();
        loadWfprov();
        model.setNsPrefixes(wfprov);
    }

    protected synchronized void loadDcTerms() {
        if (dcterms != null) {
            return;
        }
        // As http://purl.org/dc/terms/ pulls in various rubbish we cheat
        OntModel ontModel = ModelFactory.createOntologyModel(); 
        hasPart = ontModel.createObjectProperty(DCTERMS + "hasPart");
        checkNotNull(ontModel, hasPart);
        dcterms = ontModel;
    }

    
    protected synchronized void loadWfDesc() {
        if (wfdesc != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFDESC_OWL, WFDESC);  
        
        hasSubProcess = ontModel.getObjectProperty(WFDESC + "hasSubProcess");
        Input = ontModel.getOntClass(WFDESC + "Input");
//      Input.addSuperClass(Role);
        Output = ontModel.getOntClass(WFDESC + "Output");
//    Output.addSuperClass(Role);

        Process = ontModel.getOntClass(WFDESC + "Process");
        Workflow = ontModel.getOntClass(WFDESC + "Workflow");
        checkNotNull(ontModel, hasSubProcess, Process, Workflow, Input, Output);

        wfdesc = ontModel;
    }
    
    protected synchronized void loadWfprov() {
        if (wfprov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFPROV_OWL, WFPROV);    
        
        wasEnactedBy = ontModel.getObjectProperty(WFPROV + "wasEnactedBy");
//        wasEnactedBy.addSuperProperty(wasAssociatedWith);
        describedByWorkflow = ontModel.getObjectProperty(WFPROV + "describedByWorkflow");
        describedByProcess = ontModel.getObjectProperty(WFPROV + "describedByProcess");
        describedByParameter = ontModel.getObjectProperty(WFPROV + "describedByParameter");
        usedInput = ontModel.getObjectProperty(WFPROV + "usedInput");
        wasOutputFrom = ontModel.getObjectProperty(WFPROV + "wasOutputFrom");

        wasPartOfWorkflowRun = ontModel.getObjectProperty(WFPROV + "wasPartOfWorkflowRun");
//        wasPartOfWorkflowRun.addSuperProperty(hasPart);

        Artifact = ontModel.getOntClass(WFPROV + "Artifact");
//        Artifact.addSuperClass(Entity);
        ProcessRun = ontModel.getOntClass(WFPROV + "ProcessRun");
//        ProcessRun.addSuperClass(Activity);
        WorkflowRun = ontModel.getOntClass(WFPROV + "WorkflowRun");
        
        checkNotNull(ontModel, wasEnactedBy, describedByWorkflow, describedByProcess,
                describedByParameter, 
                wasPartOfWorkflowRun, usedInput, wasOutputFrom, 
                 Artifact, ProcessRun, WorkflowRun);
        wfprov = ontModel;
    }
    
    public Individual createWorkflowRun(URI runURI) {
        Individual a = createActivity(runURI);
        a.setRDFType(WorkflowRun);
        return a;
    }
    
    public Individual createProcessRun(URI processURI) {
        Individual a = createActivity(processURI);
        a.setRDFType(ProcessRun);
        return a;
    }

    public void setWasPartOfWorkflowRun(Individual process,
            Individual parentProcess) {
        process.addProperty(wasPartOfWorkflowRun, parentProcess);
        parentProcess.addProperty(hasPart, process);
    }
    
    public Individual setWasEnactedBy(Individual workflowRun, Individual workflowEngine, Individual wfplan) {
        Individual association = setWasAssociatedWith(workflowRun, workflowEngine, wfplan);
        workflowRun.addProperty(wasEnactedBy, workflowEngine);
        return association;
    }

    public void setDescribedByWorkflow(Individual workflowRun, Individual wfplan) {
        workflowRun.addProperty(describedByWorkflow, wfplan);
    }
    
    public void addSubProcess(Individual wf, Individual proc) {
//      parentWf.getWfdescHasSubProcesses().add(procPlan);
//      objCon.addDesignation(parentWf, Resource.class).getDctermsHasPart().add(procPlan);
      wf.addProperty(hasSubProcess, proc);
      wf.addProperty(hasPart, proc);
  }


    public void setDescribedByProcess(Individual processRun, Individual processPlan) {
        processRun.addProperty(describedByProcess, processPlan);
    }

    
    public Individual createWorkflow(URI wfUri) {
        Individual wfplan = createPlan(wfUri);
        wfplan.addRDFType(Workflow);
        return wfplan;
    }
    public Individual createProcess(URI processUri) {
        Individual wfplan = createPlan(processUri);
        wfplan.addRDFType(Process);
        return wfplan;
    }

    public Individual createArtifact(URI dataURI) {
       Individual entity = createEntity(dataURI);
       entity.addRDFType(Artifact);
       return entity;
    }
    

    public Individual setUsedInput(Individual activity, Individual entity) {
        Individual usage = setUsed(activity, entity);
        activity.addProperty(usedInput, entity);
        return usage;
    }

    public Individual setWasOutputFrom(Individual entity, Individual activity) {
        Individual usage = setWasGeneratedBy(entity, activity);
        entity.addProperty(wasOutputFrom, activity);
        return usage;
    }
    
    public Individual createInputParameter(URI portURI) {
        Individual parameter = createRole(portURI);
        parameter.addRDFType(Input);
        return parameter;
    }

    
    public Individual createOutputParameter(URI portURI) {
        Individual parameter = createRole(portURI);
        parameter.addRDFType(Output);
        return parameter;
    }

    public void setDescribedByParameter(Individual entity, Individual portRole, Individual involvement) {
        setRole(involvement, portRole);        
        entity.addProperty(describedByParameter, portRole);
    }

}
