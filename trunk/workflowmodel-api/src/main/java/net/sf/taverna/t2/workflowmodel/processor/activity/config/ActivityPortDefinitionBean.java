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
package net.sf.taverna.t2.workflowmodel.processor.activity.config;

import java.util.Collections;
import java.util.List;

/**
 * A generic bean that describes the shared properties of input and output ports.
 * 
 * @author Stuart Owen
 *
 */
public abstract class ActivityPortDefinitionBean {
	private String name;
	private int depth;
	private List<String> mimeTypes;
	
	/**
	 * @return the port name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the port name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the depth of the port
	 */
	public int getDepth() {
		return depth;
	}
	
	/**
	 * @param depth the depth of the port
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	/**
	 * @return a list a MIME types that describe the port
	 */
	public List<String> getMimeTypes() {
		if (mimeTypes == null) {
			return Collections.emptyList();
		}
		return mimeTypes;
	}
	
	/**
	 * @param mimeTypes the list of MIME-types that describe the port
	 */
	public void setMimeTypes(List<String> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
}
