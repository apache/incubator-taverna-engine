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
 * An identified list is a list which is identified by a T2Reference. Lists are
 * immutable once named - if getId() returns a non null value all list methods
 * modifying the underlying list data will throw {@link IllegalStateException}.
 * In the reference management API this list sub-interface is used to represent
 * both collections of identifiers (i.e. 'raw' stored lists) and more fully
 * resolved structures where the types in the list can be reference sets, error
 * documents and other lists of such. The {@link ListDao} interface uses only
 * the 'raw' form consisting of flat lists of identifiers.
 * <p>
 * The IdentifiedList has a unique T2Reference associated with it. If this is
 * null the contents of the list may be modified, otherwise all modification
 * operations throw {@link IllegalStateException}. Lists in T2, once named, are
 * immutable.
 * 
 * @author Tom Oinn
 * 
 * @param <T>
 */
public interface IdentifiedList<T> extends List<T>, Identified {

}
