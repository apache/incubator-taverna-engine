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
package net.sf.taverna.t2.reference;

/**
 * Used by the asynchronous form of the resolveIdentifier method in
 * ReferenceService
 * 
 * @author Tom Oinn
 * 
 */
public interface ReferenceServiceResolutionCallback {

	/**
	 * Called when the resolution process has completed
	 * 
	 * @param result
	 *            the Identified that corresponds to the T2Reference specified
	 *            in the call to resolveIdentifierAsynch in ReferenceService
	 */
	public void identifierResolved(Identified result);

	/**
	 * Called when the resolution process has failed
	 * 
	 * @param cause
	 *            a ReferenceServiceException describing the failure
	 */
	public void resolutionFailed(ReferenceServiceException cause);

}
