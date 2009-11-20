package net.sf.taverna.t2.provenance;

import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;

public interface ProvenanceConnectorFactory {
	
	public ProvenanceConnector getProvenanceConnector();
	public String getConnectorType();

}
