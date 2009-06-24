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
package net.sf.taverna.t2.monitor;

import java.util.Date;

/**
 * A single readable property contained by a Monitorable. This is used to
 * express properties that are dynamic with respect to workflow invocation as
 * opposed to static properties defined by the workflow model. A typical example
 * of this might be dispatch stack queue size or number of jobs completed. All
 * properties are defined relative to a particular owning process identifier,
 * this is the same mechanism as used in the workflow model to isolate different
 * data streams.
 * 
 * @author Tom Oinn
 * 
 */
public interface MonitorableProperty<T> {

	/**
	 * Return the value of this property
	 */
	public T getValue() throws NoSuchPropertyException;

	/**
	 * Return the name of this property, names are heirarchical in nature and
	 * are defined as an array of String objects. This is to allow e.g. dispatch
	 * layers to expose a set of related properties under the same root name.
	 */
	public String[] getName();

	/**
	 * Get the last update date for this property, if the property is immutable
	 * then this should be set to the date at which the implementation is
	 * created.
	 */
	public Date getLastModified();

}
