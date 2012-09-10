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
 * The set of identifiers constructed according to the generic syntax for Uniform Resource Identifiers as specified by the Internet Engineering Task Force.
 * @see http://www.ietf.org/rfc/rfc3986.txt
 */
@label({"URI", "URI"})
@seeAlso({"http://www.ietf.org/rfc/rfc3986.txt"})
@definition({"The set of identifiers constructed according to the generic syntax for Uniform Resource Identifiers as specified by the Internet Engineering Task Force."})
@comment({"The set of identifiers constructed according to the generic syntax for Uniform Resource Identifiers as specified by the Internet Engineering Task Force."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.ietf.org/rfc/rfc3986.txt"})
@Iri("http://purl.org/dc/terms/URI")
public class URI {
	public static URI valueOf(
		String value) {
		return new 		URI		(value);
	}

	private String value;

	public URI(
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
