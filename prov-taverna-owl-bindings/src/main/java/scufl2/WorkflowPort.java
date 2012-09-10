package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"ReceiverPort"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Port"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#WorkflowPort")
public interface WorkflowPort extends Port {
}
