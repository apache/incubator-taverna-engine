package uk.org.taverna.platform.execution.impl.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.monitor.NoSuchPropertyException;
import net.sf.taverna.t2.monitor.SteerableProperty;
import uk.org.taverna.platform.report.ProcessorReport;
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
