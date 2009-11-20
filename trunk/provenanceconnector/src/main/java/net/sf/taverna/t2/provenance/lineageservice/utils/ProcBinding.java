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
package net.sf.taverna.t2.provenance.lineageservice.utils;



/**
 * @author Paolo Missier
 *
 */
public class ProcBinding {
	private String identifier;

	private String pNameRef;
	private String execIDRef;
	private String wfNameRef;
	private String actName;
	private String iterationVector;
	
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("**** ProcBinding: \n").
			append("execIDRef = "+execIDRef+"\n").
			append("wfNameRef = "+wfNameRef+"\n").
			append("PNameRef = "+pNameRef+"\n").
			append("actName = "+actName+"\n").
			append("iteration = "+iterationVector+"\n");

		return sb.toString();
	}
	
	/**
	 * @return the pNameRef
	 */
	public String getPNameRef() {
		return pNameRef;
	}
	/**
	 * @param nameRef the pNameRef to set
	 */
	public void setPNameRef(String nameRef) {
		pNameRef = nameRef;
	}
	/**
	 * @return the execIDRef
	 */
	public String getExecIDRef() {
		return execIDRef;
	}
	/**
	 * @param execIDRef the execIDRef to set
	 */
	public void setExecIDRef(String execIDRef) {
		this.execIDRef = execIDRef;
	}
	/**
	 * @return the actName
	 */
	public String getActName() {
		return actName;
	}
	/**
	 * @param actName the actName to set
	 */
	public void setActName(String actName) {
		this.actName = actName;
	}
	/**
	 * @return the iteration
	 */
	public String getIterationVector() {
		return iterationVector;
	}
	/**
	 * @param iterationVector the iteration to set
	 */
	public void setIterationVector(String iterationVector) {
		this.iterationVector = iterationVector;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the wfNameRef
	 */
	public String getWfNameRef() {
		return wfNameRef;
	}

	/**
	 * @param wfNameRef the wfNameRef to set
	 */
	public void setWfNameRef(String wfNameRef) {
		this.wfNameRef = wfNameRef;
	}
	
	
}
