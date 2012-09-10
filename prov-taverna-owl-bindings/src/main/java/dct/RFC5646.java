package dct;

import java.lang.Object;
import java.lang.String;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.seeAlso;

/** 
 * The set of tags constructed according to RFC 5646 for the identification of languages.
 * @see http://www.ietf.org/rfc/rfc5646.txt
 */
@label({"RFC 5646"})
@seeAlso({"http://www.ietf.org/rfc/rfc5646.txt"})
@description({"RFC 5646 obsoletes RFC 4646."})
@comment({"The set of tags constructed according to RFC 5646 for the identification of languages."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/RFC5646")
public class RFC5646 {
	public static RFC5646 valueOf(
		String value) {
		return new 		RFC5646		(value);
	}

	private String value;

	public RFC5646(
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
