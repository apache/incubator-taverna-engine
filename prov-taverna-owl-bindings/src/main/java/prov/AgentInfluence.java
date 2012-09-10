package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

/** 
 * AgentInfluence is intended to be a general subclass of Influence of an Agenty. It is a superclass for more specific kinds of Influences (e.g. Association, Attribution, Delegation).
 * It is not recommended that the type AgentInfluence be asserted without also asserting one of its more specific subclasses.
 * @see prov.AgentInfluence#getProvAgents
 */
@editorsDefinition({"AgentInfluence provides additional descriptions of an Agent's binary influence upon any other kind of resource. Instances of AgentInfluence use the prov:agent property to cite the influencing Agent."})
@category({"qualified"})
@label({"AgentInfluence"})
@seeAlso({"http://www.w3.org/ns/prov#agent"})
@subClassOf({"http://www.w3.org/ns/prov#Influence"})
@comment({"AgentInfluence is intended to be a general subclass of Influence of an Agenty. It is a superclass for more specific kinds of Influences (e.g. Association, Attribution, Delegation).", "It is not recommended that the type AgentInfluence be asserted without also asserting one of its more specific subclasses."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#AgentInfluence")
public interface AgentInfluence extends Influence {
	/** The property used by a prov:AgentInfluence to cite the Agent that influenced an Entity, Activity, or Agent. It can be used to express the agent involved in being responsible for an activity, being attributed to an entity, starting or ending an activity, or being responsible for another subordinate agent in an activity. */
	@category({"qualified"})
	@label({"agent"})
	@component({"alternate"})
	@comment({"The property used by a prov:AgentInfluence to cite the Agent that influenced an Entity, Activity, or Agent. It can be used to express the agent involved in being responsible for an activity, being attributed to an entity, starting or ending an activity, or being responsible for another subordinate agent in an activity."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#AgentInfluence"})
	@inverse({"agentOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#agent")
	Set<Agent> getProvAgents_1();
	/** The property used by a prov:AgentInfluence to cite the Agent that influenced an Entity, Activity, or Agent. It can be used to express the agent involved in being responsible for an activity, being attributed to an entity, starting or ending an activity, or being responsible for another subordinate agent in an activity. */
	@category({"qualified"})
	@label({"agent"})
	@component({"alternate"})
	@comment({"The property used by a prov:AgentInfluence to cite the Agent that influenced an Entity, Activity, or Agent. It can be used to express the agent involved in being responsible for an activity, being attributed to an entity, starting or ending an activity, or being responsible for another subordinate agent in an activity."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#AgentInfluence"})
	@inverse({"agentOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#agent")
	void setProvAgents_1(Set<? extends Agent> provAgents_1);

}
