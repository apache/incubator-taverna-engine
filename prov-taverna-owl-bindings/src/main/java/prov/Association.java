package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Association provides additional descriptions about the binary prov:wasAssociatedWith relation from an prov:Activity to some prov:Agent that had some responsiblity for it. For example, :baking prov:wasAssociatedWith :baker; prov:qualifiedAssociation [ a prov:Association; prov:agent :baker; :foo :bar ]. */
@category({"qualified"})
@label({"Association"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Association"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Association"})
@subClassOf({"http://www.w3.org/ns/prov#AgentInfluence", "http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage"})
@comment({"An instance of prov:Association provides additional descriptions about the binary prov:wasAssociatedWith relation from an prov:Activity to some prov:Agent that had some responsiblity for it. For example, :baking prov:wasAssociatedWith :baker; prov:qualifiedAssociation [ a prov:Association; prov:agent :baker; :foo :bar ]."})
@definition({"An activity association is an assignment of responsibility to an agent for an activity, indicating that the agent had a role in the activity. It further allows for a plan to be specified, which is the plan intended by the agent to achieve some goals in the context of this activity."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasAssociatedWith"})
@Iri("http://www.w3.org/ns/prov#Association")
public interface Association extends AgentInfluence, AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage {
	/** The _optional_ Plan adopted by an Agent in Association with some Activity. Plan specifications are out of the scope of this specification. */
	@category({"qualified"})
	@label({"hadPlan"})
	@component({"agents-responsibility"})
	@comment({"The _optional_ Plan adopted by an Agent in Association with some Activity. Plan specifications are out of the scope of this specification."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Plan"})
	@inverse({"wasPlanOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadPlan")
	Set<Plan> getProvHadPlans();
	/** The _optional_ Plan adopted by an Agent in Association with some Activity. Plan specifications are out of the scope of this specification. */
	@category({"qualified"})
	@label({"hadPlan"})
	@component({"agents-responsibility"})
	@comment({"The _optional_ Plan adopted by an Agent in Association with some Activity. Plan specifications are out of the scope of this specification."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Plan"})
	@inverse({"wasPlanOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadPlan")
	void setProvHadPlans(Set<? extends Plan> provHadPlans);

}
