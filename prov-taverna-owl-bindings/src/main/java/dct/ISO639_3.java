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
 * The set of three-letter codes listed in ISO 639-3 for the representation of names of languages.
 * @see http://www.sil.org/iso639-3/
 */
@label({"ISO 639-3", "ISO 639-3"})
@seeAlso({"http://www.sil.org/iso639-3/"})
@definition({"The set of three-letter codes listed in ISO 639-3 for the representation of names of languages."})
@comment({"The set of three-letter codes listed in ISO 639-3 for the representation of names of languages."})
@isDefinedBy({"http://purl.org/dc/terms/", "http://www.sil.org/iso639-3/"})
@Iri("http://purl.org/dc/terms/ISO639-3")
public class ISO639_3 {
	public static ISO639_3 valueOf(
		String value) {
		return new 		ISO639_3		(value);
	}

	private String value;

	public ISO639_3(
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
