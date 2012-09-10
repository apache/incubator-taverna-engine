package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Output workflow port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#ReceiverPort", "http://ns.taverna.org.uk/2010/scufl2#WorkflowPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#OutputWorkflowPort")
public interface OutputWorkflowPort extends ReceiverPort, WorkflowPort {
}
