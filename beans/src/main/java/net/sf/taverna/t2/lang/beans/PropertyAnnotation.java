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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation of a Java bean style property method, ie. a getXX() or setXX()
 * method.
 * <p>
 * The annotations allow the method to better describe properties such as
 * {@link #displayName()}, {@link #shortDescription()} and {@link #hidden()}.
 * <p>
 * The annotations can be retrieved as {@link PropertyDescriptor} using
 * {@link PropertyAnnotationExtractor}, or if {@link PropertyAnnotated} has been
 * used (recommended), through Java's BeanInfo support, such as using
 * {@link Introspector}.
 * <p>
 * Annotations can be applied to interfaces or classes, abstract and normal 
 * methods, as long as they confirm with the Java bean conventions. Annotations 
 * will be inherited, so overriding methods don't need to reapply the annotations,
 * although they can if they want to override.
 * <p>
 * It is recommended that classes using these annotations either subclass
 * {@link PropertyAnnotated} or have a neighbouring BeanInfo class that
 * subclasses PropertyAnnotated.
 * <p>
 * Example usage:
 * 
 * <pre>
 * 	public interface MyBean {
 *		// Annotation for the property called "name". displayName: Title
 *		// of the property shown in UI instead of "name".
 *		&#064;PropertyAnnotation(displayName = "Full name")
 *		public String getName();
 *
 *		// Second annotation for the write-method of the same property called
 *		// "name". Both displayName and shortDescription will be set on the
 *		// property descriptor.
 *		&#064;PropertyAnnotation(shortDescription = "The name of the person")
 *		public void setName(String name);
 *
 *		// Boolean read method for the property "married", two annotations.
 *		// expert: Only shown in UI under "advanced" views.
 *		&#064;PropertyAnnotation(expert = true, shortDescription = "Marital status")
 *		public boolean isMarried();
 *
 *		// Write-method for the "married" property, no new annotations, but will
 *		// get the ones from {&#064;link #isMarried()}.
 *		public void setMarried(boolean married);
 *
 *		// Write-only method, hidden (not shown in UIs).
 *		&#064;PropertyAnnotation(hidden = true)
 *		public void setID(String id);
 *
 *		// Read-only method, no annotations, defaults will be used.
 *		public void getTelephoneNumber(String number);
 *	}
 * </pre>
 * 
 * @see PropertyAnnotated
 * @author Stian Soiland-Reyes
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface PropertyAnnotation {

	/**
	 * A unique string that means the default should be used
	 */
	public static String DEFAULT = "Default_8930B86A-50C0-4859-9B6F-DD034B3C5C1E";

	String displayName() default DEFAULT;

	String name() default DEFAULT;

	String shortDescription() default DEFAULT;

	boolean expert() default false;

	boolean hidden() default false;

	boolean preferred() default false;

}