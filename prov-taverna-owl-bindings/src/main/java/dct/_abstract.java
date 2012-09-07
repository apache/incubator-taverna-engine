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

/** A summary of the resource. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface _abstract {
	@label({"Abstract", "Abstract"})
	@definition({"A summary of the resource."})
	@comment({"A summary of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/abstract")
	String[] value();

}
