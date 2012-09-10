package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** Note that there are kinds of bundles (e.g. handwritten letters, audio recordings, etc.) that are not expressed in PROV-O, but can be still be described by PROV-O. */
@category({"expanded"})
@label({"Bundle"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-bundle-entity"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-bundle-declaration"})
@subClassOf({"http://www.w3.org/ns/prov#Entity"})
@comment({"Note that there are kinds of bundles (e.g. handwritten letters, audio recordings, etc.) that are not expressed in PROV-O, but can be still be described by PROV-O."})
@definition({"A bundle is a named set of provenance descriptions, and is itself an Entity, so allowing provenance of provenance to be expressed."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@Iri("http://www.w3.org/ns/prov#Bundle")
public interface Bundle extends Entity {
}
