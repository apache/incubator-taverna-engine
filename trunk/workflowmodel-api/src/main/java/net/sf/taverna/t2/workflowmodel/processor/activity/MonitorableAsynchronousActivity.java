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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * An extension of AsynchronousActivity with the additional stipulation that
 * implementing classes must return a set of monitorable properties for the
 * activity invocation instance when invoked. This allows for deep state
 * management, where the monitor state extends out from the workflow engine into
 * the remote resources themselves and is dependant on the resource proxied by
 * the activity implementation providing this information.
 * 
 * @author Tom Oinn
 * 
 */
public interface MonitorableAsynchronousActivity<ConfigType> extends
		AsynchronousActivity<ConfigType> {

	/**
	 * This has the same invocation semantics as
	 * AsynchronousActivity.executeAsynch and all implementations should also
	 * implement that method, with the difference that this one returns
	 * immediately with a set of monitorable properties which represent
	 * monitorable or steerable state within the invocation itself.
	 * 
	 * @param data
	 * @param callback
	 * @return a set of monitorable properties representing internal state of
	 *         the invoked resource
	 */
	public Set<MonitorableProperty<?>> executeAsynchWithMonitoring(
			Map<String, T2Reference> data,
			AsynchronousActivityCallback callback);

}
