package rdfs;

import java.lang.Object;
import java.lang.String;
import org.openrdf.annotations.Iri;

/** The class of literal values, eg. textual strings and integers. */
@label({"Literal"})
@subClassOf({"http://www.w3.org/2000/01/rdf-schema#Resource"})
@comment({"The class of literal values, eg. textual strings and integers."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#Literal")
public class Literal {
	public static Literal valueOf(
		String value) {
		return new 		Literal		(value);
	}

	private String value;

	public Literal(
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
