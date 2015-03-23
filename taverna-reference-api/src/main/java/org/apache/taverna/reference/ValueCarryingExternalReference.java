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

package org.apache.taverna.reference;

/**
 * Specialization of ExternalReferenceSPI for reference types which carry a
 * value type internally. Such references can be de-referenced to the specified
 * object type very cheaply. Note that this is not to be used to get an object
 * property of a reference, the returned object must correspond to the value of
 * the referenced data - this means that the HttpUrlReference does not use this
 * to return a java.net.URL, but that the InlineStringReference does use it to
 * return a java.lang.String
 * 
 * @author Tom Oinn
 */
public interface ValueCarryingExternalReference<T> extends ExternalReferenceSPI {
	/**
	 * Returns the type of the inlined value
	 */
	Class<T> getValueType();

	/**
	 * Returns the value
	 */
	T getValue();
}
