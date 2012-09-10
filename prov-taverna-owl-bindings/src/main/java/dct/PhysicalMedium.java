package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** 
 * A physical material or carrier.
 * Examples include paper, canvas, or DVD.
 */
@label({"Physical Medium", "Physical Medium"})
@description({"Examples include paper, canvas, or DVD."})
@definition({"A physical material or carrier."})
@subClassOf({"http://purl.org/dc/terms/MediaType"})
@comment({"A physical material or carrier.", "Examples include paper, canvas, or DVD."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/PhysicalMedium")
public interface PhysicalMedium extends MediaType {
}
