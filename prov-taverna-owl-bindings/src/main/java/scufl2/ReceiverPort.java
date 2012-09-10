package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import wfdesc.Input;

@label({"ReceiverPort"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Port", "http://purl.org/wf4ever/wfdesc#Input"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#ReceiverPort")
public interface ReceiverPort extends Port, Input {
}
