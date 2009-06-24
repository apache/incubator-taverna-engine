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

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.serialization.SerializationException;

import org.jdom.Element;

/**
 * Implementation of the XML serialisation framework for serialising a dataflow instance into a jdom XML element.
 * <br>
 * 
 * @author Stuart Owen
 *
 */
public class XMLSerializerImpl implements XMLSerializer, XMLSerializationConstants {
	
	public XMLSerializerImpl() {
		
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializer#serializeDataflow(net.sf.taverna.t2.workflowmodel.Dataflow)
	 */
	public Element serializeDataflow(Dataflow dataflow)
			throws SerializationException {
		List<Dataflow> innerDataflows = new ArrayList<Dataflow>();
		
		gatherDataflows(dataflow,innerDataflows);
		
		Element result = new Element(WORKFLOW, T2_WORKFLOW_NAMESPACE);
		// For future use
		result.setAttribute(WORKFLOW_VERSION, "1");
		result.setAttribute(PRODUCED_BY, this.getProducedBy());
		Element dataflowElement = DataflowXMLSerializer.getInstance().serializeDataflow(dataflow);
		dataflowElement.setAttribute(DATAFLOW_ROLE, DATAFLOW_ROLE_TOP);
		result.addContent(dataflowElement);
		
		for (Dataflow innerDataflow : innerDataflows) {
			Element innerDataflowElement = DataflowXMLSerializer.getInstance().serializeDataflow(innerDataflow);
			innerDataflowElement.setAttribute(DATAFLOW_ROLE,DATAFLOW_ROLE_NESTED);
			result.addContent(innerDataflowElement);
		}

		return result;
	}

	private void gatherDataflows(Dataflow dataflow,
			List<Dataflow> innerDataflows) {
		for (Processor p : dataflow.getProcessors()) {
			for (Activity<?> a : p.getActivityList()) {
				if (a.getConfiguration() instanceof Dataflow) {
					Dataflow df = (Dataflow) a.getConfiguration();
					if (!innerDataflows.contains(df)) {
						innerDataflows.add(df);
						gatherDataflows(df, innerDataflows);	
					}
				}
			}
		}
		
	}

	private String producedBy = UNSPECIFIED;

	public void setProducedBy(String producedBy) {
		this.producedBy = producedBy;
	}
	
	public String getProducedBy() {
		return this.producedBy;
	}


}
