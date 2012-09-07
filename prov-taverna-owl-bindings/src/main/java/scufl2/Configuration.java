package scufl2;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Configuration")
public interface Configuration extends WorkflowElement {
	@label({"configuration"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#configure")
	Set<Configurable> getScufl2Configures();
	@label({"configuration"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#configure")
	void setScufl2Configures(Set<? extends Configurable> scufl2Configures);

}
