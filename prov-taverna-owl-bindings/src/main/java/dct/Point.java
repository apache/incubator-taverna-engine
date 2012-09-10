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
 * The set of points in space defined by their geographic coordinates according to the DCMI Point Encoding Scheme.
 * @see http://dublincore.org/documents/dcmi-point/
 */
@label({"DCMI Point", "DCMI Point"})
@seeAlso({"http://dublincore.org/documents/dcmi-point/"})
@definition({"The set of points in space defined by their geographic coordinates according to the DCMI Point Encoding Scheme."})
@comment({"The set of points in space defined by their geographic coordinates according to the DCMI Point Encoding Scheme."})
@isDefinedBy({"http://dublincore.org/documents/dcmi-point/", "http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Point")
public class Point {
	public static Point valueOf(
		String value) {
		return new 		Point		(value);
	}

	private String value;

	public Point(
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
