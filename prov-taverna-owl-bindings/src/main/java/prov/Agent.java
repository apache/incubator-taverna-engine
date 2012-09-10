package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@category({"starting-point"})
@label({"Agent"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-agent"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Agent"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityOrAgentOrEntity", "http://www.w3.org/ns/prov#ActivityOrAgentOrEntityOrInstantaneousEvent"})
@definition({"An agent is something that bears some form of responsibility for an activity taking place, for the existence of an entity, or for another agent's activity. "})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Agent")
public interface Agent extends ActivityOrAgentOrEntity, ActivityOrAgentOrEntityOrInstantaneousEvent {
	/** An object property to express the accountability of an agent towards another agent. The subordinate agent acted on behalf of the responsible agent in an actual activity. */
	@category({"starting-point"})
	@label({"actedOnBehalfOf"})
	@component({"agents-responsibility"})
	@comment({"An object property to express the accountability of an agent towards another agent. The subordinate agent acted on behalf of the responsible agent in an actual activity. "})
	@inverse({"hadDelegate"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Delegation", "http://www.w3.org/ns/prov#qualifiedDelegation"})
	@Iri("http://www.w3.org/ns/prov#actedOnBehalfOf")
	Set<Agent> getProvActedOnBehalfOf();
	/** An object property to express the accountability of an agent towards another agent. The subordinate agent acted on behalf of the responsible agent in an actual activity. */
	@category({"starting-point"})
	@label({"actedOnBehalfOf"})
	@component({"agents-responsibility"})
	@comment({"An object property to express the accountability of an agent towards another agent. The subordinate agent acted on behalf of the responsible agent in an actual activity. "})
	@inverse({"hadDelegate"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@qualifiedForm({"http://www.w3.org/ns/prov#Delegation", "http://www.w3.org/ns/prov#qualifiedDelegation"})
	@Iri("http://www.w3.org/ns/prov#actedOnBehalfOf")
	void setProvActedOnBehalfOf(Set<? extends Agent> provActedOnBehalfOf);

	/** If this Agent prov:actedOnBehalfOf Agent :ag, then it can qualify how with prov:qualifiedResponsibility [ a prov:Responsibility;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedDelegation"})
	@component({"agents-responsibility"})
	@comment({"If this Agent prov:actedOnBehalfOf Agent :ag, then it can qualify how with prov:qualifiedResponsibility [ a prov:Responsibility;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Delegation"})
	@inverse({"qualifiedDelegationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#actedOnBehalfOf"})
	@Iri("http://www.w3.org/ns/prov#qualifiedDelegation")
	Set<Delegation> getProvQualifiedDelegations();
	/** If this Agent prov:actedOnBehalfOf Agent :ag, then it can qualify how with prov:qualifiedResponsibility [ a prov:Responsibility;  prov:agent :ag; :foo :bar ]. */
	@category({"qualified"})
	@label({"qualifiedDelegation"})
	@component({"agents-responsibility"})
	@comment({"If this Agent prov:actedOnBehalfOf Agent :ag, then it can qualify how with prov:qualifiedResponsibility [ a prov:Responsibility;  prov:agent :ag; :foo :bar ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Delegation"})
	@inverse({"qualifiedDelegationOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#qualifiedInfluence"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#actedOnBehalfOf"})
	@Iri("http://www.w3.org/ns/prov#qualifiedDelegation")
	void setProvQualifiedDelegations(Set<? extends Delegation> provQualifiedDelegations);

}
