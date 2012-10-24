/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author alanrw
 *
 */
public class DataTools {
	
	public static Data createExplicitBinaryData(DataService service, byte[] value) throws IOException {
		Data result = service.create(DataNature.BINARY_VALUE);
		result.setExplicitValue(value);
		result.setApproximateSizeInBytes(value.length);
		return result;
	}

	public static Data createExplicitTextData(DataService service, String value) throws IOException {
		Data result = service.create(DataNature.TEXT_VALUE);
		result.setExplicitValue(value);
		result.setApproximateSizeInBytes(value.getBytes("utf-8").length);
		return result;
	}

	public static Data createEmptyListData(DataService service) throws IOException {
		Data result = service.create(DataNature.LIST);
		result.setElements(new ArrayList<Data>());
		result.setApproximateSizeInBytes(-1);
		return result;
	}
	
	public static Data createListData(DataService service, List<Data>  elements) throws IOException {
		Data result = service.create(DataNature.LIST);
		result.setElements(elements);
		result.setApproximateSizeInBytes(-1);
		return result;		
	}
	
	public static Data createNullData(DataService service) throws IOException {
		Data result = service.create(DataNature.NULL);
		return result;
	}

	public static Data createSingleReferenceTextData(DataService service,
			String webAddress) throws IOException, URISyntaxException {
		DataReference dr = createSimpleDataReference(service, webAddress);
		Data result = service.create(DataNature.TEXT_VALUE);
		HashSet<DataReference> references = new HashSet<DataReference>();
		references.add(dr);
		result.setReferences(references);
		return result;
	}

	private static DataReference createSimpleDataReference(
			DataService service, String webAddress) throws URISyntaxException, IOException {
		DataReference result = service.createDataReference();
		result.setURI(new URI(webAddress));
		return result;
	}
}
