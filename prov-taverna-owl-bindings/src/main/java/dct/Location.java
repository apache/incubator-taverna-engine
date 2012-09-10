package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** A spatial region or named place. */
@label({"Location", "Location"})
@definition({"A spatial region or named place."})
@subClassOf({"http://purl.org/dc/terms/LocationPeriodOrJurisdiction"})
@comment({"A spatial region or named place."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Location")
public interface Location extends LocationPeriodOrJurisdiction {
}
