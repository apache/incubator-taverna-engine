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
package net.sf.taverna.t2.annotation;

public class DisputeEvent implements CurationEvent<DisputeEventDetails>{
	
	private DisputeEventDetails disputeEventDetails;
	private CurationEventType curationEventType;
	private Curateable targetEvent;
	
	public DisputeEvent() {
		
	}
	
	
	public DisputeEvent(DisputeEventDetails disputeEventDetails, CurationEventType curationEventType, Curateable targetEvent) {
		this.disputeEventDetails = disputeEventDetails;
		this.curationEventType = curationEventType;
		this.targetEvent = targetEvent;
	}

	public DisputeEventDetails getDetail() {
		return disputeEventDetails;
	}

	public Curateable getTarget() {
		return targetEvent;
	}

	public CurationEventType getType() {
		return curationEventType;
	}


	public void setDisputeEventDetails(DisputeEventDetails disputeEventDetails) {
//		if (disputeEventDetails != null) {
//			throw new RuntimeException("Dispute event details have already been set");
//		}
		this.disputeEventDetails = disputeEventDetails;
	}


	public void setCurationEventType(CurationEventType curationEventType) {
//		if (curationEventType != null) {
//			throw new RuntimeException("Curation event details have already been set");
//		}
		this.curationEventType = curationEventType;
	}


	public void setTargetEvent(Curateable targetEvent) {
//		if (targetEvent!= null) {
//			throw new RuntimeException("Target event details have already been set");
//		}
		this.targetEvent = targetEvent;
	}

}
