package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Start provides additional descriptions about the binary prov:wasStartedBy relation from some started prov:Activity to an prov:Entity that started it. For example, :foot_race prov:wasStartedBy :bang; prov:qualifiedStart [ a prov:Start; prov:entity :bang; :foo :bar; prov:atTime '2012-03-09T08:05:08-05:00'^^xsd:dateTime ] . */
@category({"qualified"})
@label({"Start"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Start"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Start"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage", "http://www.w3.org/ns/prov#DelegationOrDerivationOrEndOrStart", "http://www.w3.org/ns/prov#EntityInfluence", "http://www.w3.org/ns/prov#InstantaneousEvent"})
@comment({"An instance of prov:Start provides additional descriptions about the binary prov:wasStartedBy relation from some started prov:Activity to an prov:Entity that started it. For example, :foot_race prov:wasStartedBy :bang; prov:qualifiedStart [ a prov:Start; prov:entity :bang; :foo :bar; prov:atTime '2012-03-09T08:05:08-05:00'^^xsd:dateTime ] ."})
@definition({"Start is when an activity is deemed to have started. The activity did not exist before its start. Any usage or generation involving an activity follows the activity's start. A start may refer to an entity, known as trigger, that set off the activity, or to an activity, known as starter, that generated the trigger."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#wasStartedBy"})
@Iri("http://www.w3.org/ns/prov#Start")
public interface Start extends AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage, DelegationOrDerivationOrEndOrStart, EntityInfluence, InstantaneousEvent {
}
