package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import skos.definition;

/** A statement of any changes in ownership and custody of a resource since its creation that are significant for its authenticity, integrity, and interpretation. */
@label({"Provenance Statement", "Provenance Statement"})
@definition({"A statement of any changes in ownership and custody of a resource since its creation that are significant for its authenticity, integrity, and interpretation."})
@comment({"A statement of any changes in ownership and custody of a resource since its creation that are significant for its authenticity, integrity, and interpretation."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/ProvenanceStatement")
public interface ProvenanceStatement {
}
