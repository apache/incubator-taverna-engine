package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Generation provides additional descriptions about the binary prov:wasGeneratedBy relation from a generated prov:Entity to the prov:Activity that generated it. For example, :cake prov:wasGeneratedBy :baking; prov:qualifiedGeneration [ a prov:Generation; prov:activity :baking; :foo :bar ]. */
@category({"qualified"})
@label({"Generation"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Generation"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Generation"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityInfluence", "http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage", "http://www.w3.org/ns/prov#InstantaneousEvent"})
@comment({"An instance of prov:Generation provides additional descriptions about the binary prov:wasGeneratedBy relation from a generated prov:Entity to the prov:Activity that generated it. For example, :cake prov:wasGeneratedBy :baking; prov:qualifiedGeneration [ a prov:Generation; prov:activity :baking; :foo :bar ]."})
@definition({"Generation is the completion of production of a new entity by an activity. This entity did not exist before generation and becomes available for usage after this generation."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasGeneratedBy"})
@Iri("http://www.w3.org/ns/prov#Generation")
public interface Generation extends ActivityInfluence, AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage, InstantaneousEvent {
}
