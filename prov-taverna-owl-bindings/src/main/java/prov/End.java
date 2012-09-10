package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:End provides additional descriptions about the binary prov:wasEndedBy relation from some ended prov:Activity to an prov:Entity that ended it. For example, :ball_game prov:wasEndedBy :buzzer; prov:qualifiedEnd [ a prov:End; prov:entity :buzzer; :foo :bar; prov:atTime '2012-03-09T08:05:08-05:00'^^xsd:dateTime ]. */
@category({"qualified"})
@label({"End"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-End"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-End"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage", "http://www.w3.org/ns/prov#DelegationOrDerivationOrEndOrStart", "http://www.w3.org/ns/prov#EntityInfluence", "http://www.w3.org/ns/prov#InstantaneousEvent"})
@comment({"An instance of prov:End provides additional descriptions about the binary prov:wasEndedBy relation from some ended prov:Activity to an prov:Entity that ended it. For example, :ball_game prov:wasEndedBy :buzzer; prov:qualifiedEnd [ a prov:End; prov:entity :buzzer; :foo :bar; prov:atTime '2012-03-09T08:05:08-05:00'^^xsd:dateTime ]."})
@definition({"End is when an activity is deemed to have ended. The activity no longer exists after its end. Any usage, generation, or invalidation involving an activity precedes the activity's end. An end may refer to an entity, known as trigger, that terminated the activity, or to an activity, known as ender that generated the trigger."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasEndedBy"})
@Iri("http://www.w3.org/ns/prov#End")
public interface End extends AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage, DelegationOrDerivationOrEndOrStart, EntityInfluence, InstantaneousEvent {
}
