package no.s11.w3.prov.elmo;

import org.openrdf.elmo.annotations.rdf;
import java.util.Set;

@rdf("http://w3.org/ProvenanceOntology.owl#ProcessExecution")
public interface ProcessExecution {
	@rdf("http://w3.org/ProvenanceOntology.owl#Used")
	Set<Entity> getProvUsed();
	void setProvUsed(Set<? extends Entity> provUsed);

	@rdf("http://w3.org/ProvenanceOntology.owl#isControlledBy")
	Set<Agent> getProvIsControlledBy();
	void setProvIsControlledBy(Set<? extends Agent> provIsControlledBy);

	@rdf("http://w3.org/ProvenanceOntology.owl#isPrecededBy")
	Set<no.s11.w3.prov.elmo.ProcessExecution> getProvIsPrecededBy();
	void setProvIsPrecededBy(Set<? extends no.s11.w3.prov.elmo.ProcessExecution> provIsPrecededBy);

}
