/**
 * 
 */
package uk.org.taverna.platform.data.inmemory.impl;

import java.net.URI;

import uk.org.taverna.platform.data.api.DataLocation;

/**
 * @author alanrw
 *
 */
public class DataLocationImpl implements DataLocation {
	
	private URI dataServiceURI;
	private String dataID;

	public DataLocationImpl(URI uri, String id) {
		this.dataServiceURI = uri;
		this.dataID = id;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.data.api.DataLocation#getDataServiceURI()
	 */
	@Override
	public URI getDataServiceURI() {
		return dataServiceURI;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.data.api.DataLocation#getDataID()
	 */
	@Override
	public String getDataID() {
		return dataID;
	}

}
