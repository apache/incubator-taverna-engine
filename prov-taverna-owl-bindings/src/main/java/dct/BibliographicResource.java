package dct;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subPropertyOf;
import skos.definition;

/** A book, article, or other documentary resource. */
@label({"Bibliographic Resource", "Bibliographic Resource"})
@definition({"A book, article, or other documentary resource."})
@comment({"A book, article, or other documentary resource."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/BibliographicResource")
public interface BibliographicResource {
	/** 
	 * A bibliographic reference for the resource.
	 * Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible.
	 */
	@label({"Bibliographic Citation", "Bibliographic Citation"})
	@description({"Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible."})
	@definition({"A bibliographic reference for the resource."})
	@comment({"A bibliographic reference for the resource.", "Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/identifier", "http://purl.org/dc/terms/identifier"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/bibliographicCitation")
	Set<Object> getDctermsBibliographicCitation();
	/** 
	 * A bibliographic reference for the resource.
	 * Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible.
	 */
	@label({"Bibliographic Citation", "Bibliographic Citation"})
	@description({"Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible."})
	@definition({"A bibliographic reference for the resource."})
	@comment({"A bibliographic reference for the resource.", "Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/identifier", "http://purl.org/dc/terms/identifier"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/bibliographicCitation")
	void setDctermsBibliographicCitation(Set<?> dctermsBibliographicCitation);

}
