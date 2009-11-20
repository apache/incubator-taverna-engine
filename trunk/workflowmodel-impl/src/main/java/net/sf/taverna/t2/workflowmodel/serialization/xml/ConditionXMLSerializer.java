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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.Condition;
import net.sf.taverna.t2.workflowmodel.Processor;

import org.jdom.Element;

public class ConditionXMLSerializer extends AbstractXMLSerializer {
	private static ConditionXMLSerializer instance = new ConditionXMLSerializer();

	private ConditionXMLSerializer() {

	}

	public static ConditionXMLSerializer getInstance() {
		return instance;
	}
	
	public Element conditionsToXML(List<? extends Processor> processors) {
		Element result = new Element(CONDITIONS, T2_WORKFLOW_NAMESPACE);

		// gather conditions
		Set<Condition> conditions = new HashSet<Condition>();
		for (Processor p : processors) {
			for (Condition c : p.getControlledPreconditionList()) {
				conditions.add(c);
			}
		}
		for (Condition c : conditions) {
			Element conditionElement = new Element(CONDITION,
					T2_WORKFLOW_NAMESPACE);
			conditionElement.setAttribute("control", c.getControl()
					.getLocalName());
			conditionElement.setAttribute("target", c.getTarget()
					.getLocalName());
			result.addContent(conditionElement);
		}
		return result;
	}
}
