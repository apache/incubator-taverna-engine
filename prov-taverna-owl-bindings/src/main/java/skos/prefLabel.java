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
 * A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.
 * The range of skos:prefLabel is the class of RDF plain literals.
 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise
 *       disjoint properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface prefLabel {
	@label({"preferred label"})
	@definition({"The preferred lexical label for a resource, in a given language."})
	@comment({"A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.", "The range of skos:prefLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise\n" + 
		"      disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#prefLabel")
	String[] value();

}
