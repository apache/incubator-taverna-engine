/*******************************************************************************
 * Copyright (C) 2007-2011 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.HashSet;
import java.util.Map;

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
public final class DisabledActivity extends NonExecutableActivity<ActivityAndBeanWrapper> {

	public static final String URI = "http://ns.taverna.org.uk/2010/activity/disabled";

	private static Logger logger = Logger.getLogger(DisabledActivity.class);

	/**
	 * Conf holds the offline Activity and its configuration.
	 */
	private ActivityAndBeanWrapper conf;

	private Object lastWorkingConfiguration;

	/**
	 * It is not possible to create a "naked" DisabledActivity.
	 */
	private DisabledActivity() {
		super();
		lastWorkingConfiguration = null;
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

	public boolean configurationWouldWork() {
		return configurationWouldWork(conf.getBean());
	}

	public boolean configurationWouldWork(Object newConfig) {
		boolean result = true;
		lastWorkingConfiguration = null;
		try {
			Activity aa = (Activity) (conf.getActivity().getClass().newInstance());
			aa.configure(newConfig);
			boolean unknownPort = false;
			Map<String, String> currentInputPortMap = this
					.getInputPortMapping();
			HashSet<String> currentInputNames = new HashSet<String>();
			currentInputNames.addAll(currentInputPortMap.values()) ;
			for (ActivityInputPort aip : ((Activity<?>)aa).getInputPorts()) {
				currentInputNames.remove(aip.getName());
			}
			unknownPort = !currentInputNames.isEmpty();

			if (!unknownPort) {
				Map<String, String> currentOutputPortMap = this
				.getOutputPortMapping();
				HashSet<String> currentOutputNames = new HashSet<String>();
				currentOutputNames.addAll(currentOutputPortMap.values()) ;
				for (OutputPort aop : ((Activity<?>)aa).getOutputPorts()) {
					currentOutputNames.remove(aop.getName());
				}
				unknownPort = !currentOutputNames.isEmpty();
			}
			if (unknownPort) {
				result = false;
			}
		} catch (ActivityConfigurationException ex) {
			result = false;
		} catch (InstantiationException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
		if (result) {
		    lastWorkingConfiguration = newConfig;
		}
		return result;
	}

	public Object getLastWorkingConfiguration() {
	    return lastWorkingConfiguration;
	}
}
