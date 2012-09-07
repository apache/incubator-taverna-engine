package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"ActivityPort"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Child", "http://ns.taverna.org.uk/2010/scufl2#Port"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#ActivityPort")
public interface ActivityPort extends Child, Port {
}
