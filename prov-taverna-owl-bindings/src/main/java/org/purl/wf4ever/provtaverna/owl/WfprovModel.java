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
    protected OntClass WorkflowRun;
    protected ObjectProperty wasEnactedBy;

    
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
        checkNotNull(ontModel);

        wfdesc = ontModel;
    }
    
    protected synchronized void loadWfprov() {
        if (wfprov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFPROV_OWL, WFPROV);    
        
        wasEnactedBy = ontModel.getObjectProperty(WFPROV + "wasEnactedBy");
        WorkflowRun = ontModel.getOntClass(WFPROV + "WorkflowRun");
        
        checkNotNull(ontModel, wasEnactedBy, WorkflowRun);
        wfprov = ontModel;
    }
    
    public Individual createWorkflowRun(URI runURI) {
        return model.createIndividual(runURI.toASCIIString(), WorkflowRun);
    }
    
    public void setWasEnactedBy(Individual workflowRun, Individual workflowEngine) {
        setWasAssociatedWith(workflowRun, workflowEngine, null);
        workflowRun.addProperty(wasEnactedBy, workflowEngine);
        
    }

}
