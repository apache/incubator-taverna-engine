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
package net.sf.taverna.t2.provenance.lineageservice;

/**
 * encapsulates an SQL query along with directives on how to interpret the results, i.e., which elements of the select clause 
 * are to be considered relevant. For instance when the query includes a join with Collection, the intent is that lineage should
 * return the collection itself as opposed to any of its elements.  
 * @author paolo
 *
 */
public class LineageSQLQuery {

	String SQLQuery = null;
	
	int nestingLevel = 0;  // =0 => use var values, >0 => use enclosing collection

	/**
	 * @return the sQLQuery
	 */
	public String getSQLQuery() {
		return SQLQuery;
	}

	/**
	 * @param query the sQLQuery to set
	 */
	public void setSQLQuery(String query) {
		SQLQuery = query;
	}

	/**
	 * @return the nestingLevel
	 */
	public int getNestingLevel() {
		return nestingLevel;
	}

	/**
	 * @param nestingLevel the nestingLevel to set
	 */
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	
}
