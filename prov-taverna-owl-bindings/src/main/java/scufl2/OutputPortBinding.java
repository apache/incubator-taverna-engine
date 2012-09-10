package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;

@label({"Processor output Port binding"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#OutputPortBinding")
public interface OutputPortBinding {
	@label({"Bound output activity port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundOutputActivityPort")
	OutputActivityPort getScufl2BoundOutputActivityPort();
	@label({"Bound output activity port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundOutputActivityPort")
	void setScufl2BoundOutputActivityPort(OutputActivityPort scufl2BoundOutputActivityPort);

	@label({"bound output processor port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundOutputProcessorPort")
	OutputProcessorPort getScufl2BoundOutputProcessorPort();
	@label({"bound output processor port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundOutputProcessorPort")
	void setScufl2BoundOutputProcessorPort(OutputProcessorPort scufl2BoundOutputProcessorPort);

}
