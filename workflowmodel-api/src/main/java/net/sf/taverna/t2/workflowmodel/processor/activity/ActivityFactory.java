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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.net.URI;

/**
 * Factory for creating {@link Activity} instances.
 * 
 * @author David Withers
 */
public interface ActivityFactory {

	/**
	 * Creates a new <code>Activity</code>.
	 * 
	 * @return a new <code>Activity</code>
	 */
	public Activity<?> createActivity();

	/**
	 * Creates a new activity configuration.
	 * 
	 * @return a new activity configuration
	 */
	public Object createActivityConfiguration();

	/**
	 * Returns the URI that identifies the <code>Activity</code> type.
	 * 
	 * @return the URI that identifies the <code>Activity</code> type
	 */
	public URI getActivityURI();

}
