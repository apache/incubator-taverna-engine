package org.purl.wf4ever.provtaverna.owl;

import java.net.URI;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

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

    
    @Override
    public void loadOntologies() {
        super.loadOntologies();
        loadDcTerms();
        loadWfDesc();
        loadWfprov();
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

        Process = ontModel.getOntClass(WFDESC + "Process");
        Workflow = ontModel.getOntClass(WFDESC + "Workflow");
        checkNotNull(ontModel, hasSubProcess, Process, Workflow);

        wfdesc = ontModel;
    }
    
    protected synchronized void loadWfprov() {
        if (wfprov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFPROV_OWL, WFPROV);    
        
        wasEnactedBy = ontModel.getObjectProperty(WFPROV + "wasEnactedBy");
        describedByWorkflow = ontModel.getObjectProperty(WFPROV + "describedByWorkflow");
        describedByProcess = ontModel.getObjectProperty(WFPROV + "describedByProcess");
        wasPartOfWorkflowRun = ontModel.getObjectProperty(WFPROV + "wasPartOfWorkflowRun");

        
        ProcessRun = ontModel.getOntClass(WFPROV + "ProcessRun");
        WorkflowRun = ontModel.getOntClass(WFPROV + "WorkflowRun");
        
        checkNotNull(ontModel, wasEnactedBy, describedByWorkflow, describedByProcess, wasPartOfWorkflowRun, ProcessRun, WorkflowRun);
        wfprov = ontModel;
    }
    
    public Individual createWorkflowRun(URI runURI) {
        return model.createIndividual(runURI.toASCIIString(), WorkflowRun);
    }
    
    public Individual createProcessRun(URI processURI) {
        return model.createIndividual(processURI.toASCIIString(), ProcessRun);
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

    
}
