package rdfs;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;

/** Further information about the subject resource. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface seeAlso {
	@label({"seeAlso"})
	@comment({"Further information about the subject resource.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#seeAlso")
	String[] value();

}
