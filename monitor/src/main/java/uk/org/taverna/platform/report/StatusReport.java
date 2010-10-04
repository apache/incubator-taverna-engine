/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author David Withers
 */
public class StatusReport {

	private State state;

	private Date createdDate, startedDate, pausedDate, resumedDate, cancelledDate, completedDate;

	private List<Date> pausedDates, resumedDates;

	public StatusReport() {
		pausedDates = new ArrayList<Date>();
		resumedDates = new ArrayList<Date>();
		setCreatedDate(new Date());
	}

	/**
	 * Returns the current state. A state can be CREATED, RUNNING, COMPLETED,
	 * PAUSED or CANCELLED.
	 * 
	 * @return the current state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the date that the run was created.
	 * 
	 * @return the the date that the run was created
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the date that the run was created.
	 * 
	 * @param createdDate
	 *            the date that the run was created
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
		state = State.CREATED;
	}

	/**
	 * Returns the date that the run was started. If the run has not been
	 * started <code>null</code> is returned.
	 * 
	 * @return the date that the run was started
	 */
	public Date getStartedDate() {
		return startedDate;
	}

	/**
	 * Sets the date that the run was started.
	 * 
	 * @param startedDate
	 *            the date that the run was started
	 */
	public void setStartedDate(Date startedDate) {
		if (this.startedDate == null) {
			this.startedDate = startedDate;
		}
		state = State.RUNNING;
	}

	/**
	 * Returns the date that the run was last paused. If the run has never been
	 * paused <code>null</code> is returned.
	 * 
	 * @return the date that the run was last paused
	 */
	public Date getPausedDate() {
		return pausedDate;
	}

	/**
	 * Sets the date that the run was last paused.
	 * 
	 * @param pausedDate
	 *            the date that the run was last paused
	 */
	public void setPausedDate(Date pausedDate) {
		this.pausedDate = pausedDate;
		pausedDates.add(pausedDate);
		state = State.PAUSED;
	}

	/**
	 * Returns the date that the run was last resumed. If the run has never been
	 * resumed <code>null</code> is returned.
	 * 
	 * @return the date that the run was last resumed
	 */
	public Date getResumedDate() {
		return resumedDate;
	}

	/**
	 * Sets the date that the run was last resumed.
	 * 
	 * @param resumedDate
	 *            the date that the run was last resumed
	 */
	public void setResumedDate(Date resumedDate) {
		this.resumedDate = resumedDate;
		resumedDates.add(resumedDate);
		state = State.RUNNING;
	}

	/**
	 * Returns the date that the run was canceled. If the run has not been
	 * canceled <code>null</code> is returned.
	 * 
	 * @return the date that the run was canceled
	 */
	public Date getCancelledDate() {
		return cancelledDate;
	}

	/**
	 * Sets the date that the run was canceled.
	 * 
	 * @param cancelledDate
	 *            the date that the run was canceled
	 */
	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
		state = State.CANCELLED;
	}

	/**
	 * Returns the date that the run completed. If the run has not completed
	 * <code>null</code> is returned.
	 * 
	 * @return the date that the run completed
	 */
	public Date getCompletedDate() {
		return completedDate;
	}

	/**
	 * Sets the date that the run completed.
	 * 
	 * @param completedDate
	 *            the date that the run completed
	 */
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
		state = State.COMPLETED;
	}

	/**
	 * Returns the dates that the run was paused. If the run has never been
	 * paused an empty list is returned.
	 * 
	 * @return the dates that the run was paused
	 */
	public List<Date> getPausedDates() {
		return pausedDates;
	}

	/**
	 * Returns the dates that the run was resumed. If the run has never been
	 * resumed an empty list is returned.
	 * 
	 * @return the resumedDates
	 */
	public List<Date> getResumedDates() {
		return resumedDates;
	}

}