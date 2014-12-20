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
package net.sf.taverna.t2.provenance.item;

import java.sql.Timestamp;

import net.sf.taverna.t2.facade.WorkflowInstanceFacade.State;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;

/**
 * Informs the {@link ProvenanceConnector} that a workflow has been run to
 * completion. If a {@link ProvenanceConnector} receives this event then it
 * means that there are no further events to come and that the workflow has been
 * enacted to completion
 * 
 * @author Ian Dunlop
 */
public class DataflowRunComplete extends AbstractProvenanceItem {
	private SharedVocabulary eventType = SharedVocabulary.END_WORKFLOW_EVENT_TYPE;
	private Timestamp invocationEnded;
	private State state;

	public Timestamp getInvocationEnded() {
		return invocationEnded;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public void setInvocationEnded(Timestamp invocationEnded) {
		this.invocationEnded = invocationEnded;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}
}
