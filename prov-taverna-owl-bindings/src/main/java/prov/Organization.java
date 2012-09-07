package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@category({"expanded"})
@label({"Organization"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-agent"})
@component({"agents-responsibility"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-types"})
@subClassOf({"http://www.w3.org/ns/prov#Agent"})
@definition({"Agents of type Organization are social institutions such as companies, societies etc."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Organization")
public interface Organization extends Agent {
}
