package prov;

import org.openrdf.annotations.Iri;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;

/** 
 * 
 * @see http://www.w3.org/ns/prov#hadLocation
 */
@category({"expanded"})
@label({"Location"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-attribute-location"})
@seeAlso({"http://www.w3.org/ns/prov#hadLocation"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-attribute"})
@definition({"A location can be an identifiable geographic place (ISO 19112), but it can also be a non-geographic place such as a directory, row, or column. As such, there are numerous ways in which location can be expressed, such as by a coordinate, address, landmark, and so forth."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Location")
public interface Location {
}
