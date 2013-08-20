package org.purl.wf4ever.provtaverna.owl;

import java.net.URI;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class WfprovModel extends ProvModel {
    
    private static final String WFPROV_OWL = "wfprov.owl";
    private static final String WFPROV = "http://purl.org/wf4ever/wfprov#";

    private static final String WFDESC_OWL = "wfdesc.owl";
    private static final String WFDESC = "http://purl.org/wf4ever/wfdesc#";
    private OntModel wfdesc;
    private OntModel wfprov;
    private OntClass WorkflowRun;

    
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
        OntModel ontModel = loadOntologyFromClasspath(WFPROV_OWL, WFPROV);    
        checkNotNull(ontModel);

        wfdesc = ontModel;
    }
    
    protected synchronized void loadWfprov() {
        if (wfprov != null) {
            return;
        }
        OntModel ontModel = loadOntologyFromClasspath(WFPROV_OWL, WFPROV);    
        
        WorkflowRun = model.getOntClass(WFPROV + "WorkflowRun");
        
        checkNotNull(ontModel, WorkflowRun);
        wfprov = ontModel;
    }
    
    public Individual createWorkflowRun(URI runURI) {
        return model.createIndividual(runURI.toASCIIString(), WorkflowRun);
    }
}
