package scufl2;

import java.lang.Object;
import java.util.List;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.subClassOf;

@subClassOf({"http://www.w3.org/1999/02/22-rdf-syntax-ns#List"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#IterationStrategyChildren")
public interface IterationStrategyChildren extends List {
	@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")
	Set<Object> getRdfFirsts();
	@Iri("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")
	void setRdfFirsts(Set<?> rdfFirsts);

}
