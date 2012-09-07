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

/** When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface editorsDefinition {
	@comment({"When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one."})
	@subPropertyOf({"http://www.w3.org/ns/prov#definition"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#editorsDefinition")
	String[] value();

}
