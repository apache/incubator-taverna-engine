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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * a Var that has no pName is either a WF input or output, depending on isInput
 * @author Paolo Missier
 */
@Entity
public class Var {
	
	@Id @GeneratedValue
	private String identifier;
	private String vName, pName;
	private boolean isInput;
	private String wfInstanceRef;
	private String type;
	private int typeNestingLevel = 0;
	private int actualNestingLevel = 0;
	private boolean isANLset = false;  // set to true when the ANL has been set 
	private int portNameOrder = 0;
	
	/**
	 * @return the wfInstanceRef
	 */
	public String getWfInstanceRef() {
		return wfInstanceRef;
	}
	/**
	 * @param wfInstanceRef the wfInstanceRef to set
	 */
	public void setWfInstanceRef(String wfInstanceRef) {
		this.wfInstanceRef = wfInstanceRef;
	}
	/**
	 * @return the vName
	 */
	public String getVName() {
		return vName;
	}
	/**
	 * @param name the vName to set
	 */
	public void setVName(String name) {
		vName = name;
	}
	/**
	 * @return the pName
	 */
	public String getPName() {
		return pName;
	}
	/**
	 * @param name the pName to set
	 */
	public void setPName(String name) {
		pName = name;
	}
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
	 * @return the typeNestingLevel
	 */
	public int getTypeNestingLevel() {
		return typeNestingLevel;
	}
	/**
	 * @param typeNestingLevel the typeNestingLevel to set
	 */
	public void setTypeNestingLevel(int typeNestingLevel) {
		this.typeNestingLevel = typeNestingLevel;
	}
	/**
	 * @return the actualNestingLevel
	 */
	public int getActualNestingLevel() {
		return actualNestingLevel;
	}
	/**
	 * @param actualNestingLevel the actualNestingLevel to set
	 */
	public void setActualNestingLevel(int actualNestingLevel) {
		this.actualNestingLevel = actualNestingLevel;
	}
	/**
	 * @return the isANLset
	 */
	public boolean isANLset() {
		return isANLset;
	}
	/**
	 * @param isANLset the isANLset to set
	 */
	public void setANLset(boolean isANLset) {
		this.isANLset = isANLset;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @return the portNameOrder
	 */
	public int getPortNameOrder() {
		return portNameOrder;
	}
	/**
	 * @param portNameOrder the portNameOrder to set
	 */
	public void setPortNameOrder(int portNameOrder) {
		this.portNameOrder = portNameOrder;
	}


}
