/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.net.URI;

/**
 * @author alanrw
 *
 */
public class DataLocation {
	
	private URI dataServiceURI;
	private String dataID;
	
	public DataLocation(URI uri, String id) {
		this.dataServiceURI = uri;
		this.dataID = id;
	}

	public URI getDataServiceURI() {
		return dataServiceURI;
	}

	public String getDataID() {
		return dataID;
	}


}
