package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.serialization.xml.ActivityXMLDeserializer;
import net.sf.taverna.t2.workflowmodel.serialization.xml.ActivityXMLSerializer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

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

	/**
	 * Transient - will be stored as XML
	 */
	transient private Activity<?> condition = null;
	private String conditionXML;
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
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected CloneNotSupportedException",
					e);
		}
		return clone;
	}

	public Activity<?> getCondition() {
		SAXBuilder builder = new SAXBuilder();

		synchronized (this) {
			if (condition == null && conditionXML != null) {
				try {
					Document document = builder.build(new StringReader(
							conditionXML));
					Element conditionElement = document.getRootElement();
					ActivityXMLDeserializer deserializer = ActivityXMLDeserializer
							.getInstance();
					this.condition = deserializer.deserializeActivity(
							conditionElement, new HashMap<String, Element>());
				} catch (Exception e) {
					logger.warn("Can't deserialise conditional activity", e);
				}
			}
		}
		return condition;
	}

	public boolean isRunFirst() {
		if (runFirst == null) {
			return true;
		}
		return runFirst;
	}

	public void setCondition(Activity<?> activity) {
		ActivityXMLSerializer serializer = ActivityXMLSerializer.getInstance();

		this.condition = activity;

		try {
			if (condition != null) {
				Element conditionElement = serializer.activityToXML(activity);
				XMLOutputter xmlOutputter = new XMLOutputter();
				conditionXML = xmlOutputter.outputString(conditionElement);
			} else {
				conditionXML = null;
			}
		} catch (JDOMException e) {
			logger.warn("Can't serialise activity " + activity, e);
			conditionXML = null;
		} catch (IOException e) {
			logger.warn("Can't serialise activity " + activity, e);
			conditionXML = null;
		}
	}

	public void setRunFirst(boolean runFirst) {
		this.runFirst = runFirst;
	}

}
