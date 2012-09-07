package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Configurable"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Configurable")
public interface Configurable extends WorkflowElement {
}
