/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.HashSet;
import java.util.Map;

import org.apache.taverna.workflowmodel.OutputPort;

import org.apache.log4j.Logger;

/**
 * A disabled activity is a wrapper for an Activity that is offline or similarly
 * disabled. This cannot be done just by setting a flag on the corresponding
 * activity as special code needs to be used to create the ports of the disabled
 * activity that, obviously, cannot be done by confighuring the offline
 * activity.
 *
 * @author alanrw
 */
public final class DisabledActivity extends
		NonExecutableActivity<ActivityAndBeanWrapper> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/disabled";
	private static final Logger logger = Logger
			.getLogger(DisabledActivity.class);

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
	public DisabledActivity(Class<? extends Activity<?>> activityClass,
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
	public DisabledActivity(Activity<?> activity, Object config) {
		this();
		ActivityAndBeanWrapper disabledConfig = new ActivityAndBeanWrapper();
		disabledConfig.setActivity(activity);
		disabledConfig.setBean(config);
		try {
			configure(disabledConfig);
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
	 * @param activity
	 *            The Activity that is now disabled.
	 */
	public DisabledActivity(Activity<?> activity) {
		this(activity, activity.getConfiguration());
		for (ActivityInputPort aip : activity.getInputPorts())
			addInput(aip.getName(), aip.getDepth(), aip.allowsLiteralValues(),
					aip.getHandledReferenceSchemes(),
					aip.getTranslatedElementClass());
		for (OutputPort op : activity.getOutputPorts())
			addOutput(op.getName(), op.getDepth(), op.getGranularDepth());
		getInputPortMapping().clear();
		getInputPortMapping().putAll(activity.getInputPortMapping());
		getOutputPortMapping().clear();
		getOutputPortMapping().putAll(activity.getOutputPortMapping());
	}

	@Override
	public void configure(ActivityAndBeanWrapper conf)
			throws ActivityConfigurationException {
		this.conf = conf;
	}

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
			@SuppressWarnings("unchecked")
			Activity<Object> aa = conf.getActivity().getClass().newInstance();
			aa.configure(newConfig);
			boolean unknownPort = false;
			Map<String, String> currentInputPortMap = getInputPortMapping();
			HashSet<String> currentInputNames = new HashSet<>();
			currentInputNames.addAll(currentInputPortMap.values()) ;
			for (ActivityInputPort aip : aa.getInputPorts())
				currentInputNames.remove(aip.getName());
			unknownPort = !currentInputNames.isEmpty();

			if (!unknownPort) {
				Map<String, String> currentOutputPortMap = getOutputPortMapping();
				HashSet<String> currentOutputNames = new HashSet<>();
				currentOutputNames.addAll(currentOutputPortMap.values());
				for (OutputPort aop : aa.getOutputPorts())
					currentOutputNames.remove(aop.getName());
				unknownPort = !currentOutputNames.isEmpty();
			}
			if (unknownPort)
				result = false;
		} catch (ActivityConfigurationException ex) {
			result = false;
		} catch (InstantiationException|IllegalAccessException e) {
			return false;
		}
		if (result)
		    lastWorkingConfiguration = newConfig;
		return result;
	}

	public Object getLastWorkingConfiguration() {
	    return lastWorkingConfiguration;
	}
}
