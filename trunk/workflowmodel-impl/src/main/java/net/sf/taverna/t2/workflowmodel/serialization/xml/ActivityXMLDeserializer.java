/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.serialization.xml;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.apache.log4j.Logger;
import org.jdom.Element;

public class ActivityXMLDeserializer extends AbstractXMLDeserializer {
	private static ActivityXMLDeserializer instance = new ActivityXMLDeserializer();

	private static Logger logger = Logger.getLogger(ActivityXMLDeserializer.class);
	
	public static ActivityXMLDeserializer getInstance() {
		return instance;
	}

	protected ActivityXMLDeserializer() {

	}

	@SuppressWarnings("unchecked")
	public Activity<?> deserializeActivity(Element element,
			Map<String, Element> innerDataflowElements, ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, EditException, DeserializationException, ActivityConfigurationException {
		Element ravenElement = element.getChild(RAVEN, T2_WORKFLOW_NAMESPACE);
		ClassLoader cl = classLoader;
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		if (ravenElement != null) {
			try {
				cl = getRavenLoader(ravenElement);
			} catch (Exception ex) {
				logger.warn("Could not load raven classloader " + ravenElement + " for activity", ex);
				// TODO - handle this properly, either by logging correctly or
				// by going back to the repository and attempting to fetch the
				// offending missing artifacts
			}
		}
		String className = element.getChild(CLASS, T2_WORKFLOW_NAMESPACE)
				.getTextTrim();
		Class<? extends Activity> c = (Class<? extends Activity>) cl
				.loadClass(className);
		Activity<Object> activity = c.newInstance();
		
		// Handle the configuration of the activity
		Element configElement = element.getChild(CONFIG_BEAN,
				T2_WORKFLOW_NAMESPACE);
		Object configObject=null;
		if (DATAFLOW_ENCODING.equals(configElement.getAttributeValue(BEAN_ENCODING))) {
			String ref = configElement.getChild(DATAFLOW,T2_WORKFLOW_NAMESPACE).getAttributeValue(DATAFLOW_REFERENCE);
			configObject = resolveDataflowReference(ref,innerDataflowElements);
		}
		else {
			configObject = createBean(configElement, cl);
		}
		activity.configure(configObject);

		//port mappings
		Element ipElement = element.getChild(INPUT_MAP, T2_WORKFLOW_NAMESPACE);
		for (Element mapElement : (List<Element>) (ipElement.getChildren(MAP,
				T2_WORKFLOW_NAMESPACE))) {
			String processorInputName = mapElement.getAttributeValue(FROM);
			String activityInputName = mapElement.getAttributeValue(TO);
			activity.getInputPortMapping().put(processorInputName,
					activityInputName);
		}

		Element opElement = element.getChild(OUTPUT_MAP, T2_WORKFLOW_NAMESPACE);
		for (Element mapElement : (List<Element>) (opElement.getChildren(MAP,
				T2_WORKFLOW_NAMESPACE))) {
			String activityOutputName = mapElement.getAttributeValue(FROM);
			String processorOutputName = mapElement.getAttributeValue(TO);
			activity.getOutputPortMapping().put(activityOutputName,
					processorOutputName);
		}

		annotationsFromXml(activity, element, activity.getClass().getClassLoader());
		return activity;
	}

	public Activity<?> deserializeActivity(Element element,Map<String,Element> innerDataflowElements)
			throws ActivityConfigurationException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, EditException, DeserializationException {
		return deserializeActivity(element, innerDataflowElements, null);
	}

	private Object resolveDataflowReference(String ref,
			Map<String, Element> innerDataflowElements) throws EditException, DeserializationException, ActivityConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Element dfElement = innerDataflowElements.get(ref);
		return DataflowXMLDeserializer.getInstance().deserializeDataflow(dfElement, innerDataflowElements);
	}

	
}
