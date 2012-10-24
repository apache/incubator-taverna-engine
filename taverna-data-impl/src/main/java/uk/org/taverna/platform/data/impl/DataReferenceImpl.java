/**
 * 
 */
package uk.org.taverna.platform.data.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import uk.org.taverna.platform.data.api.DataReference;

/**
 * @author alanrw
 *
 */
public class DataReferenceImpl implements DataReference {

	private URI reference;
	private Charset charset;
	private String id;

	public DataReferenceImpl(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.data.api.DataReference#getURI()
	 */
	@Override
	public URI getURI() {
		return reference;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.data.api.DataReference#getCharset()
	 */
	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setURI(URI uri) throws IOException {
		reference = uri;
	}

	@Override
	public void setCharset(Charset charset) throws IOException {
		this.charset = charset;
	}

}
