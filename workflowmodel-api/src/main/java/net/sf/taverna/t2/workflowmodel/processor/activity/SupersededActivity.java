/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.processor.activity;

/**
 * 
 * A superseded activity is one which has been replaced be another activity type of similar functionality but different configuration and name
 * 
 * @author alanrw
 *
 */
public interface SupersededActivity<ConfigurationType> extends Activity<ConfigurationType> {
	
	Activity<?> getReplacementActivity() throws ActivityConfigurationException;

}
