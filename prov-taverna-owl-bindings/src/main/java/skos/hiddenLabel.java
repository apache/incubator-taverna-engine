package skos;

import java.lang.String;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;

/** 
 * The range of skos:hiddenLabel is the class of RDF plain literals.
 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface hiddenLabel {
	@label({"hidden label"})
	@definition({"A lexical label for a resource that should be hidden when generating visual displays of the resource, but should still be accessible to free text search operations."})
	@comment({"The range of skos:hiddenLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#hiddenLabel")
	String[] value();

}
