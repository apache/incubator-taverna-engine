package net.sf.taverna.t2.provenance;

import net.sf.taverna.t2.provenance.connector.AbstractProvenanceConnector;

public interface ProvenanceConnectorFactory {
	public AbstractProvenanceConnector getProvenanceConnector();
	public String getConnectorType();
}
