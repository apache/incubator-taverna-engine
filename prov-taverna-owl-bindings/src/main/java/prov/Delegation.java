package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Delegation provides additional descriptions about the binary prov:actedOnBehalfOf relation from a performing prov:Agent to some prov:Agent for whom it was performed. For example, :mixing prov:wasAssociatedWith :toddler . :toddler prov:actedOnBehalfOf :mother; prov:qualifiedDelegation [ a prov:Delegation; prov:entity :mother; :foo :bar ]. */
@category({"qualified"})
@label({"Delegation"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-delegation"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-delegation"})
@subClassOf({"http://www.w3.org/ns/prov#AgentInfluence", "http://www.w3.org/ns/prov#DelegationOrDerivationOrEndOrStart"})
@comment({"An instance of prov:Delegation provides additional descriptions about the binary prov:actedOnBehalfOf relation from a performing prov:Agent to some prov:Agent for whom it was performed. For example, :mixing prov:wasAssociatedWith :toddler . :toddler prov:actedOnBehalfOf :mother; prov:qualifiedDelegation [ a prov:Delegation; prov:entity :mother; :foo :bar ]."})
@definition({"Delegation is the assignment of authority and responsibility to an agent (by itself or by another agent) to carry out a specific activity as a delegate or representative, while the agent it acts on behalf of retains some responsibility for the outcome of the delegated work.\n" + 
	"\n" + 
	"For example, a student acted on behalf of his supervisor, who acted on behalf of the department chair, who acted on behalf of the university; all those agents are responsible in some way for the activity that took place but we do not say explicitly who bears responsibility and to what degree."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#actedOnBehalfOf"})
@Iri("http://www.w3.org/ns/prov#Delegation")
public interface Delegation extends AgentInfluence, DelegationOrDerivationOrEndOrStart {
}
