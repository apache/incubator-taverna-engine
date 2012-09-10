package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** 
 * A digital resource format.
 * Examples include the formats defined by the list of Internet Media Types.
 */
@label({"File Format", "File Format"})
@description({"Examples include the formats defined by the list of Internet Media Types."})
@definition({"A digital resource format."})
@subClassOf({"http://purl.org/dc/terms/MediaType"})
@comment({"A digital resource format.", "Examples include the formats defined by the list of Internet Media Types."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/FileFormat")
public interface FileFormat extends MediaType {
}
