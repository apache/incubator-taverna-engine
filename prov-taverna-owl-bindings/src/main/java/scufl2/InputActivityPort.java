package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Input activity port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#ActivityPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrOutputActivityPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#InputActivityPort")
public interface InputActivityPort extends ActivityPort, InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort, InputActivityPortOrInputProcessorPortOrOutputActivityPort {
}
