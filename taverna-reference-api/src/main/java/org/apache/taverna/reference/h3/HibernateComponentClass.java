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

/**
 * A marker used to denote that the class should be pre-loaded into hibernate's
 * class mapping. Used for component classes which are not going to be mapped to
 * the RDBMS but which must be loadable for mapped classes to instantiate
 * correctly. Basically if you refer to a class that isn't itself going to be
 * mapped in hibernate within a mapping definition you'll need to add that
 * component class to this SPI or hibernate won't be able to find it as it won't
 * know that it should associate it with the appropriate class loader.
 * <p>
 * This should be used as an SPI marker, and set as the first SPI registry in
 * the preloadRegistries property of the SpiRegistryAwareLocalSessionFactoryBean
 * 
 * @author Tom Oinn
 */
public interface HibernateComponentClass {
}
