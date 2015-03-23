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

package org.apache.taverna.reference.h3;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceSet;

/**
 * A marker interface used to denote that the component should be registered
 * with the Hibernate ORM system prior to any {@link ExternalReferenceSPI}
 * implementations. This is here to allow implementations of e.g.
 * {@link ReferenceSet} to be in the implementation package where they belong
 * and still guarantee that they are registered before any other plugins.
 * <p>
 * This should be used as an SPI marker, and set as the first SPI registry in
 * the spiRegistries property of the SpiRegistryAwareLocalSessionFactoryBean
 * 
 * @author Tom Oinn
 */
public interface HibernateMappedEntity {
}
