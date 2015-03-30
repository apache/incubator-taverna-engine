package org.apache.taverna.provenance;

import org.apache.taverna.provenance.connector.AbstractProvenanceConnector;

public interface ProvenanceConnectorFactory {
	public AbstractProvenanceConnector getProvenanceConnector();
	public String getConnectorType();
}
