package scufl2;

import java.math.BigInteger;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.label;
import rdfs.subClassOf;

@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Child", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#Named", "http://ns.taverna.org.uk/2010/scufl2#Port", "http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@unionOf({"http://ns.taverna.org.uk/2010/scufl2#InputProcessorPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#OutputActivityPort"})
public interface InputActivityPortOrInputProcessorPortOrOutputActivityPort extends Child, InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort, Named, Port, WorkflowElement {
	@label({"granular depth"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#granularPortDepth")
	BigInteger getScufl2GranularPortDepth();
	@label({"granular depth"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#granularPortDepth")
	void setScufl2GranularPortDepth(BigInteger scufl2GranularPortDepth);

}
