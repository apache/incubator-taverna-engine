package net.sf.taverna.t2.provenance;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;

import org.junit.Test;

public class TestSPIRegistry {
	
	@Test
	public void findSPI() {
		List<ProvenanceConnector> instances = ProvenanceConnectorRegistry.getInstance().getInstances();
		for (ProvenanceConnector instance:instances) {
			System.out.println(instance.getClass().getName());
		}
//		assertEquals(2, instances.size());
	}

}
