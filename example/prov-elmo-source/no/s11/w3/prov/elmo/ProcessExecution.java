package no.s11.w3.prov.elmo;

import org.openrdf.elmo.annotations.rdf;
import java.util.Set;

/** ProcessExecution is defined to be "an identifiable activity, which performs a piece of work." */
@rdf("http://w3.org/ProvenanceOntology.owl#ProcessExecution")
public interface ProcessExecution {
	@rdf("http://w3.org/ProvenanceOntology.owl#hadParticipant")
	Set<Entity> getProvHadParticipant();
	void setProvHadParticipant(Set<? extends Entity> provHadParticipant);

	@rdf("http://w3.org/ProvenanceOntology.owl#preceded")
	Set<no.s11.w3.prov.elmo.ProcessExecution> getProvPreceded();
	void setProvPreceded(Set<? extends no.s11.w3.prov.elmo.ProcessExecution> provPreceded);

	@rdf("http://w3.org/ProvenanceOntology.owl#used")
	Set<Entity> getProvUsed();
	void setProvUsed(Set<? extends Entity> provUsed);

	@rdf("http://w3.org/ProvenanceOntology.owl#wasControlledBy")
	Set<Agent> getProvWasControlledBy();
	void setProvWasControlledBy(Set<? extends Agent> provWasControlledBy);

}
