package rdfs;

import java.util.Set;
import org.openrdf.annotations.Iri;

/** The class of classes. */
@label({"Class"})
@subClassOf({"http://www.w3.org/2000/01/rdf-schema#Resource"})
@comment({"The class of classes."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#Class")
public interface Class extends Resource {
	/** The subject is a subclass of a class. */
	@label({"subClassOf"})
	@comment({"The subject is a subclass of a class."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#subClassOf")
	Set<Class> getRdfsSubClassOf();
	/** The subject is a subclass of a class. */
	@label({"subClassOf"})
	@comment({"The subject is a subclass of a class."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#subClassOf")
	void setRdfsSubClassOf(Set<? extends Class> rdfsSubClassOf);

}
