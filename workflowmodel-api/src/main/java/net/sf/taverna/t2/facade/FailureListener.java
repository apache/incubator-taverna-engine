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
package net.sf.taverna.t2.facade;

/**
 * Used to communicate a failure of the overall workflow to interested parties.
 * 
 * @author Tom Oinn
 */
public interface FailureListener {

	/**
	 * Called if the workflow fails in a critical and fundamental way. Most
	 * internal failures of individual process instances will not trigger this,
	 * being handled either by the per processor dispatch stack through retry,
	 * failover etc or by being converted into error tokens and injected
	 * directly into the data stream. This therefore denotes a catastrophic and
	 * unrecoverable problem.
	 * 
	 * @param message
	 * @param t
	 */
	public void workflowFailed(String message, Throwable t);

}
