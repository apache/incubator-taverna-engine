package no.s11.w3.prov.elmo;

import org.openrdf.elmo.annotations.rdf;
import java.util.Set;

/** An identifiable characterized entity. */
@rdf("http://w3.org/ProvenanceOntology.owl#Entity")
public interface Entity {
	/** wasDerivedFrom links two distinct characterized entities, where "some characterized entity is transformed from, created from, or affected by another characterized entity." */
	@rdf("http://w3.org/ProvenanceOntology.owl#wasDerivedFrom")
	Set<no.s11.w3.prov.elmo.Entity> getProvWasDerivedFrom();
	/** wasDerivedFrom links two distinct characterized entities, where "some characterized entity is transformed from, created from, or affected by another characterized entity." */
	void setProvWasDerivedFrom(Set<? extends no.s11.w3.prov.elmo.Entity> provWasDerivedFrom);

	/** wasGeneratedBy links Entitites with ProcessExecution representing that entity was generated as a result of ProcessExecution */
	@rdf("http://w3.org/ProvenanceOntology.owl#wasGeneratedBy")
	Set<ProcessExecution> getProvWasGeneratedBy();
	/** wasGeneratedBy links Entitites with ProcessExecution representing that entity was generated as a result of ProcessExecution */
	void setProvWasGeneratedBy(Set<? extends ProcessExecution> provWasGeneratedBy);

}
