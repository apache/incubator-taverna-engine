/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.Map;

import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.OutputPort;

import org.apache.log4j.Logger;

/**
 * A disabled activity is a wrapper for an Activity that is offline or similarly
 * disabled. This cannot be done just by setting a flag on the corresponding
 * activity as special code needs to be used to create the ports of the disabled
 * activity that, obviously, cannot be done by confighuring the offline
 * activity.
 * 
 * @author alanrw
 * 
 */
public final class DisabledActivity extends
		AbstractAsynchronousActivity<ActivityAndBeanWrapper> {

	private static Logger logger = Logger.getLogger(DisabledActivity.class);

	/**
	 * Conf holds the offline Activity and its configuration.
	 */
	private ActivityAndBeanWrapper conf;

	/**
	 * It is not possible to create a "naked" DisabledActivity.
	 */
	private DisabledActivity() {
		super();
	}

	/**
	 * Create a DisabledActivity that represents an offline activity of the
	 * specified class with the specified configuration. This constructor is
	 * commonly used when reading in an Activity which cannot be initially
	 * configured because it is offline.
	 * 
	 * @param activityClass
	 *            The class of Activity that is offline.
	 * @param config
	 *            The configuration of the offline Activity.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ActivityConfigurationException
	 */
	public DisabledActivity(Class<? extends Activity> activityClass,
			Object config) throws InstantiationException,
			IllegalAccessException, ActivityConfigurationException {
		this(activityClass.newInstance(), config);
	}

	/**
	 * Create a DisabledActivity that represents a specific Activity with its
	 * configuration.
	 * 
	 * @param activity
	 *            The Activity that is offline
	 * @param config
	 *            The configuration of the activity.
	 */
	public DisabledActivity(Activity activity, Object config) {
		this();
		ActivityAndBeanWrapper disabledConfig = new ActivityAndBeanWrapper();
		disabledConfig.setActivity(activity);
		disabledConfig.setBean(config);
		try {
			this.configure(disabledConfig);
		} catch (ActivityConfigurationException e) {
			logger.error(e);
		}
	}

	/**
	 * Create a DisabledActivity that represents a specific Activity that is now
	 * disabled e.g. by its remote endpoint going offline. Note that in this
	 * case, the ports of the DisabledActivity and their mapping to the
	 * containing Processor's ports can be inherited from the Activity that is
	 * now disabled.
	 * 
	 * @param activity The Activity that is now disabled.
	 */
	public DisabledActivity(Activity<?> activity) {
		this(activity, activity.getConfiguration());
		for (ActivityInputPort aip : activity.getInputPorts()) {
			this.addInput(aip.getName(), aip.getDepth(), aip
					.allowsLiteralValues(), aip.getHandledReferenceSchemes(),
					aip.getTranslatedElementClass());
		}
		for (OutputPort op : activity.getOutputPorts()) {
			this.addOutput(op.getName(), op.getDepth(), op.getGranularDepth());
		}
		this.getInputPortMapping().clear();
		this.getInputPortMapping().putAll(activity.getInputPortMapping());
		this.getOutputPortMapping().clear();
		this.getOutputPortMapping().putAll(activity.getOutputPortMapping());
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity#configure(java.lang.Object)
	 */
	@Override
	public void configure(ActivityAndBeanWrapper conf)
			throws ActivityConfigurationException {
		this.conf = conf;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity#getConfiguration()
	 */
	@Override
	public ActivityAndBeanWrapper getConfiguration() {
		return conf;
	}

	/**
	 * Add an input to the DisabledActivity with the specified name.
	 * @param portName
	 */
	public void addProxyInput(String portName) {
		super.addInput(portName, 0, true, null, null);
	}

	/**
	 * Add an input to the DisabledActivity with the specified name and depth.
	 * @param portName
	 * @param depth
	 */
	public void addProxyInput(String portName, int depth) {
		super.addInput(portName, depth, true, null, null);
	}

	/**
	 * Add an output to the DisabledActivity with the specified name
	 * 
	 * @param portName
	 */
	public void addProxyOutput(String portName) {
		super.addOutput(portName, 0);
	}

	/**
	 * Add an output to the DisabledActivity with the specified name and depth
	 * 
	 * @param portName
	 * @param depth
	 */
	public void addProxyOutput(String portName, int depth) {
		super.addOutput(portName, depth);
	}

	/**
	 * @return The Activity that has been disabled
	 */
	public Activity<?> getActivity() {
		return getConfiguration().getActivity();
	}

	/**
	 * @return The configuration of the Activity that has been disabled
	 */
	public Object getActivityConfiguration() {
		return getConfiguration().getBean();
	}

	/* 
	 * Attempting to run a DisabledActivity will always fail.
	 * 
	 * (non-Javadoc)
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity#executeAsynch(java.util.Map, net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback)
	 */
	@Override
	public void executeAsynch(Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {

			public void run() {
				callback.fail("The service is offline");
			}
		});
	}

}
