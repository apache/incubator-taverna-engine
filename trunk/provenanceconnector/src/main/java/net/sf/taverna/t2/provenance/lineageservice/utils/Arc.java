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
 * @author paolo
 * 
 */

public class Arc {
	private
	String identifier;

	String wfInstanceRef;
	String sourcePnameRef;
	String sourceVarNameRef;
	String sinkPnameRef;
	String sinkVarNameRef;

	/**
	 * @return the wfInstanceRef
	 */
	public String getWfInstanceRef() {
		return wfInstanceRef;
	}

	/**
	 * @param wfInstanceRef
	 *            the wfInstanceRef to set
	 */
	public void setWfInstanceRef(String wfInstanceRef) {
		this.wfInstanceRef = wfInstanceRef;
	}

	/**
	 * @return the sourcePnameRef
	 */
	public String getSourcePnameRef() {
		return sourcePnameRef;
	}

	/**
	 * @param sourcePnameRef
	 *            the sourcePnameRef to set
	 */
	public void setSourcePnameRef(String sourcePnameRef) {
		this.sourcePnameRef = sourcePnameRef;
	}

	/**
	 * @return the sourceVarNameRef
	 */
	public String getSourceVarNameRef() {
		return sourceVarNameRef;
	}

	/**
	 * @param sourceVarNameRef
	 *            the sourceVarNameRef to set
	 */
	public void setSourceVarNameRef(String sourceVarNameRef) {
		this.sourceVarNameRef = sourceVarNameRef;
	}

	/**
	 * @return the sinkPnameRef
	 */
	public String getSinkPnameRef() {
		return sinkPnameRef;
	}

	/**
	 * @param sinkPnameRef
	 *            the sinkPnameRef to set
	 */
	public void setSinkPnameRef(String sinkPnameRef) {
		this.sinkPnameRef = sinkPnameRef;
	}

	/**
	 * @return the sinkVarNameRef
	 */
	public String getSinkVarNameRef() {
		return sinkVarNameRef;
	}

	/**
	 * @param sinkVarNameRef
	 *            the sinkVarNameRef to set
	 */
	public void setSinkVarNameRef(String sinkVarNameRef) {
		this.sinkVarNameRef = sinkVarNameRef;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

}
