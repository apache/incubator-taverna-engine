package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Input workflow port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#SenderPort", "http://ns.taverna.org.uk/2010/scufl2#WorkflowPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#InputWorkflowPort")
public interface InputWorkflowPort extends InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort, SenderPort, WorkflowPort {
}
