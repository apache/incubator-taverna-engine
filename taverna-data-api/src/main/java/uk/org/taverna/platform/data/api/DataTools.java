/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author alanrw
 *
 */
public class DataTools {
	
	private static Logger logger = Logger.getLogger(DataTools.class.getName());
	
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
	
	public static boolean isUsableData(Data data) {
		// Should NULL be in here
		return !(data.hasDataNature(DataNature.NOT_YET_ARRIVED) || data.hasDataNature(DataNature.WILL_NOT_COME));
	}
	
	public static URI getFirstURI(Data data) {
		if (!data.hasReferences()) {
			return null;
		}
		Set<DataReference> drSet;
		try {
			drSet = data.getReferences();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			return null;
		}
		if (drSet.isEmpty()) {
			return null;
		}
		DataReference dr = (DataReference) drSet.toArray()[0];
		return dr.getURI();
	}
	
	public static Object convertToObject(Data data) {
		Object result = null;
		switch (data.getDataNature()) {
			case NOT_YET_ARRIVED :
			case WILL_NOT_COME :
			case NULL : {
				result = null;
				break;
			}
			case BINARY_VALUE:
			case TEXT_VALUE : {
				if (data.hasExplicitValue()) {
					result = data.getExplicitValue();
				} else {
					URI uri = getFirstURI(data);
					if (uri.getScheme().equals("file")) {
						result = new File(uri);
					} else {
						result = uri;
					}
				}
				break;
			}
			case LIST : {
				ArrayList<Object> elemList = new ArrayList<Object> ();
				for (Data elem : data.getElements()) {
					elemList.add(convertToObject(elem));
				}
				result = elemList;
				break;
			}
			default: {
				logger.severe("Unable to convert");
			}
		}
		return result;
	}
}
