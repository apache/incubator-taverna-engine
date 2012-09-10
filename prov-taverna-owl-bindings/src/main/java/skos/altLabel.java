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
 * The range of skos:altLabel is the class of RDF plain literals.
 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE})
public @interface altLabel {
	@label({"alternative label"})
	@definition({"An alternative lexical label for a resource."})
	@comment({"The range of skos:altLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@example({"Acronyms, abbreviations, spelling variants, and irregular plural/singular forms may be included among the alternative labels for a concept. Mis-spelled terms are normally included as hidden labels (see skos:hiddenLabel)."})
	@Iri("http://www.w3.org/2004/02/skos/core#altLabel")
	String[] value();

}
