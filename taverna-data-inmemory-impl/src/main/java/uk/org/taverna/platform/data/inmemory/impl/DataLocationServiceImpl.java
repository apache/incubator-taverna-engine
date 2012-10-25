/**
 * 
 */
package uk.org.taverna.platform.data.inmemory.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataLocation;
import uk.org.taverna.platform.data.api.DataLocationService;
import uk.org.taverna.platform.data.api.DataService;

/**
 * @author alanrw
 *
 */
public class DataLocationServiceImpl implements DataLocationService {
	
	private Set<DataService> dataServices;

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.data.api.DataLocationService#getData(uk.org.taverna.platform.data.api.DataLocation)
	 */
	@Override
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
