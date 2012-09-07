package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.maxCardinality;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

/** 
 * ActivityInfluence is intended to be a general subclass of Influence of an Activity. It is a superclass for more specific kinds of Influences (e.g. Generation, Communication, and Invalidation).
 * It is not recommended that the type ActivityInfluence be asserted without also asserting one of its more specific subclasses.
 * @see prov.ActivityInfluence#getProvActivities
 */
@editorsDefinition({"ActivityInfluence provides additional descriptions of an Activity's binary influence upon any other kind of resource. Instances of ActivityInfluence use the prov:activity property to cite the influencing Activity."})
@category({"qualified"})
@label({"ActivityInfluence"})
@seeAlso({"http://www.w3.org/ns/prov#activity"})
@subClassOf({"http://www.w3.org/ns/prov#Influence"})
@comment({"ActivityInfluence is intended to be a general subclass of Influence of an Activity. It is a superclass for more specific kinds of Influences (e.g. Generation, Communication, and Invalidation).", "It is not recommended that the type ActivityInfluence be asserted without also asserting one of its more specific subclasses."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#ActivityInfluence")
public interface ActivityInfluence extends Influence {
	/** The property used by an prov:ActivityInfluence to cite the prov:Activity that influenced an Entity, Activity, or Agent. It can be used to refer to the activity involved in generating an entity, informing another activity, or starting another activity. */
	@category({"qualified"})
	@label({"activity"})
	@comment({"The property used by an prov:ActivityInfluence to cite the prov:Activity that influenced an Entity, Activity, or Agent. It can be used to refer to the activity involved in generating an entity, informing another activity, or starting another activity."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#ActivityInfluence"})
	@inverse({"activityOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#activity")
	Set<Activity> getProvActivities_1();
	/** The property used by an prov:ActivityInfluence to cite the prov:Activity that influenced an Entity, Activity, or Agent. It can be used to refer to the activity involved in generating an entity, informing another activity, or starting another activity. */
	@category({"qualified"})
	@label({"activity"})
	@comment({"The property used by an prov:ActivityInfluence to cite the prov:Activity that influenced an Entity, Activity, or Agent. It can be used to refer to the activity involved in generating an entity, informing another activity, or starting another activity."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#ActivityInfluence"})
	@inverse({"activityOfInfluence"})
	@editorialNote({"This property behaves in spirit like rdf:object; it references the object of a prov:wasInfluencedBy triple."})
	@subPropertyOf({"http://www.w3.org/ns/prov#influencer"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#activity")
	void setProvActivities_1(Set<? extends Activity> provActivities_1);

	/** The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead). */
	@category({"qualified"})
	@label({"hadActivity"})
	@component({"derivations"})
	@comment({"The _optional_ Activity of an Influence, which used, generated, invalidated, or was the responsibility of some Entity. This property is _not_ used by ActivityInfluence (use prov:activity instead)."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Activity"})
	@inverse({"wasActivityOfInfluence"})
	@editorialNote({"The multiple rdfs:domain assertions are intended. One is simpler and works for OWL-RL, the union is more specific but is not recognized by OWL-RL."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@maxCardinality("0")
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

}
