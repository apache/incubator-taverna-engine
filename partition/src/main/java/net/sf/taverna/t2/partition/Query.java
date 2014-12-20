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
package net.sf.taverna.t2.partition;

import java.util.Date;

/**
 * Defines a query which can be re-run and which presents a set view on its
 * results. The Query is intended to represent both the old Taverna scavenger
 * class (which were queries in all but name) and new integration with external
 * search-able repositories in which case the term 'query' is a more literal
 * description.
 * 
 * @author Tom Oinn
 * 
 * @param <ItemType>
 *            the parameterised type of the result set of the query
 */
public interface Query<ItemType> extends SetModel<ItemType> {

	/**
	 * Run the query. The query has internal state from any previous runs
	 * (including the initial empty state) and will notify all listeners from
	 * the SetModel interface of any items that are present in the new query
	 * result and not in the old state or vice versa. It also updates the query
	 * time to be the current time.
	 */
	public void doQuery();

	/**
	 * Returns the time at which the query was last invoked, or null if the
	 * query has not been invoked yet.
	 * 
	 * @return time of last call to doQuery or null if this hasn't happened yet.
	 */
	public Date getLastQueryTime();

}
