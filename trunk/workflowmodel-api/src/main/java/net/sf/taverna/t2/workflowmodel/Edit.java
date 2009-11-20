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
package net.sf.taverna.t2.workflowmodel;

/**
 * The workflow object model exposed by this API is read only. Properties of the
 * model can only be changed through implementations of this interface, this
 * ensures a consistant approach to grouped edits (transactions) and undo / redo
 * support within the UI. It also potentially allows for capture of editing
 * provenance where a workflow is repurposed or created from an aggregate of
 * several others.
 * 
 * @author Tom Oinn
 * 
 */
public interface Edit<TargetType> {

	/**
	 * Perform the edit
	 * 
	 * @throws EditException
	 *             if the edit fails. If an edit throws EditException it should
	 *             try to ensure the subject is unaltered. Where this is
	 *             impossible consider breaking edits down into a compound edit.
	 */
	public TargetType doEdit() throws EditException;

	/**
	 * Undo the edit, reverting the subject to the state it was in prior to the
	 * edit
	 */
	public void undo();

	/**
	 * Return the object to which this edit applies
	 * 
	 * @return
	 */
	public Object getSubject();

	/**
	 * Has the edit been applied yet?
	 * 
	 * @return true if and only if the edit has been successfully applied to the
	 *         subject
	 */
	public boolean isApplied();

}
