package net.sf.taverna.t2.workflowmodel.serialization.xml;

import java.util.List;

import net.sf.taverna.t2.spi.SPIRegistry;

public class XMLDeserializerRegistry extends SPIRegistry<XMLDeserializer> {

	private static XMLDeserializerRegistry instance;
	
	protected XMLDeserializerRegistry() {
		super(XMLDeserializer.class);
	}

	public static synchronized XMLDeserializerRegistry getInstance() {
		if (instance == null) {
			instance = new XMLDeserializerRegistry();
		}
		return instance;
	}

	public XMLDeserializer getDeserializer() {
		List<XMLDeserializer> instances = getInstance().getInstances();
		XMLDeserializer result = null;
		if (instances.size() == 0) {
			System.out.println("No Deserializer implementation defined");
		} else {
			if (instances.size() > 1)
				System.out
						.println("More that 1 XML Deserializer implementation defined, using the first");
			result=instances.get(0);
		}
		return result;
	}
}
