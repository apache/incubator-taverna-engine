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

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.jdom.Element;

public class ConditionXMLDeserializer extends AbstractXMLDeserializer {
	private static ConditionXMLDeserializer instance = new ConditionXMLDeserializer();

	private ConditionXMLDeserializer() {

	}

	public static ConditionXMLDeserializer getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void buildConditions(Dataflow df, Element conditionsElement,
			Map<String, Processor> createdProcessors) throws DeserializationException, EditException {
		for (Element conditionElement : (List<Element>)conditionsElement.getChildren(CONDITION,T2_WORKFLOW_NAMESPACE)) {
			String control=conditionElement.getAttributeValue("control");
			String target=conditionElement.getAttributeValue("target");
			Processor controlProcessor=createdProcessors.get(control);
			Processor targetProcessor=createdProcessors.get(target);
			if (controlProcessor==null) throw new DeserializationException("Unable to find start service for control link, named:"+control);
			if (targetProcessor==null) throw new DeserializationException("Unable to find target service for control link, named:"+target);
			edits.getCreateConditionEdit(controlProcessor, targetProcessor).doEdit();
		}		
	}
}
