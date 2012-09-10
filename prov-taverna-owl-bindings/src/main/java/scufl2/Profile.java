package scufl2;

import java.math.BigInteger;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Profile"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Child", "http://ns.taverna.org.uk/2010/scufl2#Named", "http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Profile")
public interface Profile extends Child, Named, WorkflowElement {
	@label({"activates configuration"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activatesConfiguration")
	Set<Configuration> getScufl2ActivatesConfigurations();
	@label({"activates configuration"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activatesConfiguration")
	void setScufl2ActivatesConfigurations(Set<? extends Configuration> scufl2ActivatesConfigurations);

	@label({"processorBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processorBinding")
	Set<ProcessorBinding> getScufl2ProcessorBinding();
	@label({"processorBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processorBinding")
	void setScufl2ProcessorBinding(Set<? extends ProcessorBinding> scufl2ProcessorBinding);

	@label({"profile position"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#profilePosition")
	BigInteger getScufl2ProfilePosition();
	@label({"profile position"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#profilePosition")
	void setScufl2ProfilePosition(BigInteger scufl2ProfilePosition);

}
