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
import skos.note;

/** A name given to the resource. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface title {
	@label({"Title", "Title"})
	@note({"In current practice, this term is used primarily with literal values; however, there are important uses with non-literal values as well. As of December 2007, the DCMI Usage Board is leaving this range unspecified pending an investigation of options."})
	@definition({"A name given to the resource."})
	@comment({"A name given to the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/title"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/title")
	String[] value();

}
