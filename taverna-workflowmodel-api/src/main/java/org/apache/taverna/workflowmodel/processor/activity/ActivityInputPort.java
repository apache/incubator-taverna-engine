/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.List;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.InputPort;

/**
 * Specialisation of InputPort to capture the extra information required by
 * Activity instances.
 * 
 * @author Tom Oinn
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
	List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes();

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
	boolean allowsLiteralValues();

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
	Class<?> getTranslatedElementClass();
}
