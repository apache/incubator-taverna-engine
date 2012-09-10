package dct;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subPropertyOf;
import skos.definition;
import skos.note;

/** A material thing. */
@label({"Physical Resource", "Physical Resource"})
@definition({"A material thing."})
@comment({"A material thing."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/PhysicalResource")
public interface PhysicalResource {
	/** The material or physical carrier of the resource. */
	@label({"Medium", "Medium"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal but only as a related description."})
	@definition({"The material or physical carrier of the resource."})
	@comment({"The material or physical carrier of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format", "http://purl.org/dc/terms/format"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/medium")
	Set<PhysicalMedium> getDctermsMediums();
	/** The material or physical carrier of the resource. */
	@label({"Medium", "Medium"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal but only as a related description."})
	@definition({"The material or physical carrier of the resource."})
	@comment({"The material or physical carrier of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format", "http://purl.org/dc/terms/format"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/medium")
	void setDctermsMediums(Set<? extends PhysicalMedium> dctermsMediums);

}
