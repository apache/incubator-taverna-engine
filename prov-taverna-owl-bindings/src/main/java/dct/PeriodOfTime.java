package dct;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** An interval of time that is named or defined by its start and end dates. */
@label({"Period of Time", "Period of Time"})
@definition({"An interval of time that is named or defined by its start and end dates."})
@subClassOf({"http://purl.org/dc/terms/LocationPeriodOrJurisdiction"})
@comment({"An interval of time that is named or defined by its start and end dates."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/PeriodOfTime")
public interface PeriodOfTime extends LocationPeriodOrJurisdiction {
}
