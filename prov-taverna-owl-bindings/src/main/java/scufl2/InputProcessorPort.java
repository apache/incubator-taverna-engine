package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Input processor port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#IterationStrategyNode", "http://ns.taverna.org.uk/2010/scufl2#ProcessorPort", "http://ns.taverna.org.uk/2010/scufl2#ReceiverPort"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#InputProcessorPort")
public interface InputProcessorPort extends InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort, InputActivityPortOrInputProcessorPortOrOutputActivityPort, IterationStrategyNode, ProcessorPort, ReceiverPort {
}
