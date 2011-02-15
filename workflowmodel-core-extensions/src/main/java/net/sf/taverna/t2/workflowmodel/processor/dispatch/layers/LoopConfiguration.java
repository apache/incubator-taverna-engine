package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import java.util.Properties;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

/**
 * Configuration bean for the {@link Loop}.
 * <p>
 * Set the {@link #setCondition(Activity)} for an activity with an output port
 * called "loop". The LoopLayer will re-send a job only if this port exist and
 * it's output can be dereferenced to a string equal to "true".
 * </p>
 * <p>
 * If {@link #isRunFirst()} is false, the loop layer will check the condition
 * before invoking the job for the first time, otherwise the condition will be
 * invoked after the job has come back with successful results.
 * </p>
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class LoopConfiguration implements Cloneable {

	transient private static Logger logger = Logger
			.getLogger(LoopConfiguration.class);

	private Activity<?> condition = null;
	private Boolean runFirst;
	private Properties properties;

	public Properties getProperties() {
		synchronized (this) {
			if (properties == null) {
				properties = new Properties();
			}
		}
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public LoopConfiguration clone() {
		LoopConfiguration clone;
		try {
			clone = (LoopConfiguration) super.clone();
			clone.condition = null;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected CloneNotSupportedException",
					e);
		}
		return clone;
	}

	public Activity<?> getCondition() {
		return condition;
	}

	public boolean isRunFirst() {
		if (runFirst == null) {
			return true;
		}
		return runFirst;
	}

	public void setCondition(Activity<?> activity) {
		this.condition = activity;
	}

	public void setRunFirst(boolean runFirst) {
		this.runFirst = runFirst;
	}

}
