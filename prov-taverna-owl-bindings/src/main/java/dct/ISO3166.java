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
 * The set of codes listed in ISO 3166-1 for the representation of names of countries.
 * @see http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html
 */
@label({"ISO 3166", "ISO 3166"})
@seeAlso({"http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html"})
@definition({"The set of codes listed in ISO 3166-1 for the representation of names of countries."})
@comment({"The set of codes listed in ISO 3166-1 for the representation of names of countries."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.iso.org/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html"})
@Iri("http://purl.org/dc/terms/ISO3166")
public class ISO3166 {
	public static ISO3166 valueOf(
		String value) {
		return new 		ISO3166		(value);
	}

	private String value;

	public ISO3166(
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
