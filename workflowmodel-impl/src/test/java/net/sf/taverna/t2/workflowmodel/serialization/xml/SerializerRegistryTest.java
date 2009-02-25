package net.sf.taverna.t2.workflowmodel.serialization.xml;

import static org.junit.Assert.*;

import org.junit.Test;

public class SerializerRegistryTest {
	
	@Test
	public void getSerializer() {
		XMLSerializer serializer = XMLSerializerRegistry.getInstance().getSerializer();
		assertNotNull(serializer);
	}
	
	@Test
	public void getDeserializer() {
		XMLDeserializer deserializer = XMLDeserializerRegistry.getInstance().getDeserializer();
		assertNotNull(deserializer);
	}

}
