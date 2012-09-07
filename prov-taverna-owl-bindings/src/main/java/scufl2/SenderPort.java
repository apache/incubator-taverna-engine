package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import wfdesc.Output;

@label({"SenderPort"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Port", "http://purl.org/wf4ever/wfdesc#Output"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#SenderPort")
public interface SenderPort extends Port, Output {
}
