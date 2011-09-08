package no.s11.w3.prov.elmo;

import org.openrdf.elmo.annotations.rdf;
import java.util.Set;

@rdf("http://w3.org/ProvenanceOntology.owl#Entity")
public interface Entity {
	@rdf("http://w3.org/ProvenanceOntology.owl#isDerivedFrom")
	Set<no.s11.w3.prov.elmo.Entity> getProvIsDerivedFrom();
	void setProvIsDerivedFrom(Set<? extends no.s11.w3.prov.elmo.Entity> provIsDerivedFrom);

	@rdf("http://w3.org/ProvenanceOntology.owl#isGeneratedBy")
	Set<ProcessExecution> getProvIsGeneratedBy();
	void setProvIsGeneratedBy(Set<? extends ProcessExecution> provIsGeneratedBy);

	@rdf("http://w3.org/ProvenanceOntology.owl#isParticipantIn")
	Set<ProcessExecution> getProvIsParticipantIn();
	void setProvIsParticipantIn(Set<? extends ProcessExecution> provIsParticipantIn);

}
