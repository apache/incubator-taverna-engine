package prov;

import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instantaneous event, or event for short, happens in the world and marks a change in the world, in its activities and in its entities. The term 'event' is commonly used in process algebra with a similar meaning. Events represent communications or interactions; they are assumed to be atomic and instantaneous. */
@category({"qualified"})
@label({"InstantaneousEvent"})
@component({"entities-activities"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#dfn-event"})
@subClassOf({"http://www.w3.org/ns/prov#ActivityOrAgentOrEntityOrInstantaneousEvent"})
@comment({"An instantaneous event, or event for short, happens in the world and marks a change in the world, in its activities and in its entities. The term 'event' is commonly used in process algebra with a similar meaning. Events represent communications or interactions; they are assumed to be atomic and instantaneous."})
@definition({"The PROV data model is implicitly based on a notion of instantaneous events (or just events), that mark transitions in the world. Events include generation, usage, or invalidation of entities, as well as starting or ending of activities. This notion of event is not first-class in the data model, but it is useful for explaining its other concepts and its semantics."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#InstantaneousEvent")
public interface InstantaneousEvent extends ActivityOrAgentOrEntityOrInstantaneousEvent {
	/** The time at which an InstantaneousEvent occurred, in the form of xsd:dateTime. */
	@category({"qualified"})
	@label({"atTime"})
	@component({"entities-activities"})
	@comment({"The time at which an InstantaneousEvent occurred, in the form of xsd:dateTime."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#InstantaneousEvent"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#endedAtTime", "http://www.w3.org/ns/prov#generatedAtTime", "http://www.w3.org/ns/prov#invalidatedAtTime", "http://www.w3.org/ns/prov#startedAtTime"})
	@Iri("http://www.w3.org/ns/prov#atTime")
	Set<XMLGregorianCalendar> getProvAtTime();
	/** The time at which an InstantaneousEvent occurred, in the form of xsd:dateTime. */
	@category({"qualified"})
	@label({"atTime"})
	@component({"entities-activities"})
	@comment({"The time at which an InstantaneousEvent occurred, in the form of xsd:dateTime."})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#InstantaneousEvent"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@unqualifiedForm({"http://www.w3.org/ns/prov#endedAtTime", "http://www.w3.org/ns/prov#generatedAtTime", "http://www.w3.org/ns/prov#invalidatedAtTime", "http://www.w3.org/ns/prov#startedAtTime"})
	@Iri("http://www.w3.org/ns/prov#atTime")
	void setProvAtTime(Set<? extends XMLGregorianCalendar> provAtTime);

}
