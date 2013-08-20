package org.purl.wf4ever.provtaverna.owl;

import java.net.URI;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class WfprovModel extends ProvModel {
    
    private static final String WFPROV_OWL = "wfprov.owl";
    private static final String WFPROV = "http://purl.org/wf4ever/wfprov#";

    private static final String WFDESC_OWL = "wfdesc.owl";
    private static final String WFDESC = "http://purl.org/wf4ever/wfdesc#";
    
    protected OntModel wfdesc;
    protected OntModel wfprov;
    
    protected OntClass Workflow;
    protected OntClass WorkflowRun;

    protected ObjectProperty wasEnactedBy;
    protected ObjectProperty describedByProcess;
    protected ObjectProperty describedByWorkflow;

    
    @Override
    public void loadOntologies() {
        super.loadOntologies();
        loadWfDesc();
        loadWfprov();
    }

    protected synchronized void loadWfDesc() {
        if (wfdesc != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFDESC_OWL, WFDESC);  
        Workflow = ontModel.getOntClass(WFDESC + "Workflow");
        checkNotNull(ontModel, Workflow);

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

        WorkflowRun = ontModel.getOntClass(WFPROV + "WorkflowRun");
        
        checkNotNull(ontModel, wasEnactedBy, describedByWorkflow, describedByProcess,  WorkflowRun);
        wfprov = ontModel;
    }
    
    public Individual createWorkflowRun(URI runURI) {
        return model.createIndividual(runURI.toASCIIString(), WorkflowRun);
    }
    
    public Individual setWasEnactedBy(Individual workflowRun, Individual workflowEngine, Individual wfplan) {
        Individual association = setWasAssociatedWith(workflowRun, workflowEngine, wfplan);
        workflowRun.addProperty(wasEnactedBy, workflowEngine);
        workflowRun.addProperty(describedByWorkflow, wfplan);
        return association;
    }

    public Individual createWorkflow(URI wfUri) {
        Individual wfplan = createPlan(wfUri);
        wfplan.addRDFType(Workflow);
        return wfplan;
    }

    
}
