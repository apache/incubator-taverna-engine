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


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Java bean to hold a list of {@link LineageQueryResultRecord}, representing
 * one record in the provenance DB at the finest possible level of granularity,
 * i.e., a single value possibly within a collection, bound to a processor port
 * and associated to a specific run of a specific workflow.
 * 
 * @author Paolo Missier
 * @see LineageQueryResultRecord
 */
public class Dependencies {
	final public static String COLL_TYPE = "referenceSetCollection";
	final public static String ATOM_TYPE = "referenceSet";
	
	boolean printResolvedValue;

	private List<LineageQueryResultRecord> records = new ArrayList<>();

	public ListIterator<LineageQueryResultRecord> iterator() {
		return getRecords().listIterator();
	}

	/**
	 * adds a single record to the list of dependencies
	 * 
	 * @param workflowId
	 * @param pname
	 * @param vname
	 * @param workflowRun
	 * @param iteration
	 * @param collIdRef
	 * @param parentCollIDRef
	 * @param value
	 * @param resolvedValue
	 * @param type
	 * @param isInput
	 * @param isCollection
	 */
	public void addLineageQueryResultRecord(String workflowId, String pname,
			String vname, String workflowRun, String iteration,
			String collIdRef, String parentCollIDRef, String value,
			String resolvedValue, String type, boolean isInput,
			boolean isCollection) {

		LineageQueryResultRecord record = new LineageQueryResultRecord();

		record.setWorkflowId(workflowId);
		record.setWorkflowRunId(workflowRun);
		record.setProcessorName(pname);
		record.setValue(value);
		record.setPortName(vname);
		record.setIteration(iteration);
		record.setResolvedValue(resolvedValue);
		record.setIsInputPort(isInput);
		record.setCollectionT2Reference(collIdRef);
		record.setParentCollIDRef(parentCollIDRef);
		record.setCollection(isCollection);

		getRecords().add(record);
	}

	/**
	 * populates the bean with an entire list of {@link LineageQueryResultRecord} elements
	 * @param records
	 */
	public void setRecords(List<LineageQueryResultRecord> records) {
		this.records = records;
	}

	/**
	 * @return the entire set of records
	 */
	public List<LineageQueryResultRecord> getRecords() {
		return records;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (LineageQueryResultRecord record : getRecords()) {
			record.setPrintResolvedValue(printResolvedValue);
			sb.append("***  record: ****\n"+record.toString());
		}		
		return sb.toString();
	}

	/**
	 * @return true is the records contain the actual values, in addition to the URI references to the values
	 * <br/>NOT YET SUPPORTED. This switch is currently ignored and no values are returned in the current version 
	 */
	public boolean isPrintResolvedValue() {
		return printResolvedValue;
	}

	/**
	 * @param toggles insertion of values in addition to references to values in the records
	 * <br/>NOT YET SUPPORTED. This switch is currently ignored and no values are returned in the current version 
	 */
	public void setPrintResolvedValue(boolean printResolvedValue) {
		this.printResolvedValue = printResolvedValue;
	}
}
