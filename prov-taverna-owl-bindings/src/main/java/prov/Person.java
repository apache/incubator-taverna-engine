package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@category({"expanded"})
@label({"Person"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-agent"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-types"})
@subClassOf({"http://www.w3.org/ns/prov#Agent"})
@definition({"Agents of type Person are people."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Person")
public interface Person extends Agent {
}
