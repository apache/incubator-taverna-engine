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

/** 
 * An account of the resource.
 * Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface description {
	@label({"Description", "Description"})
	@description({"Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@definition({"An account of the resource."})
	@comment({"An account of the resource.", "Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/description")
	String[] value();

}
