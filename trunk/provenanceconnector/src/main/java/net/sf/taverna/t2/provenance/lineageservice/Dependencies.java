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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Paolo Missier<p/>
 * Java bean to hold a list of dependencies, all of them associated with a pair (&lt;port name>, &lt;path>), as described in {@link net.sf.taverna.t2.provenance.api.QueryAnswer}.
 * <br/> Each element of the list is a record of type {@link LineageQueryResultRecord} in the provenance DB at the finest possible level of granularity, i.e., 
 * a single value possibly within a collection, bound to a processor port and associated to a specific run of a specific workflow.
 * @see LineageQueryResultRecord   
 */
public class Dependencies {

	final public static String COLL_TYPE = "referenceSetCollection";
	final public static String ATOM_TYPE = "referenceSet";
	
	boolean printResolvedValue;

	private List<LineageQueryResultRecord> records = new ArrayList<LineageQueryResultRecord>();

	public ListIterator<LineageQueryResultRecord> iterator() { return getRecords().listIterator(); }

	/**
	 * adds a single record to the list of dependencies
	 * @param wfNameRef
	 * @param pname
	 * @param vname
	 * @param wfInstance
	 * @param iteration
	 * @param collIdRef
	 * @param parentCollIDRef
	 * @param value
	 * @param resolvedValue
	 * @param type
	 * @param isInput
	 * @param isCollection
	 */
	public void addLineageQueryResultRecord(
			String wfNameRef,
			String pname,
			String vname,
			String wfInstance,
			String iteration,
			String collIdRef,
			String parentCollIDRef,
			String value,
			String resolvedValue,
			String type, boolean isInput, boolean isCollection) {

		LineageQueryResultRecord record = new LineageQueryResultRecord();

		record.setWfName(wfNameRef);
		record.setWfInstance(wfInstance);
		record.setPname(pname);
		record.setValue(value);
		record.setVname(vname);
		record.setIteration(iteration);
		record.setResolvedValue(resolvedValue);
		record.setInput(isInput);
		record.setCollIdRef(collIdRef);
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


	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (LineageQueryResultRecord record:getRecords()) {
			
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

