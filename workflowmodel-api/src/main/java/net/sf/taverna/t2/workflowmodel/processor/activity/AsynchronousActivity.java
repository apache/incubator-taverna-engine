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

import net.sf.taverna.t2.reference.T2Reference;

/**
 * A concrete invokable activity with an asynchronous invocation API and no
 * knowledge of invocation context. This is the most common concrete activity
 * type in Taverna 2, it has no knowledge of any enclosing iteration or other
 * handling process. The activity may stream results in the sense that it can use
 * the AsynchronousActivityCallback object to push multiple results followed by a
 * completion event. If a completion event is received by the callback before
 * any data events the callback will insert a data event containing empty
 * collections of the appropriate depth.
 * 
 * @param <ConfigurationType> the ConfigurationType associated with the Activity.
 * @author Tom Oinn
 * 
 */
public interface AsynchronousActivity<ConfigurationType> extends Activity<ConfigurationType> {

	/**
	 * Invoke the activity in an asynchronous manner. The activity uses the
	 * specified ActivityCallback object to push results, errors and completion
	 * events back to the dispatch stack.
	 */
	public void executeAsynch(Map<String, T2Reference> data,
			AsynchronousActivityCallback callback);

}
