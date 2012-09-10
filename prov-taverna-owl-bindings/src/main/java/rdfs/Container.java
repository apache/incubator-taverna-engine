package rdfs;

import org.openrdf.annotations.Iri;

/** The class of RDF containers. */
@label({"Container"})
@subClassOf({"http://www.w3.org/2000/01/rdf-schema#Resource"})
@comment({"The class of RDF containers."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#Container")
public interface Container extends Resource {
}
