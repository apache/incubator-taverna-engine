package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;

@unionOf({"http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity", "http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity", "http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity", "http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity", "http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity"})
public interface ActivityOrAgentOrEntity {
	/** Because prov:qualifiedInfluence is a broad relation, the more specific relations (qualifiedCommunication, qualifiedDelegation, qualifiedEnd, etc.) should be used when applicable. */
	@category({"qualified"})
	@label({"qualifiedInfluence"})
	@component({"derivations"})
	@comment({"Because prov:qualifiedInfluence is a broad relation, the more specific relations (qualifiedCommunication, qualifiedDelegation, qualifiedEnd, etc.) should be used when applicable."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"qualifiedInfluenceOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedInfluence")
	Set<Influence> getProvQualifiedInfluences();
	/** Because prov:qualifiedInfluence is a broad relation, the more specific relations (qualifiedCommunication, qualifiedDelegation, qualifiedEnd, etc.) should be used when applicable. */
	@category({"qualified"})
	@label({"qualifiedInfluence"})
	@component({"derivations"})
	@comment({"Because prov:qualifiedInfluence is a broad relation, the more specific relations (qualifiedCommunication, qualifiedDelegation, qualifiedEnd, etc.) should be used when applicable."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"qualifiedInfluenceOf"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@Iri("http://www.w3.org/ns/prov#qualifiedInfluence")
	void setProvQualifiedInfluences(Set<? extends Influence> provQualifiedInfluences);

	/** Because prov:wasInfluencedBy is a broad relation, the more specific relations (prov:wasInformedBy, prov:actedOnBehalfOf, prov:endedBy, etc.) should be used when applicable. */
	@category({"qualified"})
	@label({"wasInfluencedBy"})
	@component({"agents-responsibility"})
	@comment({"Because prov:wasInfluencedBy is a broad relation, the more specific relations (prov:wasInformedBy, prov:actedOnBehalfOf, prov:endedBy, etc.) should be used when applicable."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"influenced"})
	@editorialNote({"The sub-properties of prov:wasInfluencedBy can be elaborated in more detail using the Qualification Pattern. For example, the binary relation :baking prov:used :spoon can be qualified by asserting :baking prov:qualifiedUsage [ a prov:Usage; prov:entity :spoon; prov:atLocation :kitchen ] .\n" + 
		"\n" + 
		"Subproperties of prov:wasInfluencedBy may also be asserted directly without being qualified.\n" + 
		"\n" + 
		"prov:wasInfluencedBy should not be used without also using one of its subproperties. \n"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#wasInfluencedBy")
	Set<ActivityOrAgentOrEntity> getProvWasInfluencedBy();
	/** Because prov:wasInfluencedBy is a broad relation, the more specific relations (prov:wasInformedBy, prov:actedOnBehalfOf, prov:endedBy, etc.) should be used when applicable. */
	@category({"qualified"})
	@label({"wasInfluencedBy"})
	@component({"agents-responsibility"})
	@comment({"Because prov:wasInfluencedBy is a broad relation, the more specific relations (prov:wasInformedBy, prov:actedOnBehalfOf, prov:endedBy, etc.) should be used when applicable."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"influenced"})
	@editorialNote({"The sub-properties of prov:wasInfluencedBy can be elaborated in more detail using the Qualification Pattern. For example, the binary relation :baking prov:used :spoon can be qualified by asserting :baking prov:qualifiedUsage [ a prov:Usage; prov:entity :spoon; prov:atLocation :kitchen ] .\n" + 
		"\n" + 
		"Subproperties of prov:wasInfluencedBy may also be asserted directly without being qualified.\n" + 
		"\n" + 
		"prov:wasInfluencedBy should not be used without also using one of its subproperties. \n"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#wasInfluencedBy")
	void setProvWasInfluencedBy(Set<? extends ActivityOrAgentOrEntity> provWasInfluencedBy);

}
