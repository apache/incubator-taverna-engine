package net.sf.taverna.t2.workflowmodel.serialization.xml;

import java.util.List;

import net.sf.taverna.t2.spi.SPIRegistry;

public class XMLSerializerRegistry extends SPIRegistry<XMLSerializer> {

	private static XMLSerializerRegistry instance;
	
	protected XMLSerializerRegistry() {
		super(XMLSerializer.class);
	}

	public static synchronized XMLSerializerRegistry getInstance() {
		if (instance == null) {
			instance = new XMLSerializerRegistry();
		}
		return instance;
	}

	public XMLSerializer getSerializer() {
		List<XMLSerializer> instances = getInstance().getInstances();
		XMLSerializer result = null;
		if (instances.size() == 0) {
			System.out.println("No Serializer implementation defined");
		} else {
			if (instances.size() > 1)
				System.out
						.println("More that 1 XML Serializer implementation defined, using the first");
			result=instances.get(0);
		}
		return result;
	}
}