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

import java.util.Set;

/**
 * A set of ExternalReferenceSPI instances, all of which point to the same (byte
 * equivalent) data. The set is identified by a T2Reference. This interface is
 * read-only, as are most of the interfaces in this package. Rather than
 * modifying properties of the reference set directly the client code should use
 * the reference manager functionality.
 * <p>
 * It is technically okay, but rather unhelpful, to have a ReferenceSet with no
 * ExternalReferenceSPI implementations. In general this is a sign that
 * something has gone wrong somewhere as the reference set will not be
 * resolvable in any way, but it would still retain its unique identifier so
 * there may be occasions where this is the desired behaviour.
 * 
 * @author Tom Oinn
 */
public interface ReferenceSet extends Identified {
	/**
	 * The reference set contains a set of ExternalReferenceSPI instances, all
	 * of which point to byte equivalent data.
	 * 
	 * @return the set of references to external data
	 */
	Set<ExternalReferenceSPI> getExternalReferences();

	/**
	 * Get approximate size of the data pointed to by this ReferenceSet.
	 */
	Long getApproximateSizeInBytes();
}
