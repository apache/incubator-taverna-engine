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
package org.apache.taverna.provenance.lineageservice;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


/**
 * encapsulates an SQL query along with directives on how to interpret the
 * results, i.e., which elements of the select clause are to be considered
 * relevant. For instance when the query includes a join with Collection, the
 * intent is that lineage should return the collection itself as opposed to any
 * of its elements.
 * 
 * @author paolo
 * 
 */
public class LineageSQLQuery {
	private String vbQuery = null;
	private String collQuery = null;

	/** =0 => use var values, >0 => use enclosing collection */
	private int depth = 0;

	/**
	 * @return the depth
	 */
	public int getdepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setdepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return the collQuery
	 */
	public String getCollQuery() {
		return collQuery;
	}

	/**
	 * @param collQuery the collQuery to set
	 */
	public void setCollQuery(String collQuery) {
		this.collQuery = collQuery;
	}

	/**
	 * @return the vbQuery
	 */
	public String getVbQuery() {
		return vbQuery;
	}

	/**
	 * @param vbQuery the vbQuery to set
	 */
	public void setVbQuery(String vbQuery) {
		this.vbQuery = vbQuery;
	}	
}
