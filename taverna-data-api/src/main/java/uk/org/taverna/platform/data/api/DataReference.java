/**
 * 
 */
package uk.org.taverna.platform.data.api;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

/**
 * 
 * A DataReference is a reference to where the data value can be accessed.
 * 
 * @author alanrw
 *
 */
public interface DataReference {
	
	public String getID();
	
	/**
	 * @return The URI at which the data is
	 */
	public URI getURI();
	
	public void setURI(URI uri) throws IOException;

	/**
	 * @return The Charset in which the text data is stored - or null if the data is not text
	 */
	public Charset getCharset();
	
	public void setCharset(Charset charset) throws IOException;

}
