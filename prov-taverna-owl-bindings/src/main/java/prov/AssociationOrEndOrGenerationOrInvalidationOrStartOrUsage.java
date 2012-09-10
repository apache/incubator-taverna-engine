package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@subClassOf({"http://www.w3.org/ns/prov#Influence"})
@unionOf({"http://www.w3.org/ns/prov#Association", "http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#Generation", "http://www.w3.org/ns/prov#Invalidation", "http://www.w3.org/ns/prov#Start", "http://www.w3.org/ns/prov#Usage"})
public interface AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage extends Influence {
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

}
