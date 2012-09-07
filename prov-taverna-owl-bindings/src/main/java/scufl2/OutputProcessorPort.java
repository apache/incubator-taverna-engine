package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Output processor port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#ProcessorPort", "http://ns.taverna.org.uk/2010/scufl2#SenderPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#OutputProcessorPort")
public interface OutputProcessorPort extends ProcessorPort, SenderPort {
}
