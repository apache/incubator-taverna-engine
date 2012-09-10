package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@category({"expanded"})
@label({"EmptyCollection"})
@component({"collections"})
@subClassOf({"http://www.w3.org/ns/prov#Collection"})
@definition({"An empty collection is a collection without members."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#EmptyCollection")
public interface EmptyCollection extends Collection {
}
