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

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

/**
 * 
 * @author David Withers
 */
@ConfigurationBean(uri = ActivityServiceImplTest.annotatedBeanURI + "#Configuration")
public class ActivityTestBean {

	public String stringType, optionalStringType;
	public int integerType;
	public long longType;
	public float floatType;
	public double doubleType;
	public boolean booleanType;
	public ActivityTestEnum enumType;
	public ActivityTestBean2 beanType, beanType2;
	public String[] arrayType;
	public List<String> listType, unorderedListType;
	public HashSet<String> setType;
	public ActivityTestBean2[] arrayOfBeanType;
	public List<ActivityTestBean2> listOfBeanType;
	public Set<ActivityTestBean2> setOfBeanType;
	public ActivityTestEnum[] arrayOfEnumType;
	public List<ActivityTestEnum> listOfEnumType;
	public SortedSet<ActivityTestEnum> setOfEnumType;
	public URI uriType;
	public SubclassActivityTestBean subclass;
	
	@ConfigurationProperty(name = "stringType")
	public void setStringType(String parameter) {
		stringType = parameter;
	}

	@ConfigurationProperty(name = "optionalStringType", required = false)
	public void setOptionalStringType(String parameter) {
		optionalStringType = parameter;
	}

	@ConfigurationProperty(name = "integerType")
	public void setIntegerType(int parameter) {
		integerType = parameter;
	}

	@ConfigurationProperty(name = "longType")
	public void setLongType(long parameter) {
		longType = parameter;
	}

	@ConfigurationProperty(name = "floatType")
	public void setFloatType(float parameter) {
		floatType = parameter;
	}

	@ConfigurationProperty(name = "doubleType")
	public void setDoubleType(double parameter) {
		doubleType = parameter;
	}

	@ConfigurationProperty(name = "booleanType")
	public void setBooleanType(boolean parameter) {
		booleanType = parameter;
	}

	@ConfigurationProperty(name = "enumType")
	public void setEnumType(ActivityTestEnum parameter) {
		enumType = parameter;
	}

	@ConfigurationProperty(name = "beanType")
	public void setBeanType(ActivityTestBean2 parameter) {
		beanType = parameter;
	}

	@ConfigurationProperty(name = "beanType2")
	public void setBeanType2(ActivityTestBean2 parameter) {
		beanType2 = parameter;
	}

	@ConfigurationProperty(name = "subclass")
	public void setBeanType2(SubclassActivityTestBean parameter) {
		subclass = parameter;
	}

	
	@ConfigurationProperty(name = "arrayType")
	public void setArrayType(String[] parameter) {
		arrayType = parameter;
	}

	@ConfigurationProperty(name = "listType")
	public void setListType(List<String> parameter) {
		listType = parameter;
	}

	@ConfigurationProperty(name = "unorderedListType", ordering = ConfigurationProperty.OrderPolicy.NON_ORDERED)
	public void setUnorderedListType(List<String> parameter) {
		unorderedListType = parameter;
	}

	@ConfigurationProperty(name = "setType")
	public void setSetType(HashSet<String> parameter) {
		setType = parameter;
	}

	@ConfigurationProperty(name = "arrayOfBeanType")
	public void setArrayOfBeanType(ActivityTestBean2[] parameter) {
		arrayOfBeanType = parameter;
	}

	@ConfigurationProperty(name = "listOfBeanType")
	public void setListOfBeanType(List<ActivityTestBean2> parameter) {
		listOfBeanType = parameter;
	}

	@ConfigurationProperty(name = "setOfBeanType")
	public void setSetOfBeanType(Set<ActivityTestBean2> parameter) {
		setOfBeanType = parameter;
	}

	@ConfigurationProperty(name = "arrayOfEnumType")
	public void setArrayOfEnumType(ActivityTestEnum[] parameter) {
		arrayOfEnumType = parameter;
	}

	@ConfigurationProperty(name = "listOfEnumType")
	public void setListOfEnumType(List<ActivityTestEnum> parameter) {
		listOfEnumType = parameter;
	}

	@ConfigurationProperty(name = "setOfEnumType")
	public void setSetOfEnumType(SortedSet<ActivityTestEnum> parameter) {
		setOfEnumType = parameter;
	}

	@ConfigurationProperty(name = "uriType")
	public void setURIType(URI parameter) {
		uriType = parameter;
	}

}
