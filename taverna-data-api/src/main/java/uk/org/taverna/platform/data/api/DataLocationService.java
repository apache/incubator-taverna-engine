/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.io.IOException;
import java.net.URI;
import java.util.Set;


/**
 * @author alanrw
 *
 */
public class DataLocationService {
	
	private Set<DataService> dataServices;

	
	/**
	 * Return the Data within the DataService as specified by the DataLocation
	 *
	 * @param location The DataLocation that identifies the Data
	 * @return the Data specified by the DataLocation
	 * @throws IOException thrown if the Data is not known to the DataService
	 */
	public Data getData(DataLocation location) throws IOException {
		URI dataServiceURI =  location.getDataServiceURI();
		DataService correctService = null;
		for (DataService ds : dataServices) {
			if (ds.getURI().equals(dataServiceURI)) {
				correctService = ds;
				break;
			}
		}
		if (correctService == null) {
			throw new IOException("Unable to locate DataService " + dataServiceURI);
		}
		Data result = correctService.get(location.getDataID());
		return result;
	}

	/**
	 * @return the dataServices
	 */
	public Set<DataService> getDataServices() {
		return dataServices;
	}

	/**
	 * @param dataServices the dataServices to set
	 */
	public void setDataServices(Set<DataService> dataServices) {
		this.dataServices = dataServices;
	}
}
