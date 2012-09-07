package prov;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;

/** An instance of prov:Influence provides additional descriptions about the binary prov:wasInfluencedBy relation from some influenced Activity, Entity, or Agent to the influencing Activity, Entity, or Agent. For example, :stomach_ache prov:wasInfluencedBy :spoon; prov:qualifiedInfluence [ a prov:Influence; prov:entity :spoon; :foo :bar ] . Because prov:Influence is a broad relation, the more specific relations (Communication, Delegation, End, etc.) should be used when applicable. */
@category({"qualified"})
@label({"Influence"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-influence"})
@component({"derivations"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-influence"})
@comment({"An instance of prov:Influence provides additional descriptions about the binary prov:wasInfluencedBy relation from some influenced Activity, Entity, or Agent to the influencing Activity, Entity, or Agent. For example, :stomach_ache prov:wasInfluencedBy :spoon; prov:qualifiedInfluence [ a prov:Influence; prov:entity :spoon; :foo :bar ] . Because prov:Influence is a broad relation, the more specific relations (Communication, Delegation, End, etc.) should be used when applicable."})
@definition({"Influence is the capacity an entity, activity, or agent to have an effect on the character, development, or behavior of another by means of usage, start, end, generation, invalidation, communication, derivation, attribution, association, or delegation. "})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasInfluencedBy"})
@Iri("http://www.w3.org/ns/prov#Influence")
public interface Influence {
	/** The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead). */
	@category({"qualified"})
	@label({"hadActivity"})
	@component({"derivations"})
	@comment({"The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead)."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Activity"})
	@inverse({"wasActivityOfInfluence"})
	@editorialNote({"The multiple rdfs:domain assertions are intended. One is simpler and works for OWL-RL, the union is more specific but is not recognized by OWL-RL."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadActivity")
	Set<Activity> getProvHadActivities();
	/** The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead). */
	@category({"qualified"})
	@label({"hadActivity"})
	@component({"derivations"})
	@comment({"The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead)."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Activity"})
	@inverse({"wasActivityOfInfluence"})
	@editorialNote({"The multiple rdfs:domain assertions are intended. One is simpler and works for OWL-RL, the union is more specific but is not recognized by OWL-RL."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadActivity")
	void setProvHadActivities(Set<? extends Activity> provHadActivities);

	/** The _optional_ Role that an Entity assumed in the context of an Activity. For example, :baking prov:used :spoon; prov:qualified [ a prov:Usage; prov:entity :spoon; prov:hadRole roles:mixing_implement ]. */
	@category({"qualified"})
	@label({"hadRole"})
	@component({"agents-responsibility"})
	@comment({"The _optional_ Role that an Entity assumed in the context of an Activity. For example, :baking prov:used :spoon; prov:qualified [ a prov:Usage; prov:entity :spoon; prov:hadRole roles:mixing_implement ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Role"})
	@inverse({"wasRoleIn"})
	@definition({"prov:hadRole references the Role (i.e. the function of an entity with respect to an activity), in the context of a usage, generation, association, start, and end."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadRole")
	Set<Role> getProvHadRoles();
	/** The _optional_ Role that an Entity assumed in the context of an Activity. For example, :baking prov:used :spoon; prov:qualified [ a prov:Usage; prov:entity :spoon; prov:hadRole roles:mixing_implement ]. */
	@category({"qualified"})
	@label({"hadRole"})
	@component({"agents-responsibility"})
	@comment({"The _optional_ Role that an Entity assumed in the context of an Activity. For example, :baking prov:used :spoon; prov:qualified [ a prov:Usage; prov:entity :spoon; prov:hadRole roles:mixing_implement ]."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Role"})
	@inverse({"wasRoleIn"})
	@definition({"prov:hadRole references the Role (i.e. the function of an entity with respect to an activity), in the context of a usage, generation, association, start, and end."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadRole")
	void setProvHadRoles(Set<? extends Role> provHadRoles);

	/** Subproperties of prov:influencer are used to cite the object of an unqualified PROV-O triple whose predicate is a subproperty of prov:wasInfluencedBy (e.g. prov:used, prov:wasGeneratedBy). prov:influencer is used much like rdf:object is used. */
	@editorsDefinition({"This property is used as part of the qualified influence pattern. Subclasses of prov:Influence use these subproperties to reference the resource (Entity, Agent, or Activity) whose influence is being qualified."})
	@category({"qualified"})
	@label({"influencer"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-influence"})
	@comment({"Subproperties of prov:influencer are used to cite the object of an unqualified PROV-O triple whose predicate is a subproperty of prov:wasInfluencedBy (e.g. prov:used, prov:wasGeneratedBy). prov:influencer is used much like rdf:object is used."})
	@inverse({"hadInfluence"})
	@editorialNote({"This property and its subproperties are used in the same way as the rdf:object property, i.e. to reference the object of an unqualified prov:wasInfluencedBy or prov:influenced triple."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#influencer")
	Set<Object> getProvInfluencers();
	/** Subproperties of prov:influencer are used to cite the object of an unqualified PROV-O triple whose predicate is a subproperty of prov:wasInfluencedBy (e.g. prov:used, prov:wasGeneratedBy). prov:influencer is used much like rdf:object is used. */
	@editorsDefinition({"This property is used as part of the qualified influence pattern. Subclasses of prov:Influence use these subproperties to reference the resource (Entity, Agent, or Activity) whose influence is being qualified."})
	@category({"qualified"})
	@label({"influencer"})
	@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-influence"})
	@comment({"Subproperties of prov:influencer are used to cite the object of an unqualified PROV-O triple whose predicate is a subproperty of prov:wasInfluencedBy (e.g. prov:used, prov:wasGeneratedBy). prov:influencer is used much like rdf:object is used."})
	@inverse({"hadInfluence"})
	@editorialNote({"This property and its subproperties are used in the same way as the rdf:object property, i.e. to reference the object of an unqualified prov:wasInfluencedBy or prov:influenced triple."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#influencer")
	void setProvInfluencers(Set<?> provInfluencers);

}
