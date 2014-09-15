/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.execution.impl.local;

import java.util.Date;

import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.monitor.NoSuchPropertyException;

/**
 * A MonitorableProperty with fixed values.
 * 
 * @author David Withers
 */
public class StaticProperty implements MonitorableProperty<Object> {
	private Object value;
	private String[] name;
	private Date lastModified;
	
	/**
	 * Records the state of the MonitorableProperty.
	 * 
	 * @param property
	 */
	public StaticProperty(MonitorableProperty<?> property) {
		try {
			value = property.getValue();
		} catch (NoSuchPropertyException e) {
		}
		name = property.getName();
		lastModified = property.getLastModified();
	}
	
	@Override
	public Object getValue() throws NoSuchPropertyException {
		return value;
	}

	@Override
	public String[] getName() {
		return name;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
}
