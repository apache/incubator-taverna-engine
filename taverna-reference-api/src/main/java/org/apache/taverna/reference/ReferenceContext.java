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

import java.util.List;

/**
 * Many operations over the reference manager require access to an appropriate
 * context. The context contains hooks out to platform level facilities such as
 * the security agent framework (when used in conjunction with the enactor).
 * <p>
 * This interface is also used to pass in resources required by the external
 * reference translation and construction SPIs. An example might be a translator
 * from File to URL could work by copying the source file to a web share of some
 * kind, but obviously this can't happen unless properties such as the location
 * of the web share folder are known. These properties tend to be properties of
 * the installation rather than of the code, referring as they do to resources
 * on the machine hosting the reference manager (and elsewhere).
 * <p>
 * Where entities in the context represent properties of the platform rather
 * than the 'session' they are likely to be configured in a central location
 * such as a Spring context definition, this interface is neutral to those
 * concerns.
 * 
 * @author Tom Oinn
 */
public interface ReferenceContext {
	/**
	 * Return a list of all entities in the resolution context which match the
	 * supplied entity type argument.
	 * 
	 * @param <T>
	 *            The generic type of the returned entity list. In general the
	 *            compiler is smart enough that you don't need to specify this,
	 *            it can pick it up from the entityType parameter.
	 * @param entityType
	 *            Class of entity to return. Use Object.class to return all
	 *            entities within the reference context
	 * @return a list of entities from the reference context which can be cast
	 *         to the specified type.
	 */
	<T extends Object> List<T> getEntities(Class<T> entityType);

	/**
	 * Add an entity to the context.
	 */
	void addEntity(Object entity);
}
