package dct;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subPropertyOf;
import skos.definition;

/** A list of subunits of the resource. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface tableOfContents {
	@label({"Table Of Contents", "Table Of Contents"})
	@definition({"A list of subunits of the resource."})
	@comment({"A list of subunits of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/tableOfContents")
	String[] value();

}
