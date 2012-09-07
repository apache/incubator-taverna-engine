package scufl2;

import java.lang.String;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@label({"Named"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Named")
public interface Named extends WorkflowElement {
	@label({"name"})
	@subPropertyOf({"http://purl.org/dc/terms/identifier"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#name")
	String getScufl2Name();
	@label({"name"})
	@subPropertyOf({"http://purl.org/dc/terms/identifier"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#name")
	void setScufl2Name(String scufl2Name);

}
