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
package net.sf.taverna.t2.provenance.lineageservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.item.IterationProvenanceItem;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.workflowmodel.Dataflow;

/**
 * Implemented by the database class that a {@link ProvenanceConnector}
 * implementation uses for storage purposes
 * 
 * @author Paolo Missier
 * @author Ian Dunlop
 * 
 */
//FIXME is this class really needed. Can't we just push the
//acceptRawProvanceEvent up into the ProvenanceConnector?
public class Provenance {

	private static Logger logger = Logger.getLogger(Provenance.class);

	protected ProvenanceQuery pq;
	protected ProvenanceWriter pw;
	protected EventProcessor ep;

	private String saveEvents;
	
	private boolean isfirstWorkflowStructure = true;

	private List<String> workflowIDStack = new ArrayList<String>();
	
	private Map<String, String> workflowIDMap = new HashMap<String, String>(); 

	public Provenance() {	}

	public Provenance(EventProcessor eventProcessor) {
		this.ep = eventProcessor;
		this.pq = ep.getPq();
		this.pw = ep.getPw();		
	}

	public void clearDB() throws SQLException {
		getPw().clearDBStatic();
		getPw().clearDBDynamic();
	}
	

	/**
	 * @return the saveEvents
	 */
	public String getSaveEvents() {
		return saveEvents;
	}

	/**
	 * @param saveEvents
	 *            the saveEvents to set
	 */
	public void setSaveEvents(String saveEvents) {
		this.saveEvents = saveEvents;
	}

	// FIXME I think the provenance query and writer should both come from the
	// EventProcessor
	// seems silly setting the ep, pq and pw separately.
	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}

	public ProvenanceQuery getPq() {
		return pq;
	}

	public void setPw(ProvenanceWriter pw) {
		this.pw = pw;
	}

	public ProvenanceWriter getPw() {
		return pw;
	}

	public void setEp(EventProcessor ep) {
		this.ep = ep;
	}

	public EventProcessor getEp() {
		return ep;
	}

	/**
	 * maps each incoming event to an insert query into the provenance store
	 * 
	 * @param eventType
	 * @param content
	 * @throws SQLException
	 * @throws IOException
	 */
	public void acceptRawProvenanceEvent(SharedVocabulary eventType,
			ProvenanceItem provenanceItem) throws SQLException, IOException {

		processEvent(provenanceItem, eventType);
	}

	/**
	 * parse d and generate SQL insert calls into the provenance DB
	 * 
	 * @param d
	 *            DOM for the event
	 * @param eventType
	 *            see {@link SharedVocabulary}
	 * @throws SQLException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void processEvent(ProvenanceItem provenanceItem,
			SharedVocabulary eventType) throws SQLException, IOException {

		// only attempt to save the data events, since the workflow itself may not be XMLEncode-able
		if (!eventType.equals(SharedVocabulary.WORKFLOW_EVENT_TYPE)) {

			// saveEvent for debugging / testing
			if (saveEvents != null && saveEvents.equals("all")) {

				getEp().saveEvent(provenanceItem, eventType);

			} else if (saveEvents != null && saveEvents.equals("iteration")) {

				if (eventType.equals("iteration"))
					getEp().saveEvent(provenanceItem, eventType);

			}
		}

		if (eventType.equals(SharedVocabulary.WORKFLOW_EVENT_TYPE)) {
			// process the workflow structure

			if (isfirstWorkflowStructure) {

				String dataflowId = ((WorkflowProvenanceItem) provenanceItem).getDataflow().getInternalIdentier();
				String instanceId = provenanceItem.getIdentifier();
				
				workflowIDMap.put(instanceId, dataflowId);
//				logger.debug("pushed workflowID "+dataflowId);
				
				isfirstWorkflowStructure = false;
//				logger.debug("processing event of type "
//						+ SharedVocabulary.WORKFLOW_EVENT_TYPE);
				workflowIDStack.add(0,getEp().processWorkflowStructure(provenanceItem));
				
//				logger.debug("pushed workflowID "+workflowIDStack.get(0));

				getEp().propagateANL(provenanceItem.getIdentifier());
			} else {
				
				String dataflowId = ((WorkflowProvenanceItem) provenanceItem).getDataflow().getInternalIdentier();
				String instanceId = provenanceItem.getIdentifier();
				
				workflowIDMap.put(instanceId, dataflowId);
//				logger.debug("pushed workflowID "+dataflowId);

				Dataflow df = ((WorkflowProvenanceItem)provenanceItem).getDataflow();
				workflowIDStack.add(0,df.getInternalIdentier());
			}

		} else if (provenanceItem.getEventType().equals(SharedVocabulary.END_WORKFLOW_EVENT_TYPE)) {

//			String currentWorkflowID = workflowIDStack.get(0);
//			workflowIDStack.remove(0);
			

			String currentWorkflowID = workflowIDMap.get(provenanceItem.getParentId());
			
//			logger.debug("popped workflowID "+currentWorkflowID);
			
			getEp().processProcessEvent(provenanceItem, currentWorkflowID);
			
		} else {  // all other event types (iteration etc.)
			
//			logger.debug("processEvent of type "+provenanceItem.getEventType()+" for item of type "+provenanceItem.getClass().getName());
			String currentWorkflowID = provenanceItem.getWorkflowId();
//			String currentWorkflowID = workflowIDMap.get(provenanceItem.getParentId());

//			logger.debug("setting currentWorkflowID to "+ currentWorkflowID);
			getEp().processProcessEvent(provenanceItem, currentWorkflowID);
		
//			getEp().processProcessEvent(provenanceItem, workflowIDStack.get(0));
		}

	}
}
