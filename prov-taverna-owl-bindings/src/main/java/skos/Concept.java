package skos;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.Thing;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import vs.term_status;

@label({"Concept", "Concept"})
@definition({"An idea or notion; a unit of thought."})
@subClassOf({"http://www.w3.org/2004/02/skos/core#CollectionOrConcept"})
@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
@Iri("http://www.w3.org/2004/02/skos/core#Concept")
public interface Concept extends CollectionOrConcept {
	@label({"has broader transitive"})
	@definition({"skos:broaderTransitive is a transitive superproperty of skos:broader."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broaderTransitive is not used to make assertions. Rather, the properties can be used to draw inferences about the transitive closure of the hierarchical relation, which is useful e.g. when implementing a simple query expansion algorithm in a search application."})
	@Iri("http://www.w3.org/2004/02/skos/core#broaderTransitive")
	Set<Concept> getSkosBroaderTransitive();
	@label({"has broader transitive"})
	@definition({"skos:broaderTransitive is a transitive superproperty of skos:broader."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broaderTransitive is not used to make assertions. Rather, the properties can be used to draw inferences about the transitive closure of the hierarchical relation, which is useful e.g. when implementing a simple query expansion algorithm in a search application."})
	@Iri("http://www.w3.org/2004/02/skos/core#broaderTransitive")
	void setSkosBroaderTransitive(Set<? extends Concept> skosBroaderTransitive);

	/** These concept mapping relations mirror semantic relations, and the data model defined below is similar (with the exception of skos:exactMatch) to the data model defined for semantic relations. A distinct vocabulary is provided for concept mapping relations, to provide a convenient way to differentiate links within a concept scheme from links between concept schemes. However, this pattern of usage is not a formal requirement of the SKOS data model, and relies on informal definitions of best practice. */
	@label({"is in mapping relation with"})
	@definition({"Relates two concepts coming, by convention, from different schemes, and that have comparable meanings"})
	@comment({"These concept mapping relations mirror semantic relations, and the data model defined below is similar (with the exception of skos:exactMatch) to the data model defined for semantic relations. A distinct vocabulary is provided for concept mapping relations, to provide a convenient way to differentiate links within a concept scheme from links between concept schemes. However, this pattern of usage is not a formal requirement of the SKOS data model, and relies on informal definitions of best practice."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#mappingRelation")
	Set<Concept> getSkosMappingRelations();
	/** These concept mapping relations mirror semantic relations, and the data model defined below is similar (with the exception of skos:exactMatch) to the data model defined for semantic relations. A distinct vocabulary is provided for concept mapping relations, to provide a convenient way to differentiate links within a concept scheme from links between concept schemes. However, this pattern of usage is not a formal requirement of the SKOS data model, and relies on informal definitions of best practice. */
	@label({"is in mapping relation with"})
	@definition({"Relates two concepts coming, by convention, from different schemes, and that have comparable meanings"})
	@comment({"These concept mapping relations mirror semantic relations, and the data model defined below is similar (with the exception of skos:exactMatch) to the data model defined for semantic relations. A distinct vocabulary is provided for concept mapping relations, to provide a convenient way to differentiate links within a concept scheme from links between concept schemes. However, this pattern of usage is not a formal requirement of the SKOS data model, and relies on informal definitions of best practice."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#mappingRelation")
	void setSkosMappingRelations(Set<? extends Concept> skosMappingRelations);

	@label({"has narrower match"})
	@definition({"skos:narrowMatch is used to state a hierarchical mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation", "http://www.w3.org/2004/02/skos/core#narrower"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#narrowMatch")
	Set<Concept> getSkosNarrowMatch();
	@label({"has narrower match"})
	@definition({"skos:narrowMatch is used to state a hierarchical mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation", "http://www.w3.org/2004/02/skos/core#narrower"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#narrowMatch")
	void setSkosNarrowMatch(Set<? extends Concept> skosNarrowMatch);

	@label({"has narrower transitive"})
	@definition({"skos:narrowerTransitive is a transitive superproperty of skos:narrower."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:narrowerTransitive is not used to make assertions. Rather, the properties can be used to draw inferences about the transitive closure of the hierarchical relation, which is useful e.g. when implementing a simple query expansion algorithm in a search application."})
	@Iri("http://www.w3.org/2004/02/skos/core#narrowerTransitive")
	Set<Concept> getSkosNarrowerTransitive();
	@label({"has narrower transitive"})
	@definition({"skos:narrowerTransitive is a transitive superproperty of skos:narrower."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:narrowerTransitive is not used to make assertions. Rather, the properties can be used to draw inferences about the transitive closure of the hierarchical relation, which is useful e.g. when implementing a simple query expansion algorithm in a search application."})
	@Iri("http://www.w3.org/2004/02/skos/core#narrowerTransitive")
	void setSkosNarrowerTransitive(Set<? extends Concept> skosNarrowerTransitive);

	/** skos:related is disjoint with skos:broaderTransitive */
	@label({"has related"})
	@definition({"Relates a concept to a concept with which there is an associative semantic relationship."})
	@comment({"skos:related is disjoint with skos:broaderTransitive"})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#related")
	Set<Concept> getSkosRelated();
	/** skos:related is disjoint with skos:broaderTransitive */
	@label({"has related"})
	@definition({"Relates a concept to a concept with which there is an associative semantic relationship."})
	@comment({"skos:related is disjoint with skos:broaderTransitive"})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#semanticRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#related")
	void setSkosRelated(Set<? extends Concept> skosRelated);

	@label({"has related match"})
	@definition({"skos:relatedMatch is used to state an associative mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation", "http://www.w3.org/2004/02/skos/core#related"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#relatedMatch")
	Set<Concept> getSkosRelatedMatch();
	@label({"has related match"})
	@definition({"skos:relatedMatch is used to state an associative mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation", "http://www.w3.org/2004/02/skos/core#related"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#relatedMatch")
	void setSkosRelatedMatch(Set<? extends Concept> skosRelatedMatch);

	@label({"is in semantic relation with"})
	@definition({"Links a concept to a concept related by meaning."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"This property should not be used directly, but as a super-property for all properties denoting a relationship of meaning between concepts."})
	@Iri("http://www.w3.org/2004/02/skos/core#semanticRelation")
	Set<Concept> getSkosSemanticRelations();
	@label({"is in semantic relation with"})
	@definition({"Links a concept to a concept related by meaning."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"This property should not be used directly, but as a super-property for all properties denoting a relationship of meaning between concepts."})
	@Iri("http://www.w3.org/2004/02/skos/core#semanticRelation")
	void setSkosSemanticRelations(Set<? extends Concept> skosSemanticRelations);

	@label({"is top concept in scheme"})
	@definition({"Relates a concept to the concept scheme that it is a top level concept of."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#inScheme"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#topConceptOf")
	Set<ConceptScheme> getSkosTopConceptOf();
	@label({"is top concept in scheme"})
	@definition({"Relates a concept to the concept scheme that it is a top level concept of."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#inScheme"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#topConceptOf")
	void setSkosTopConceptOf(Set<? extends ConceptScheme> skosTopConceptOf);

	/** The underlying or 'focal' entity associated with some SKOS-described concept. */
	@label({"focus"})
	@term_status({"testing"})
	@comment({"The underlying or 'focal' entity associated with some SKOS-described concept."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/focus")
	Set<Thing> getFoafFocus();
	/** The underlying or 'focal' entity associated with some SKOS-described concept. */
	@label({"focus"})
	@term_status({"testing"})
	@comment({"The underlying or 'focal' entity associated with some SKOS-described concept."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/focus")
	void setFoafFocus(Set<? extends Thing> foafFocus);

}
