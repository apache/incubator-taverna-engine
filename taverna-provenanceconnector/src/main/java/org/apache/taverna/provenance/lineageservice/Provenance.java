/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.taverna.provenance.connector.AbstractProvenanceConnector;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import org.apache.taverna.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * Implemented by the database class that a {@link AbstractProvenanceConnector}
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

	private volatile boolean firstWorkflowStructure = true;

	public boolean isFirstWorkflowStructure() {
		return firstWorkflowStructure;
	}

	public void setFirstWorkflowStructure(boolean firstWorkflowStructure) {
		this.firstWorkflowStructure = firstWorkflowStructure;
	}

	private List<String> workflowIDStack = Collections.synchronizedList(new ArrayList<String>());

	private Map<String, String> workflowIDMap = new ConcurrentHashMap<String, String>();

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
	protected void processEvent(ProvenanceItem provenanceItem,
			SharedVocabulary eventType) throws SQLException, IOException {
		if (eventType.equals(SharedVocabulary.WORKFLOW_EVENT_TYPE)) {
			// process the workflow structure
			//workflowStartedMap.put()
			WorkflowProvenanceItem workflowProvenanceItem = (WorkflowProvenanceItem) provenanceItem;

			getEp().getWfdp().workflowStarted.put(workflowProvenanceItem.getIdentifier(), workflowProvenanceItem.getInvocationStarted());
			if (isFirstWorkflowStructure()) {
				String dataflowId = workflowProvenanceItem.getDataflow().getIdentifier();
				String instanceId = provenanceItem.getIdentifier();

				workflowIDMap.put(instanceId, dataflowId);
				setFirstWorkflowStructure(false);
				String processWorkflowStructure = getEp().processWorkflowStructure(provenanceItem);
				synchronized(workflowIDStack) {
					workflowIDStack.add(0,processWorkflowStructure);
				}

				getEp().propagateANL(provenanceItem.getIdentifier());
			} else {
				String dataflowId = workflowProvenanceItem.getDataflow().getIdentifier();
				String instanceId = provenanceItem.getIdentifier();

				workflowIDMap.put(instanceId, dataflowId);

				Dataflow df = workflowProvenanceItem.getDataflow();
				synchronized(workflowIDStack) {
					workflowIDStack.add(0,df.getIdentifier());
				}
			}
		} else if (provenanceItem.getEventType().equals(SharedVocabulary.END_WORKFLOW_EVENT_TYPE)) {
//			String currentWorkflowID = workflowIDStack.get(0);
//			workflowIDStack.remove(0);
			String currentWorkflowID = provenanceItem.getParentId();

			getEp().processProcessEvent(provenanceItem, currentWorkflowID);

		} else {  // all other event types (iteration etc.)
			logger.debug("processEvent of type "+provenanceItem.getEventType()+" for item of type "+provenanceItem.getClass().getName());
			String currentWorkflowID = provenanceItem.getWorkflowId();
//			String currentWorkflowID = workflowIDMap.get(provenanceItem.getParentId());

			getEp().processProcessEvent(provenanceItem, currentWorkflowID);

//			getEp().processProcessEvent(provenanceItem, workflowIDStack.get(0));
		}
	}
}
