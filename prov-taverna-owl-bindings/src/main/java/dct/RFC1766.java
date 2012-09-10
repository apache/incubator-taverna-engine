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
 * The set of tags, constructed according to RFC 1766, for the identification of languages.
 * @see http://www.ietf.org/rfc/rfc1766.txt
 */
@label({"RFC 1766", "RFC 1766"})
@seeAlso({"http://www.ietf.org/rfc/rfc1766.txt"})
@definition({"The set of tags, constructed according to RFC 1766, for the identification of languages."})
@comment({"The set of tags, constructed according to RFC 1766, for the identification of languages."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.ietf.org/rfc/rfc1766.txt"})
@Iri("http://purl.org/dc/terms/RFC1766")
public class RFC1766 {
	public static RFC1766 valueOf(
		String value) {
		return new 		RFC1766		(value);
	}

	private String value;

	public RFC1766(
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
