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
 * The three-letter alphabetic codes listed in ISO639-2 for the representation of names of languages.
 * @see http://lcweb.loc.gov/standards/iso639-2/langhome.html
 */
@label({"ISO 639-2", "ISO 639-2"})
@seeAlso({"http://lcweb.loc.gov/standards/iso639-2/langhome.html"})
@definition({"The three-letter alphabetic codes listed in ISO639-2 for the representation of names of languages."})
@comment({"The three-letter alphabetic codes listed in ISO639-2 for the representation of names of languages."})
@isDefinedBy({"http://lcweb.loc.gov/standards/iso639-2/langhome.html", "http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/ISO639-2")
public class ISO639_2 {
	public static ISO639_2 valueOf(
		String value) {
		return new 		ISO639_2		(value);
	}

	private String value;

	public ISO639_2(
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
