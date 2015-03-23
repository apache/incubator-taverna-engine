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

package org.apache.taverna.platform.execution.impl.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.monitor.NoSuchPropertyException;
import org.apache.taverna.monitor.SteerableProperty;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.scufl2.api.core.Processor;

/**
 * ProcessorReport implementation based on MonitorableProperty objects.
 * 
 * @author David Withers
 */
public class LocalProcessorReport extends ProcessorReport {
	private static final String DISPATCH_ERRORBOUNCE_TOTAL_TRANSLATED = "dispatch:errorbounce:totalTranslated";
	private static final String DISPATCH_PARALLELIZE_COMPLETEDJOBS = "dispatch:parallelize:completedjobs";
	private static final String DISPATCH_PARALLELIZE_SENTJOBS = "dispatch:parallelize:sentjobs";
	private static final String DISPATCH_PARALLELIZE_QUEUESIZE = "dispatch:parallelize:queuesize";

	private Map<String, MonitorableProperty<?>> propertyMap;

	public LocalProcessorReport(Processor processor) {
		super(processor);
		propertyMap = new HashMap<String, MonitorableProperty<?>>();
	}

	public void addProperties(Set<MonitorableProperty<?>> properties) {
		for (MonitorableProperty<?> property : properties) {
			propertyMap.put(getPropertyName(property), property);
		}
	}

	public void saveProperties() {
		for (Entry<String, MonitorableProperty<?>> entry : propertyMap
				.entrySet())
			entry.setValue(new StaticProperty(entry.getValue()));
	}

	@Override
	public int getJobsQueued() {
		int result = -1;
		MonitorableProperty<?> property = propertyMap
				.get(DISPATCH_PARALLELIZE_QUEUESIZE);
		try {
			if (property != null)
				result = (Integer) property.getValue();
		} catch (NoSuchPropertyException e) {
		}
		return result;
	}

	@Override
	public int getJobsStarted() {
		int result = -1;
		MonitorableProperty<?> property = propertyMap
				.get(DISPATCH_PARALLELIZE_SENTJOBS);
		if (property != null) {
			try {
				result = (Integer) property.getValue();
			} catch (NoSuchPropertyException e) {
			}
		}
		return result;
	}

	@Override
	public int getJobsCompleted() {
		int result = -1;
		MonitorableProperty<?> property = propertyMap
				.get(DISPATCH_PARALLELIZE_COMPLETEDJOBS);
		try {
			if (property != null)
				result = (Integer) property.getValue();
		} catch (NoSuchPropertyException e) {
		}
		return result;
	}

	@Override
	public int getJobsCompletedWithErrors() {
		int result = -1;
		MonitorableProperty<?> property = propertyMap
				.get(DISPATCH_ERRORBOUNCE_TOTAL_TRANSLATED);
		try {
			if (property != null)
				result = (Integer) property.getValue();
		} catch (NoSuchPropertyException e) {
		}
		return result;
	}

	@Override
	public Set<String> getPropertyKeys() {
		if (!propertyMap.isEmpty())
			return new HashSet<>(propertyMap.keySet());
		return super.getPropertyKeys();
	}

	@Override
	public Object getProperty(String key) {
		Object result = null;
		MonitorableProperty<?> property = propertyMap.get(key);
		try {
			if (property != null)
				result = property.getValue();
		} catch (NoSuchPropertyException e) {
		}
		return result;
	}

	@Override
	public void setProperty(String key, Object value) {
		MonitorableProperty<?> monitorableProperty = propertyMap.get(key);
		if (monitorableProperty instanceof SteerableProperty<?>) {
			@SuppressWarnings("unchecked")
			SteerableProperty<Object> steerableProperty = (SteerableProperty<Object>) monitorableProperty;
			try {
				steerableProperty.setProperty(value);
			} catch (NoSuchPropertyException e) {
			}
		}
	}

	private String getPropertyName(MonitorableProperty<?> property) {
		StringBuilder sb = new StringBuilder();
		String[] name = property.getName();
		for (int i = 0; i < name.length; i++) {
			if (i > 0)
				sb.append(':');
			sb.append(name[i]);
		}
		return sb.toString();
	}

}
