package no.s11.w3.prov.elmo;

import org.openrdf.elmo.annotations.rdf;

/** ProvenanceContainer is defined to be an aggregation of provenance assertions. A provenance container SHOULD have an URI associated with it. */
@rdf("http://w3.org/ProvenanceOntology.owl#ProvenanceContainer")
public interface ProvenanceContainer extends Entity {
}
