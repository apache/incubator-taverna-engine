package rdfs;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;

/** A human-readable name for the subject. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface label {
	@label({"label"})
	@comment({"A human-readable name for the subject.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#label")
	String[] value();

}
