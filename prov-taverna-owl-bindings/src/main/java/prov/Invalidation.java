package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Invalidation provides additional descriptions about the binary prov:wasInvalidatedBy relation from an invalidated prov:Entity to the prov:Activity that invalidated it. For example, :uncracked_egg prov:wasInvalidatedBy :baking; prov:qualifiedInvalidation [ a prov:Invalidation; prov:activity :baking; :foo :bar ]. */
@category({"qualified"})
@label({"Invalidation"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Invalidation"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Invalidation"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityInfluence", "http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage", "http://www.w3.org/ns/prov#InstantaneousEvent"})
@comment({"An instance of prov:Invalidation provides additional descriptions about the binary prov:wasInvalidatedBy relation from an invalidated prov:Entity to the prov:Activity that invalidated it. For example, :uncracked_egg prov:wasInvalidatedBy :baking; prov:qualifiedInvalidation [ a prov:Invalidation; prov:activity :baking; :foo :bar ]."})
@definition({"Invalidation is the start of the destruction, cessation, or expiry of an existing entity by an activity. The entity is no longer available for use (or further invalidation) after invalidation. Any generation or usage of an entity precedes its invalidation."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasInvalidatedBy"})
@Iri("http://www.w3.org/ns/prov#Invalidation")
public interface Invalidation extends ActivityInfluence, AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage, InstantaneousEvent {
}
