package scufl2;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subPropertyOf;

@label({"Parent"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Parent")
public interface Parent {
	@label({"child"})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#child")
	Set<Child> getScufl2Childs();
	@label({"child"})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#child")
	void setScufl2Childs(Set<? extends Child> scufl2Childs);

	@label({"Port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#port")
	Set<Port> getScufl2Ports();
	@label({"Port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#port")
	void setScufl2Ports(Set<? extends Port> scufl2Ports);

}
