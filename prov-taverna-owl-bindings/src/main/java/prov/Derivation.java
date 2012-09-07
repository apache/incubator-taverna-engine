package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Derivation provides additional descriptions about the binary prov:wasDerivedFrom relation from some derived prov:Entity to another prov:Entity from which it was derived. For example, :chewed_bubble_gum prov:wasDerivedFrom :unwrapped_bubble_gum; prov:qualifiedDerivation [ a prov:Derivation; prov:entity :unwrapped_bubble_gum; :foo :bar ]. */
@category({"qualified"})
@label({"Derivation"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Derivation"})
@component({"derivations"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#Derivation-Relation"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#DelegationOrDerivationOrEndOrStart", "http://www.w3.org/ns/prov#EntityInfluence"})
@comment({"An instance of prov:Derivation provides additional descriptions about the binary prov:wasDerivedFrom relation from some derived prov:Entity to another prov:Entity from which it was derived. For example, :chewed_bubble_gum prov:wasDerivedFrom :unwrapped_bubble_gum; prov:qualifiedDerivation [ a prov:Derivation; prov:entity :unwrapped_bubble_gum; :foo :bar ]."})
@definition({"A derivation is a transformation of an entity into another, an update of an entity resulting in a new one, or the construction of a new entity based on a pre-existing entity."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasDerivedFrom"})
@Iri("http://www.w3.org/ns/prov#Derivation")
public interface Derivation extends DelegationOrDerivationOrEndOrStart, EntityInfluence {
	/** The _optional_ Generation involved in an Entity's Derivation. */
	@category({"qualified"})
	@label({"hadGeneration"})
	@component({"derivations"})
	@comment({"The _optional_ Generation involved in an Entity's Derivation."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"generatedAsDerivation"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadGeneration")
	Set<Generation> getProvHadGenerations();
	/** The _optional_ Generation involved in an Entity's Derivation. */
	@category({"qualified"})
	@label({"hadGeneration"})
	@component({"derivations"})
	@comment({"The _optional_ Generation involved in an Entity's Derivation."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Generation"})
	@inverse({"generatedAsDerivation"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadGeneration")
	void setProvHadGenerations(Set<? extends Generation> provHadGenerations);

	/** The _optional_ Usage involved in an Entity's Derivation. */
	@category({"qualified"})
	@label({"hadUsage"})
	@component({"derivations"})
	@comment({"The _optional_ Usage involved in an Entity's Derivation."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Usage"})
	@inverse({"wasUsedInDerivation"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadUsage")
	Set<Usage> getProvHadUsages();
	/** The _optional_ Usage involved in an Entity's Derivation. */
	@category({"qualified"})
	@label({"hadUsage"})
	@component({"derivations"})
	@comment({"The _optional_ Usage involved in an Entity's Derivation."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Usage"})
	@inverse({"wasUsedInDerivation"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadUsage")
	void setProvHadUsages(Set<? extends Usage> provHadUsages);

}
