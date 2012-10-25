/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.net.URI;

/**
 * @author alanrw
 *
 */
public interface DataLocation {
	
	/**
	 * @return The URI identifying the DataService where the corresponding Data is located
	 */
	public URI getDataServiceURI();
	
	/**
	 * @return The identification of the Data within the DataService
	 */
	public String getDataID();

}
