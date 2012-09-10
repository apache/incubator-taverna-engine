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
 * The set of regions in space defined by their geographic coordinates according to the DCMI Box Encoding Scheme.
 * @see http://dublincore.org/documents/dcmi-box/
 */
@label({"DCMI Box", "DCMI Box"})
@seeAlso({"http://dublincore.org/documents/dcmi-box/"})
@definition({"The set of regions in space defined by their geographic coordinates according to the DCMI Box Encoding Scheme."})
@comment({"The set of regions in space defined by their geographic coordinates according to the DCMI Box Encoding Scheme."})
@isDefinedBy({"http://dublincore.org/documents/dcmi-box/", "http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Box")
public class Box {
	public static Box valueOf(
		String value) {
		return new 		Box		(value);
	}

	private String value;

	public Box(
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
