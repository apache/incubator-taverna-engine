/**
 *
 */
package net.sf.taverna.t2.workflowmodel.processor.activity;

import org.jdom.Element;

/**
 * An unrecognized activity is an activity that was not recognized when the
 * workflow was opened.
 * 
 * @author alanrw
 */
public final class UnrecognizedActivity extends NonExecutableActivity<Element> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/unrecognized";

	private Element conf;

	/**
	 * It is not possible to create a "naked" UnrecognizedActivity.
	 */
	private UnrecognizedActivity() {
		super();
	}

	public UnrecognizedActivity(Element config)
			throws ActivityConfigurationException {
		this();
		this.configure(config);
	}

	@Override
	public void configure(Element conf) throws ActivityConfigurationException {
		this.conf = conf;
	}

	@Override
	public Element getConfiguration() {
		return conf;
	}
}