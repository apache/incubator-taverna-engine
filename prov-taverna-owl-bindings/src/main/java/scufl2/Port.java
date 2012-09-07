package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"Port"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Named"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Port")
public interface Port extends Named {
}
