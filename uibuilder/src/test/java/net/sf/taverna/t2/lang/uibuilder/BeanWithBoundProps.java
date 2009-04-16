package net.sf.taverna.t2.lang.uibuilder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Example bean with string and URL properties, both of which fire property
 * change events
 * 
 * @author Tom Oinn
 * 
 */
public class BeanWithBoundProps {

	private String string = "Default value";
	private URL url;
	private PropertyChangeSupport pcs;
	private URI uri;
	

	public BeanWithBoundProps() {
		try {
			this.url = new URL("http://some.default.url");
			this.pcs = new PropertyChangeSupport(this);
			this.uri = URI.create("http://google.com/"); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUrl(URL url) {
		URL old = this.url;
		this.url = url;
		this.pcs.firePropertyChange("url", old, url);
	}

	public URL getUrl() {
		return url;
	}
	
	

	public void setString(String string) {
		String old = this.string;
		this.string = string;
		this.pcs.firePropertyChange("string", old, string);
	}

	public String getString() {
		return string;
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener l) {
		pcs.addPropertyChangeListener(propertyName, l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public URI getUri() {
		return uri;
	}

}
