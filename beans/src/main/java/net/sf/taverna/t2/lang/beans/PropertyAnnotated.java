/**********************************************************************
 * Copyright (C) 2009 The University of Manchester   
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
 **********************************************************************/
package net.sf.taverna.t2.lang.beans;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * A {@link BeanInfo} that includes {@link PropertyDescriptor}s from methods
 * annotated using {@link PropertyAnnotation}.
 * <p>
 * The bean info from the PropertyAnnotation will then be available through
 * Java's {@link Introspector}, and allows you to specify details such as
 * {@link PropertyAnnotation#displayName()} and
 * {@link PropertyAnnotation#hidden()} for the properties of a Java Bean.
 * <p>
 * This class can either be used as a superclass for the classes containing
 * property annotated methods, or put in a neighbouring BeanInfo class.
 * <p>
 * For instance, if your class is called DescribedClass and has methods
 * annotated using {@link PropertyAnnotation}, either let DescribedClass
 * subclass {@link PropertyAnnotated}, or make a neighbouring {@link BeanInfo}
 * class called DescribedClassBeanInfo, which should subclass
 * {@link PropertyAnnotated}.
 * 
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class PropertyAnnotated extends SimpleBeanInfo {

	private static PropertyAnnotationExtractor extractor = new PropertyAnnotationExtractor();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return extractor.getPropertyDescriptors(getDescribedClass());
	}

	/**
	 * The class that is being described. By default this returns
	 * {@link #getClass()} so that {@link PropertyAnnotated} can be used as a
	 * superclass, but if instead the DescribedClassBeanInfo pattern is used,
	 * subclass PropertyAnnotated in each BeanInfo class, and override this
	 * method to return the described class. (DescribedClass in this example)
	 * 
	 */
	public Class<?> getDescribedClass() {
		return getClass();
	}

}
