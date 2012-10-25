/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.io.IOException;

/**
 * @author alanrw
 *
 */
public interface DataLocationService {
	
	/**
	 * Return the Data within the DataService as specified by the DataLocation
	 *
	 * @param location The DataLocation that identifies the Data
	 * @return the Data specified by the DataLocation
	 * @throws IOException thrown if the Data is not known to the DataService
	 */
	public Data getData(DataLocation location) throws IOException;

}
