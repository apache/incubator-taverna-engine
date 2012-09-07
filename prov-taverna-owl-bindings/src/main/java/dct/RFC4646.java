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
 * The set of tags constructed according to RFC 4646 for the identification of languages.
 * RFC 4646 obsoletes RFC 3066.
 * @see http://www.ietf.org/rfc/rfc4646.txt
 */
@label({"RFC 4646", "RFC 4646"})
@seeAlso({"http://www.ietf.org/rfc/rfc4646.txt"})
@description({"RFC 4646 obsoletes RFC 3066."})
@definition({"The set of tags constructed according to RFC 4646 for the identification of languages."})
@comment({"The set of tags constructed according to RFC 4646 for the identification of languages.", "RFC 4646 obsoletes RFC 3066."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.ietf.org/rfc/rfc4646.txt"})
@Iri("http://purl.org/dc/terms/RFC4646")
public class RFC4646 {
	public static RFC4646 valueOf(
		String value) {
		return new 		RFC4646		(value);
	}

	private String value;

	public RFC4646(
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
