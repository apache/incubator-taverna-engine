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

package org.apache.taverna.monitor;

import java.util.Date;

/**
 * A single readable property contained by a Monitorable. This is used to
 * express properties that are dynamic with respect to workflow invocation as
 * opposed to static properties defined by the workflow model. A typical example
 * of this might be dispatch stack queue size or number of jobs completed. All
 * properties are defined relative to a particular owning process identifier,
 * this is the same mechanism as used in the workflow model to isolate different
 * data streams.
 * 
 * @author Tom Oinn
 */
public interface MonitorableProperty<T> {
	/**
	 * Return the value of this property
	 */
	T getValue() throws NoSuchPropertyException;

	/**
	 * Return the name of this property, names are heirarchical in nature and
	 * are defined as an array of String objects. This is to allow e.g. dispatch
	 * layers to expose a set of related properties under the same root name.
	 */
	String[] getName();

	/**
	 * Get the last update date for this property, if the property is immutable
	 * then this should be set to the date at which the implementation is
	 * created.
	 */
	Date getLastModified();
}
