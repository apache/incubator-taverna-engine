package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;

@label({"Processor input port binding"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#InputPortBinding")
public interface InputPortBinding {
	@label({"Bound input activity port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundInputActivityPort")
	InputActivityPort getScufl2BoundInputActivityPort();
	@label({"Bound input activity port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundInputActivityPort")
	void setScufl2BoundInputActivityPort(InputActivityPort scufl2BoundInputActivityPort);

	@label({"bound input processor port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundInputProcessorPort")
	InputProcessorPort getScufl2BoundInputProcessorPort();
	@label({"bound input processor port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundInputProcessorPort")
	void setScufl2BoundInputProcessorPort(InputProcessorPort scufl2BoundInputProcessorPort);

}
