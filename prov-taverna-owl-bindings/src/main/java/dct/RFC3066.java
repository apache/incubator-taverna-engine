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
 * The set of tags constructed according to RFC 3066 for the identification of languages.
 * RFC 3066 has been obsoleted by RFC 4646.
 * @see http://www.ietf.org/rfc/rfc3066.txt
 */
@label({"RFC 3066", "RFC 3066"})
@seeAlso({"http://www.ietf.org/rfc/rfc3066.txt"})
@description({"RFC 3066 has been obsoleted by RFC 4646."})
@definition({"The set of tags constructed according to RFC 3066 for the identification of languages."})
@comment({"The set of tags constructed according to RFC 3066 for the identification of languages.", "RFC 3066 has been obsoleted by RFC 4646."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.ietf.org/rfc/rfc3066.txt"})
@Iri("http://purl.org/dc/terms/RFC3066")
public class RFC3066 {
	public static RFC3066 valueOf(
		String value) {
		return new 		RFC3066		(value);
	}

	private String value;

	public RFC3066(
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
