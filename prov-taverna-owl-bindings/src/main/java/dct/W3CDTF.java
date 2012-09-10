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
 * The set of dates and times constructed according to the W3C Date and Time Formats Specification.
 * @see http://www.w3.org/TR/NOTE-datetime
 */
@label({"W3C-DTF", "W3C-DTF"})
@seeAlso({"http://www.w3.org/TR/NOTE-datetime"})
@definition({"The set of dates and times constructed according to the W3C Date and Time Formats Specification."})
@comment({"The set of dates and times constructed according to the W3C Date and Time Formats Specification."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.w3.org/TR/NOTE-datetime"})
@Iri("http://purl.org/dc/terms/W3CDTF")
public class W3CDTF {
	public static W3CDTF valueOf(
		String value) {
		return new 		W3CDTF		(value);
	}

	private String value;

	public W3CDTF(
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
