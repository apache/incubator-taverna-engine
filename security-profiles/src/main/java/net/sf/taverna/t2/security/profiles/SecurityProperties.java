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
package net.sf.taverna.t2.security.profiles;

import java.util.HashMap;

/**
 * A generic container for various security properties of a service.
 * 
 * @author Alexandra Nenadic
 *
 */
public class SecurityProperties {
	
	/* True/false constants */
	public static final String TRUE = "True";
	public static final String FALSE = "False";

	/** A collection of various security properties */
	private HashMap<String,String> properties;
	
	// Constructor that creates an empty properties collection
	public SecurityProperties(){
		properties = new HashMap<String,String>();
	}
	
	public void setProperty(String property, String value) {
			properties.put(property, value);
	}
	
	public String getProperty(String property) throws NoSuchSecurityPropertyException{
		if (!properties.keySet().contains(property)){
			throw new NoSuchSecurityPropertyException();
		}
		else
			return properties.get(property);
	}
	
	public void setProperties(HashMap<String,String> pr) {
		properties = pr;
	}
	
	public HashMap<String,String> getProperties(){
		return properties;
	}
}
