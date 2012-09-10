package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@subClassOf({"http://www.w3.org/ns/prov#Influence"})
@unionOf({"http://www.w3.org/ns/prov#Delegation", "http://www.w3.org/ns/prov#Derivation", "http://www.w3.org/ns/prov#End", "http://www.w3.org/ns/prov#Start"})
public interface DelegationOrDerivationOrEndOrStart extends Influence {
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

}
