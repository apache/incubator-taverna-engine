package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;

/** 
 * 
 * @see prov.AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage#getProvHadRoles
 * @see prov.Influence#getProvHadRoles_1
 */
@category({"qualified"})
@label({"Role"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-attribute-role"})
@component({"agents-responsibility"})
@seeAlso({"http://www.w3.org/ns/prov#hadRole"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-attribute"})
@definition({"A role is the function of an entity or agent with respect to an activity, in the context of a usage, generation, invalidation, association, start, and end."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Role")
public interface Role {
}
