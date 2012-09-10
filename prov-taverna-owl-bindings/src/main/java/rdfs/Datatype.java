package rdfs;

import org.openrdf.annotations.Iri;

/** The class of RDF datatypes. */
@label({"Datatype"})
@subClassOf({"http://www.w3.org/2000/01/rdf-schema#Class"})
@comment({"The class of RDF datatypes."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#Datatype")
public interface Datatype extends Class {
}
