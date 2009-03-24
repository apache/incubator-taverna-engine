/*******************************************************************************
 * Copyright (C) 2007-2009 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.List;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.InputPort;

/**
 * Specialisation of InputPort to capture the extra information required by
 * Activity instances.
 * 
 * @author Tom Oinn
 * 
 */
public interface ActivityInputPort extends InputPort, ActivityPort {

	/**
	 * Declares that the DataDocument instances fed as input data (either
	 * directly or as elements of a collection) to this input port must contain
	 * at least one of the specified ReferenceScheme types. This is used to
	 * specify that e.g. an activity can only accept URLs, values or similar.
	 * 
	 * @return Class objects representing the reference scheme types which this
	 *         input can handle
	 */
	public List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes();

	/**
	 * Literal values are a special case as they are not represented by
	 * reference schemes - in rare cases activities may choose to deny literal
	 * values, forcing *all* their inputs to be in a particular reference
	 * scheme. If this is the case then this method should return false, if the
	 * activity is capable of handling literal types without any upconversion to
	 * references (please do implement this!) then it returns false
	 * 
	 * @return true if the activity can cope with literal values, false if it
	 *         requires them to be converted to an instance of a reference
	 *         scheme class (as defined by getHandledReferenceSchemes)
	 */
	public boolean allowsLiteralValues();

	/**
	 * The Java object type desired when the input data reference is converted
	 * to an object. This is only used by the parent Activity when invoking the
	 * data facade. Where the input data is a list this returns the type of leaf
	 * nodes within the collection structure - the instances of this type will
	 * always be wrapped up in a Java collection rather than an array type
	 * <p>
	 * Note that this is not intended to allow activities to consume arbitrary
	 * java classes, activities such as the API consumer should handle this
	 * through the reference scheme mechanism backed by an appropriate store
	 * (most likely an in-memory hash of active objects)
	 * 
	 * @return the desired class of the object returned by the data facade when
	 *         converting the input data reference into a java object. This will
	 *         almost always be String.class or byte[].class but other cases may
	 *         exist.
	 */
	public Class<?> getTranslatedElementClass();

}
