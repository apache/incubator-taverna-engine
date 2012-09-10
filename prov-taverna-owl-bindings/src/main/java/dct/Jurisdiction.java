package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** The extent or range of judicial, law enforcement, or other authority. */
@label({"Jurisdiction", "Jurisdiction"})
@definition({"The extent or range of judicial, law enforcement, or other authority."})
@subClassOf({"http://purl.org/dc/terms/LocationPeriodOrJurisdiction"})
@comment({"The extent or range of judicial, law enforcement, or other authority."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Jurisdiction")
public interface Jurisdiction extends LocationPeriodOrJurisdiction {
}
