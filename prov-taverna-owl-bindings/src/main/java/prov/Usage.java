package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Usage provides additional descriptions about the binary prov:used relation from some prov:Activity to an prov:Entity that it used. For example, :keynote prov:used :podium; prov:qualifiedUsage [ a prov:Usage; prov:entity :podium; :foo :bar ]. */
@category({"qualified"})
@label({"Usage"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Usage"})
@component({"entities-activities"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Usage"})
@constraints({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-constraints.html#prov-dm-constraints-fig"})
@subClassOf({"http://www.w3.org/ns/prov#AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage", "http://www.w3.org/ns/prov#EntityInfluence", "http://www.w3.org/ns/prov#InstantaneousEvent"})
@comment({"An instance of prov:Usage provides additional descriptions about the binary prov:used relation from some prov:Activity to an prov:Entity that it used. For example, :keynote prov:used :podium; prov:qualifiedUsage [ a prov:Usage; prov:entity :podium; :foo :bar ]."})
@definition({"Usage is the beginning of utilizing an entity by an activity. Before usage, the activity had not begun to utilize this entity and could not have been affected by the entity."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#used"})
@Iri("http://www.w3.org/ns/prov#Usage")
public interface Usage extends AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage, EntityInfluence, InstantaneousEvent {
}
