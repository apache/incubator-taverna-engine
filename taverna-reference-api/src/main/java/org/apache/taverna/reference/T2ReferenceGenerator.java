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
 * Provides new unique T2Reference instances. Used by and injected into the
 * various service interface implementations when registering new reference
 * sets, error documents and lists.
 * 
 * @author Tom Oinn
 * @see T2Reference
 */
public interface T2ReferenceGenerator {
	/**
	 * All T2Reference objects will have this namespace
	 * 
	 * @return the namespace as a string
	 */
	String getNamespace();

	/**
	 * Create a new and otherwise unused T2Reference to a ReferenceSet. The
	 * namespace of the reference will depend on the current workflow run read
	 * from the ReferenceContext.
	 * 
	 * @return new T2Reference for a ReferenceSet, namespace and local parts
	 *         will be initialized and the reference is ready to use when
	 *         returned.
	 */
	T2Reference nextReferenceSetReference(ReferenceContext context);

	/**
	 * Create a new and otherwise unused T2Reference to an IdentifiedList. The
	 * namespace of the reference will depend on the current workflow run read
	 * from the ReferenceContext.
	 * 
	 * @param containsErrors
	 *            whether the list this reference is generated for contains
	 *            t2references with their containsErrors property set to true.
	 *            Returns true if <em>any</em> reference in the list is or
	 *            contains an error.
	 * @param listDepth
	 *            depth of the list to which this identifier will be applied
	 * @return a new T2Reference for an IdentifiedList. Namespace, type and
	 *         local parts will be initialized but depth and error content will
	 *         still be at their default values of '0' and 'false' respectively,
	 *         these will need to be re-set before the reference is viable.
	 */
	T2Reference nextListReference(boolean containsErrors, int listDepth,
			ReferenceContext context);

	/**
	 * Create a new and otherwise unused T2Reference to an ErrorDocument. The
	 * namespace of the reference will depend on the current workflow run read
	 * from the ReferenceContext.
	 * 
	 * @param depth
	 *            the depth of the error document to which this identifier will
	 *            refer
	 * @return a new T2Reference for an ErrorDocument
	 */
	T2Reference nextErrorDocumentReference(int depth, ReferenceContext context);
}
