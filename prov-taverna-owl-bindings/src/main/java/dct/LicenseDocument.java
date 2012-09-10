package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** A legal document giving official permission to do something with a Resource. */
@label({"License Document", "License Document"})
@definition({"A legal document giving official permission to do something with a Resource."})
@subClassOf({"http://purl.org/dc/terms/RightsStatement"})
@comment({"A legal document giving official permission to do something with a Resource."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/LicenseDocument")
public interface LicenseDocument extends RightsStatement {
}
