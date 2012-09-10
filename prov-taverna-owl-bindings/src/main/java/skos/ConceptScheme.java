package skos;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;

@label({"Concept Scheme"})
@definition({"A set of concepts, optionally including statements about semantic relationships between those concepts."})
@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
@example({"Thesauri, classification schemes, subject heading lists, taxonomies, 'folksonomies', and other types of controlled vocabulary are all examples of concept schemes. Concept schemes are also embedded in glossaries and terminologies."})
@scopeNote({"A concept scheme may be defined to include concepts from different sources."})
@Iri("http://www.w3.org/2004/02/skos/core#ConceptScheme")
public interface ConceptScheme {
	@label({"has top concept"})
	@definition({"Relates, by convention, a concept scheme to a concept which is topmost in the broader/narrower concept hierarchies for that scheme, providing an entry point to these hierarchies."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#hasTopConcept")
	Set<Concept> getSkosHasTopConcepts();
	@label({"has top concept"})
	@definition({"Relates, by convention, a concept scheme to a concept which is topmost in the broader/narrower concept hierarchies for that scheme, providing an entry point to these hierarchies."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#hasTopConcept")
	void setSkosHasTopConcepts(Set<? extends Concept> skosHasTopConcepts);

}
