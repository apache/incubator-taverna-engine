package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@category({"expanded"})
@label({"SoftwareAgent"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-agent"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-types"})
@subClassOf({"http://www.w3.org/ns/prov#Agent"})
@definition({"A software agent is running software."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#SoftwareAgent")
public interface SoftwareAgent extends Agent {
}
