package rdf;

import java.lang.Boolean;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.Class;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;

@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property")
public interface Property {
	@Iri("http://ns.taverna.org.uk/2010/scufl2#requiredConfiguration")
	Boolean getScufl2RequiredConfiguration();
	@Iri("http://ns.taverna.org.uk/2010/scufl2#requiredConfiguration")
	void setScufl2RequiredConfiguration(Boolean scufl2RequiredConfiguration);

	/** A domain of the subject property. */
	@label({"domain"})
	@comment({"A domain of the subject property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#domain")
	Set<Class> getRdfsDomains();
	/** A domain of the subject property. */
	@label({"domain"})
	@comment({"A domain of the subject property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#domain")
	void setRdfsDomains(Set<? extends Class> rdfsDomains);

	/** A range of the subject property. */
	@label({"range"})
	@comment({"A range of the subject property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#range")
	Set<Class> getRdfsRanges();
	/** A range of the subject property. */
	@label({"range"})
	@comment({"A range of the subject property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#range")
	void setRdfsRanges(Set<? extends Class> rdfsRanges);

	/** The subject is a subproperty of a property. */
	@label({"subPropertyOf"})
	@comment({"The subject is a subproperty of a property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")
	Set<Property> getRdfsSubPropertyOf();
	/** The subject is a subproperty of a property. */
	@label({"subPropertyOf"})
	@comment({"The subject is a subproperty of a property."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#subPropertyOf")
	void setRdfsSubPropertyOf(Set<? extends Property> rdfsSubPropertyOf);

}
