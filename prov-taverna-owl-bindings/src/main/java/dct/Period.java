package dct;

import java.lang.Object;
import java.lang.String;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;
import skos.definition;

/** 
 * The set of time intervals defined by their limits according to the DCMI Period Encoding Scheme.
 * @see http://dublincore.org/documents/dcmi-period/
 */
@label({"DCMI Period", "DCMI Period"})
@seeAlso({"http://dublincore.org/documents/dcmi-period/"})
@definition({"The set of time intervals defined by their limits according to the DCMI Period Encoding Scheme."})
@comment({"The set of time intervals defined by their limits according to the DCMI Period Encoding Scheme."})
@isDefinedBy({"http://dublincore.org/documents/dcmi-period/", "http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Period")
public class Period {
	public static Period valueOf(
		String value) {
		return new 		Period		(value);
	}

	private String value;

	public Period(
	String value) {
	this.value = value;
	}

	public String toString() {
		return value;
	}

	public int hashCode() {
		return value.hashCode();
	}

	public boolean equals(
		Object o) {
		return getClass().equals(o.getClass()) && toString().equals(o.toString());
	}

}
