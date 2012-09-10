package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Output activity port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#ActivityPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrOutputActivityPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#OutputActivityPort")
public interface OutputActivityPort extends ActivityPort, InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort, InputActivityPortOrInputProcessorPortOrOutputActivityPort {
}
