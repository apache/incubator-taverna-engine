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

/** Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface unqualifiedForm {
	@comment({"Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#unqualifiedForm")
	String[] value();

}
