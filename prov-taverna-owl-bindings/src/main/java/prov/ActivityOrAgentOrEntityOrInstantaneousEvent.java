package prov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.unionOf;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;

@unionOf({"http://www.w3.org/ns/prov#Activity", "http://www.w3.org/ns/prov#Agent", "http://www.w3.org/ns/prov#Entity", "http://www.w3.org/ns/prov#InstantaneousEvent"})
public interface ActivityOrAgentOrEntityOrInstantaneousEvent {
	/** The Location of any resource. */
	@category({"expanded"})
	@label({"atLocation"})
	@comment({"The Location of any resource."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Location"})
	@inverse({"locationOf"})
	@editorialNote({"The naming of prov:atLocation parallels prov:atTime, and is not named prov:hadLocation to avoid conflicting with the convention that prov:had* properties are used on prov:Influence classes.", "This property is not functional because the many values could be at a variety of granularies (In this building, in this room, in that chair)."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#atLocation")
	Set<Location> getProvAtLocations();
	/** The Location of any resource. */
	@category({"expanded"})
	@label({"atLocation"})
	@comment({"The Location of any resource."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Location"})
	@inverse({"locationOf"})
	@editorialNote({"The naming of prov:atLocation parallels prov:atTime, and is not named prov:hadLocation to avoid conflicting with the convention that prov:had* properties are used on prov:Influence classes.", "This property is not functional because the many values could be at a variety of granularies (In this building, in this room, in that chair)."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#atLocation")
	void setProvAtLocations(Set<? extends Location> provAtLocations);

}
