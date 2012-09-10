package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** A file format or physical medium. */
@label({"Media Type", "Media Type"})
@definition({"A file format or physical medium."})
@subClassOf({"http://purl.org/dc/terms/MediaTypeOrExtent"})
@comment({"A file format or physical medium."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/MediaType")
public interface MediaType extends MediaTypeOrExtent {
}
