package skos;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@label({"Collection"})
@definition({"A meaningful collection of concepts."})
@subClassOf({"http://www.w3.org/2004/02/skos/core#CollectionOrConcept"})
@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
@scopeNote({"Labelled collections can be used where you would like a set of concepts to be displayed under a 'node label' in the hierarchy."})
@Iri("http://www.w3.org/2004/02/skos/core#Collection")
public interface Collection extends CollectionOrConcept {
	@label({"has member"})
	@definition({"Relates a collection to one of its members."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#member")
	Set<CollectionOrConcept> getSkosMembers();
	@label({"has member"})
	@definition({"Relates a collection to one of its members."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#member")
	void setSkosMembers(Set<? extends CollectionOrConcept> skosMembers);

}
