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

public class LineageQueryResultRecord {

	String pname;
	String vname;
	String wfInstance;
	String iteration;
	String value;     // atomic or XML-formatted collection -- this is actually a reference to the value...
	String resolvedValue;
	String type;  // one of referenceSet, referenceSetCollection
	boolean printResolvedValue;
	boolean isInput; 

	public String toString() {

		if (printResolvedValue)
			return "proc "+getPname()+
			" var "+getVname()+" " +
			"iteration: "+getIteration()+
			" value: "+getValue()+
			" resolvedValue: "+getResolvedValue();
		else  
			return "proc "+getPname()+
			" var "+getVname()+" " +
			"iteration: "+getIteration()+
			" value: "+getValue();
	}


	/**
	 * @return the pname
	 */
	public String getPname() {
		return pname;
	}
	/**
	 * @param pname the pname to set
	 */
	public void setPname(String pname) {
		this.pname = pname;
	}
	/**
	 * @return the vname
	 */
	public String getVname() {
		return vname;
	}
	/**
	 * @param vname the vname to set
	 */
	public void setVname(String vname) {
		this.vname = vname;
	}
	/**
	 * @return the wfInstance
	 */
	public String getWfInstance() {
		return wfInstance;
	}
	/**
	 * @param wfInstance the wfInstance to set
	 */
	public void setWfInstance(String wfInstance) {
		this.wfInstance = wfInstance;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}
	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	/**
	 * @return the resolvedValue
	 */
	public String getResolvedValue() {
		return resolvedValue;
	}
	/**
	 * @param resolvedValue the resolvedValue to set
	 */
	public void setResolvedValue(String resolvedValue) {
		this.resolvedValue = resolvedValue;
	}


	public void setPrintResolvedValue(boolean b) {
		this.printResolvedValue = b
		;	}


	/**
	 * @return the isInput
	 */
	public boolean isInput() {
		return isInput;
	}


	/**
	 * @param isInput the isInput to set
	 */
	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}

}