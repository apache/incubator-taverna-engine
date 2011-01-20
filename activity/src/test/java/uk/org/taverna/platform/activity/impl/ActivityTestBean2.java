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
package uk.org.taverna.platform.activity.impl;

import net.sf.taverna.t2.workflowmodel.processor.activity.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ConfigurationProperty;

/**
 * 
 * @author David Withers
 */
@ConfigurationBean(uri = ActivityServiceImplTest.annotatedBeanURI + "/configuration2")
public class ActivityTestBean2 {

	public String stringType2;

	@ConfigurationProperty(name = "stringType2")
	public void setStringType(String stringType) {
		stringType2 = stringType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stringType2 == null) ? 0 : stringType2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityTestBean2 other = (ActivityTestBean2) obj;
		if (stringType2 == null) {
			if (other.stringType2 != null)
				return false;
		} else if (!stringType2.equals(other.stringType2))
			return false;
		return true;
	}
	
}

