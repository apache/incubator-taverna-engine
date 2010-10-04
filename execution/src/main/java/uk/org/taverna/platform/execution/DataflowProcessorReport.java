package uk.org.taverna.platform.execution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.monitor.NoSuchPropertyException;
import net.sf.taverna.t2.monitor.SteerableProperty;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Processor;

public class DataflowProcessorReport extends ProcessorReport {

	private Map<String, MonitorableProperty<?>> propertyMap;

	private MonitorableProperty<?> jobsQueued, jobsStarted, jobsCompleted, jobsCompletedWithErrors;
	
	public DataflowProcessorReport(Processor processor, WorkflowReport parentReport) {
		super(processor, parentReport);
	}

	public void addProperties(Set<MonitorableProperty<?>> properties) {
		propertyMap = new HashMap<String, MonitorableProperty<?>>();
		for (MonitorableProperty<?> property : properties) {
			String propertyName = getPropertyName(property);
			propertyMap.put(propertyName, property);
			if (propertyName.equals("dispatch:parallelize:queuesize")) {
				jobsQueued = property;
			} else if (propertyName.equals("dispatch:parallelize:sentjobs")) {
				jobsStarted = property;
			} else if (propertyName.equals("dispatch:parallelize:completedjobs")) {
				jobsCompleted = property;
			} else if (propertyName.equals("dispatch:errorbounce:totalTranslated")) {
				jobsCompletedWithErrors = property;
			}
		}
	}
	
	public void saveProperties() {
		setJobsQueued(getJobsQueued());
		setJobsStarted(getJobsStarted());
		setJobsCompleted(getJobsCompleted());
		setJobsCompletedWithErrors(getJobsCompletedWithErrors());
		jobsQueued = null;
		jobsStarted = null;
		jobsCompleted = null;
		jobsCompletedWithErrors = null;
		for (Entry<String, MonitorableProperty<?>> entry : propertyMap.entrySet()) {
			try {
				super.setProperty(entry.getKey(), entry.getValue().getValue());
			} catch (NoSuchPropertyException e) {
			}
		}
		propertyMap.clear();
	}

	@Override
	public int getJobsQueued() {
		if (jobsQueued != null) {
			try {
				return (Integer) jobsQueued.getValue();
			} catch (NoSuchPropertyException e) {
				return super.getJobsQueued();
			}
		}
		return super.getJobsQueued();
	}

	@Override
	public int getJobsStarted() {
		if (jobsStarted != null) {
			try {
				return (Integer) jobsStarted.getValue();
			} catch (NoSuchPropertyException e) {
				return super.getJobsStarted();
			}
		}
		return super.getJobsStarted();
	}

	@Override
	public int getJobsCompleted() {
		if (jobsCompleted != null) {
			try {
				return (Integer) jobsCompleted.getValue();
			} catch (NoSuchPropertyException e) {
				return super.getJobsCompleted();
			}
		}
		return super.getJobsCompleted();
	}

	@Override
	public int getJobsCompletedWithErrors() {
		if (jobsCompletedWithErrors != null) {
			try {
				return (Integer) jobsCompletedWithErrors.getValue();
			} catch (NoSuchPropertyException e) {
				return super.getJobsCompletedWithErrors();
			}
		}
		return super.getJobsCompletedWithErrors();
	}

	public Set<String> getPropertyKeys() {
		if (!propertyMap.isEmpty()) {
			return new HashSet<String>(propertyMap.keySet());
		}
		return super.getPropertyKeys();
	}
	
	public Object getProperty(String key) {
		if (propertyMap.containsKey(key)) {
			return propertyMap.get(key);
		}
		return super.getProperty(key);
	}
	
	public void setProperty(String key, Object value) {
		MonitorableProperty<?> monitorableProperty = propertyMap.get(key);
		if (monitorableProperty instanceof SteerableProperty<?>) {
			SteerableProperty<Object> steerableProperty = (SteerableProperty<Object>) monitorableProperty;
			try {
				 steerableProperty.setProperty(value);
			} catch (NoSuchPropertyException e) {
				super.setProperty(key, value);
			}
		} else {
			super.setProperty(key, value);
		}
	}
	
	private String getPropertyName(MonitorableProperty<?> property) {
		StringBuilder sb = new StringBuilder();
		String[] name = property.getName();
		for (int i = 0; i < name.length; i++) {
			if (i > 0) {
				sb.append(':');
			}
			sb.append(name[i]);
		}
		return sb.toString();
	}

}
