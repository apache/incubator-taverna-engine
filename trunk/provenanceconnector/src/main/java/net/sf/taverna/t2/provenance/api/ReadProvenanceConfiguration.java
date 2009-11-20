package net.sf.taverna.t2.provenance.api;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ReadProvenanceConfiguration {

	String bundleName;
	private ResourceBundle RESOURCE_BUNDLE;

	private final String BUNDLE_NAME = "net.sf.taverna.t2.lineageService.capture.test.CaptureTestFiles"; //$NON-NLS-1$

	public ReadProvenanceConfiguration(String bundleName) {  
		this.bundleName = bundleName; 
		RESOURCE_BUNDLE = ResourceBundle.getBundle(bundleName);
		 }

	public String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
