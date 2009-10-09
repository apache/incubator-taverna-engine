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
 * @author paolo
 * bean to hold results from an SQL query to VarBinding join Var
 */
public class Dependencies {

	final public static String COLL_TYPE = "referenceSetCollection";
	final public static String ATOM_TYPE = "referenceSet";
	
	boolean printResolvedValue;

	private List<LineageQueryResultRecord> records = new ArrayList<LineageQueryResultRecord>();

	public ListIterator<LineageQueryResultRecord> iterator() { return getRecords().listIterator(); }

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

	public void setRecords(List<LineageQueryResultRecord> records) {
		this.records = records;
	}

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
	 * @return the printResolvedValue
	 */
	public boolean isPrintResolvedValue() {
		return printResolvedValue;
	}

	/**
	 * @param printResolvedValue the printResolvedValue to set
	 */
	public void setPrintResolvedValue(boolean printResolvedValue) {
		this.printResolvedValue = printResolvedValue;
	}
}

