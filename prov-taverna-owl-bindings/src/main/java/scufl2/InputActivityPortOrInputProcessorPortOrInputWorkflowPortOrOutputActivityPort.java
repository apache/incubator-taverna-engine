package scufl2;

import java.math.BigInteger;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.label;
import rdfs.subClassOf;

@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Named", "http://ns.taverna.org.uk/2010/scufl2#Port", "http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@unionOf({"http://ns.taverna.org.uk/2010/scufl2#InputWorkflowPort", "http://ns.taverna.org.uk/2010/scufl2#InputProcessorPort", "http://ns.taverna.org.uk/2010/scufl2#InputActivityPort", "http://ns.taverna.org.uk/2010/scufl2#OutputActivityPort"})
public interface InputActivityPortOrInputProcessorPortOrInputWorkflowPortOrOutputActivityPort extends Named, Port, WorkflowElement {
	@label({"depth"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#portDepth")
	BigInteger getScufl2PortDepth();
	@label({"depth"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#portDepth")
	void setScufl2PortDepth(BigInteger scufl2PortDepth);

}
