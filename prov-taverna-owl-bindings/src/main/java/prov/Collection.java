package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@category({"expanded"})
@label({"Collection"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-collection"})
@component({"collections"})
@subClassOf({"http://www.w3.org/ns/prov#Entity"})
@definition({"A collection is an entity that provides a structure to some constituents, which are themselves entities. These constituents are said to be member of the collections."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Collection")
public interface Collection extends Entity {
	@category({"expanded"})
	@label({"hadMember"})
	@component({"expanded"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Collection"})
	@inverse({"wasMemberOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadMember")
	Set<Entity> getProvHadMembers();
	@category({"expanded"})
	@label({"hadMember"})
	@component({"expanded"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Collection"})
	@inverse({"wasMemberOf"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#hadMember")
	void setProvHadMembers(Set<? extends Entity> provHadMembers);

}
