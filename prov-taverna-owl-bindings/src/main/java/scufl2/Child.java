package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subPropertyOf;

@label({"Child"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Child")
public interface Child {
	@label({"parent"})
	@subPropertyOf({"http://purl.org/dc/terms/isPartOf"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#parent")
	Parent getScufl2Parent();
	@label({"parent"})
	@subPropertyOf({"http://purl.org/dc/terms/isPartOf"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#parent")
	void setScufl2Parent(Parent scufl2Parent);

}
