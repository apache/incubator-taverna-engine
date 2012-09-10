package tavernaprov;

import org.openrdf.annotations.Iri;
import rdfs.subClassOf;
import wfprov.WorkflowEngine;

@subClassOf({"http://purl.org/wf4ever/wfprov#WorkflowEngine"})
@Iri("http://ns.taverna.org.uk/2012/tavernaprov/TavernaEngine")
public interface TavernaEngine extends WorkflowEngine {
}
