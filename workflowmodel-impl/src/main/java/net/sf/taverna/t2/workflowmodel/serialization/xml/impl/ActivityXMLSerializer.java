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
package net.sf.taverna.t2.workflowmodel.serialization.xml.impl;

import java.io.IOException;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.jdom.Element;
import org.jdom.JDOMException;

public class ActivityXMLSerializer extends AbstractXMLSerializer {
	private static ActivityXMLSerializer instance = new ActivityXMLSerializer();

	public static ActivityXMLSerializer getInstance() {
		return instance;
	}
	
	public Element activityToXML(Activity<?> activity) throws JDOMException,
	IOException {
		return activityToXML(activity, activity, activity.getConfiguration());
	}

	public Element activityToXML(Activity<?> activity, Activity<?> describingActivity, Object config) throws JDOMException,
			IOException {
		Element activityElem = new Element(ACTIVITY, T2_WORKFLOW_NAMESPACE);

//		ClassLoader cl = activity.getClass().getClassLoader();
//		if (cl instanceof LocalArtifactClassLoader) {
//			activityElem
//					.addContent(ravenElement((LocalArtifactClassLoader) cl));
//		}
		Element classNameElement = new Element(CLASS, T2_WORKFLOW_NAMESPACE);
		classNameElement.setText(activity.getClass().getName());
		activityElem.addContent(classNameElement);

		// Write out the mappings (processor input -> activity input, activity
		// output -> processor output)
		Element ipElement = new Element(INPUT_MAP, T2_WORKFLOW_NAMESPACE);
		for (String processorInputName : describingActivity.getInputPortMapping()
				.keySet()) {
			Element mapElement = new Element(MAP, T2_WORKFLOW_NAMESPACE);
			mapElement.setAttribute(FROM, processorInputName);
			mapElement.setAttribute(TO, describingActivity.getInputPortMapping().get(
					processorInputName));
			ipElement.addContent(mapElement);
		}
		activityElem.addContent(ipElement);

		Element opElement = new Element(OUTPUT_MAP, T2_WORKFLOW_NAMESPACE);
		for (String activityOutputName : describingActivity.getOutputPortMapping()
				.keySet()) {
			Element mapElement = new Element(MAP, T2_WORKFLOW_NAMESPACE);
			mapElement.setAttribute(FROM, activityOutputName);
			mapElement.setAttribute(TO, describingActivity.getOutputPortMapping().get(
					activityOutputName));
			opElement.addContent(mapElement);
		}
		activityElem.addContent(opElement);

		Element configElement = beanAsElement(config);
		activityElem.addContent(configElement);

		// annotations
		activityElem.addContent(annotationsToXML(activity));
		
		return activityElem;
	}

}
