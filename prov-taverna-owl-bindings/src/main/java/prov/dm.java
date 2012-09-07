package prov;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.subPropertyOf;

/** A reference to the principal section of the PROV-DM document that describes this concept. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface dm {
	@comment({"A reference to the principal section of the PROV-DM document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#dm")
	String[] value();

}
