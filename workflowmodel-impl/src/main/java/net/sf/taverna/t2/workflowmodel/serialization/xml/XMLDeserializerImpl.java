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

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.jdom.Element;

/**
 * Implementation class that acts as the main entry point for deserialising a complete XML dataflow document into a dataflow instance.
 * @author Stuart Owen
 *
 */
public class XMLDeserializerImpl implements XMLDeserializer, XMLSerializationConstants {
	
	Edits edits = new EditsImpl();
	
	public XMLDeserializerImpl() {
		
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workflowmodel.serialization.xml.XMLDeserializer#deserializeDataflow(org.jdom.Element)
	 */
	public Dataflow deserializeDataflow(Element element)
			throws DeserializationException,EditException {
		Element topDataflow = findTopDataflow(element);
		if (topDataflow==null) throw new DeserializationException("No top level dataflow defined in the XML document");
		Map<String,Element> innerDataflowElements = gatherInnerDataflows(element);
		try {
			return DataflowXMLDeserializer.getInstance().deserializeDataflow(topDataflow,innerDataflowElements);
		} catch (Exception e) {
			throw new DeserializationException("An error occurred deserializing the dataflow:"+e.getMessage(),e);
		}
	}

	private Element findTopDataflow(Element element) {
		Element result = null;
		for (Object elObj : element.getChildren(DATAFLOW,T2_WORKFLOW_NAMESPACE)) {
			Element dataflowElement = (Element)elObj;
			if (DATAFLOW_ROLE_TOP.equals(dataflowElement.getAttribute(DATAFLOW_ROLE).getValue())) {
				result=dataflowElement;
			}
		}
		return result;
	}

	private Map<String, Element> gatherInnerDataflows(Element element) throws DeserializationException {
		Map<String,Element> result=new HashMap<String, Element>();
		for (Object elObj : element.getChildren(DATAFLOW,T2_WORKFLOW_NAMESPACE)) {
			Element dataflowElement = (Element)elObj;
			if (DATAFLOW_ROLE_NESTED.equals(dataflowElement.getAttribute(DATAFLOW_ROLE).getValue())) {
				String id = dataflowElement.getAttributeValue(DATAFLOW_ID);
				if (result.containsKey(id)) throw new DeserializationException("Duplicate dataflow id:"+id);
				result.put(id,dataflowElement);
			}
		}
		return result;
	}
}
