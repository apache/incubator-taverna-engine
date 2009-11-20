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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.events;

/**
 * A simple enumeration of possible failure classes, used to determine whether
 * fault handling dispatch layers should attempt to handle a given failure
 * message.
 * 
 * @author Tom Oinn
 * 
 */
public enum DispatchErrorType {

	/**
	 * Indicates that the failure to invoke the activity was due to invalid
	 * input data, in this case there is no point in trying to invoke the
	 * activity again with the same data as it will always fail. Fault handling
	 * layers such as retry should pass this error type through directly; layers
	 * such as failover handlers should handle it as the input data may be
	 * applicable to other activities within the processor.
	 */
	DATA,

	/**
	 * Indicates that the failure was related to the invocation of the resource
	 * rather than the input data, and that an identical invocation at a later
	 * time may succeed.
	 */
	INVOCATION,

	/**
	 * Indicates that the failure was due to missing or incorrect authentication
	 * credentials and that retrying the activity invocation without modifying
	 * the credential set is pointless.
	 */
	AUTHENTICATION;

}
