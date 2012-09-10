package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** There exist no prescriptive requirement on the nature of plans, their representation, the actions or steps they consist of, or their intended goals. Since plans may evolve over time, it may become necessary to track their provenance, so plans themselves are entities. Representing the plan explicitly in the provenance can be useful for various tasks: for example, to validate the execution as represented in the provenance record, to manage expectation failures, or to provide explanations. */
@category({"expanded", "qualified"})
@label({"Plan"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-Association"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-Association"})
@subClassOf({"http://www.w3.org/ns/prov#Entity"})
@comment({"There exist no prescriptive requirement on the nature of plans, their representation, the actions or steps they consist of, or their intended goals. Since plans may evolve over time, it may become necessary to track their provenance, so plans themselves are entities. Representing the plan explicitly in the provenance can be useful for various tasks: for example, to validate the execution as represented in the provenance record, to manage expectation failures, or to provide explanations."})
@definition({"A plan is an entity that represents a set of actions or steps intended by one or more agents to achieve some goals."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Plan")
public interface Plan extends Entity {
}
