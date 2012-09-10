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
 * EntityInfluence is intended to be a general subclass of Influence of an Entity. It is a superclass for more specific kinds of Influences (e.g. Usage, Derivation, Source).
 * It is not recommended that the type EntityInfluence be asserted without also asserting one of its more specific subclasses.
 * @see prov.EntityInfluence#getProvEntities
 */
@editorsDefinition({"EntityInfluence provides additional descriptions of an Entity's binary influence upon any other kind of resource. Instances of EntityInfluence use the prov:entity property to cite the influencing Entity."})
@category({"qualified"})
@label({"EntityInfluence"})
@seeAlso({"http://www.w3.org/ns/prov#entity"})
@subClassOf({"http://www.w3.org/ns/prov#Influence"})
@comment({"EntityInfluence is intended to be a general subclass of Influence of an Entity. It is a superclass for more specific kinds of Influences (e.g. Usage, Derivation, Source).", "It is not recommended that the type EntityInfluence be asserted without also asserting one of its more specific subclasses."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#EntityInfluence")
public interface EntityInfluence extends Influence {
	/** The property used by an prov:EntityInfluence to cite the Entity that was influenced by an Entity, Activity, or Agent. It can be used to refer to the entity involved in deriving another entity, being quoted or revised from, being the source of another entity, or being used in an activity. */
	@category({"qualified"})
	@label({"entity"})
	@comment({"The property used by an prov:EntityInfluence to cite the Entity that was influenced by an Entity, Activity, or Agent. It can be used to refer to the entity involved in deriving another entity, being quoted or revised from, being the source of another entity, or being used in an activity. "})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#EntityInfluence"})
	@inverse({"entityOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#entity")
	Set<Entity> getProvEntities_1();
	/** The property used by an prov:EntityInfluence to cite the Entity that was influenced by an Entity, Activity, or Agent. It can be used to refer to the entity involved in deriving another entity, being quoted or revised from, being the source of another entity, or being used in an activity. */
	@category({"qualified"})
	@label({"entity"})
	@comment({"The property used by an prov:EntityInfluence to cite the Entity that was influenced by an Entity, Activity, or Agent. It can be used to refer to the entity involved in deriving another entity, being quoted or revised from, being the source of another entity, or being used in an activity. "})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#EntityInfluence"})
	@inverse({"entityOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#entity")
	void setProvEntities_1(Set<? extends Entity> provEntities_1);

}
