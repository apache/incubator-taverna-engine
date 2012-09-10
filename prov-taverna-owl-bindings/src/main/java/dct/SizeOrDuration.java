package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** 
 * A dimension or extent, or a time taken to play or execute.
 * Examples include a number of pages, a specification of length, width, and breadth, or a period in hours, minutes, and seconds.
 */
@label({"Size or Duration", "Size or Duration"})
@description({"Examples include a number of pages, a specification of length, width, and breadth, or a period in hours, minutes, and seconds."})
@definition({"A dimension or extent, or a time taken to play or execute."})
@subClassOf({"http://purl.org/dc/terms/MediaTypeOrExtent"})
@comment({"A dimension or extent, or a time taken to play or execute.", "Examples include a number of pages, a specification of length, width, and breadth, or a period in hours, minutes, and seconds."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/SizeOrDuration")
public interface SizeOrDuration extends MediaTypeOrExtent {
}
