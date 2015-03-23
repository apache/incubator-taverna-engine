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

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

/**
 * Top level access point to the reference manager for client code which is
 * aware of references and error documents. Provides methods to store and
 * retrieve instances of ReferenceSet, IdentifiedList&lt;T2Reference&gt; and
 * ErrorDocument. Acts as an integration layer for the three sub-component
 * service, providing in addition collection traversal and retrieval of lists of
 * identified entities (ReferenceSet, IdentifiedList&lt;Identified&gt; and
 * ErrorDocument) from a T2Reference identifying a list.
 * <p>
 * Also provides registration and retrieval logic for POJOs where supported by
 * appropriate plug-in instances, these methods can be used by code which is not
 * 'reference aware' to store and retrieve value types transparently.
 * <p>
 * Resolution of collections can happen at three different levels:
 * <ol>
 * <li>The embedded {@link ListService} resolves the collection ID to a list of
 * child IDs, and doesn't traverse these children if they are themselves lists.
 * Use the {@link #getListService()}.{@link ListService#getList(T2Reference)
 * getList()} to call this method directly on the list service if you need this
 * functionality, returning a list of {@link T2Reference}</li>
 * <li>The {@link #resolveIdentifier(T2Reference, Set, ReferenceContext)
 * resolveIdentifier} method in this service instead resolves to a fully
 * realized collection of the entities which those IDs reference, and does
 * recursively apply this to child lists, resulting in a nested collection
 * structure where the leaf nodes are ReferenceSet and ErrorDocument instances
 * and non-leaf are IdentifiedList of Identified (the super-interface for
 * IdentifiedList, ReferenceSet and ErrorDocument). Use this method if you want
 * to access the ExternalReferenceSPI and ErrorDocument entities directly
 * because your code can act on a particular reference type - in general in
 * these cases you would also be using the augmentation system to ensure that
 * the required reference type was actually present in the collection structure.
 * </li>
 * <li>The third level of resolution is to render the identifier through
 * {@link #renderIdentifier(T2Reference, Class, ReferenceContext)
 * renderIdentifier} to a nested structure as in
 * {@link #resolveIdentifier(T2Reference, Set, ReferenceContext)
 * resolveIdentifier} but where the structure consists of POJOs of the specified
 * type and lists or either list or the leaf type. This is used when your code
 * is reference agnostic and just requires the values in an easy to consume
 * fashion. Note that because this involves pulling the entire structure into
 * memory it may not be suitable for large data, use with caution. This method
 * will, unlike {@link #resolveIdentifier(T2Reference, Set, ReferenceContext)
 * resolveIdentifier}, fail if the reference contains or is an error.</li>
 * </ol>
 * 
 * @author Tom Oinn
 */
public interface ReferenceService {
	/**
	 * Perform recursive identifier resolution, building a collection structure
	 * of Identified objects, any collection elements being IdentifiedLists of
	 * Identified subclasses. If the id has depth 0 this will just return the
	 * Identified to which that id refers.
	 * 
	 * @param id
	 *            the T2Reference to resolve
	 * @param ensureTypes
	 *            a set of ExternalReferenceSPI classes, this is used to augment
	 *            any resolved ReferenceSet instances to ensure that each one
	 *            has at least one of the specified types. If augmentation is
	 *            not required this can be set to null.
	 * @param context
	 *            the ReferenceContext to use to resolve this and any
	 *            recursively resolved identifiers <br/>
	 *            If null the implementation should insert a new empty context
	 *            and proceed.
	 * @return fully resolved Identified subclass - this is either a (recursive)
	 *         IdentifiedList of Identified, a ReferenceSet or an ErrorDocument
	 * @throws ReferenceServiceException
	 *             if any problems occur during resolution
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	Identified resolveIdentifier(T2Reference id,
			Set<Class<ExternalReferenceSPI>> ensureTypes,
			ReferenceContext context) throws ReferenceServiceException;

	/**
	 * As resolveIdentifier but using a callback object and returning
	 * immediately
	 * 
	 * @throws ReferenceServiceException
	 *             if anything goes wrong with the setup of the resolution job.
	 *             Any exceptions during the resolution process itself are
	 *             communicated through the callback object.
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	void resolveIdentifierAsynch(T2Reference id,
			Set<Class<ExternalReferenceSPI>> ensureTypes,
			ReferenceContext context,
			ReferenceServiceResolutionCallback callback)
			throws ReferenceServiceException;

	/**
	 * Resolve the given identifier, building a POJO structure where the
	 * non-list items are of the desired class. This makes of any external
	 * references that can directly expose the appropriate object type, then, if
	 * none are present in a given reference set, it attempts to locate a POJO
	 * builder and uses the cheapest available reference to get an InputStream
	 * and build the target object. If no appropriate builder or embedded value
	 * can be found the process throws ReferenceServiceException, it also does
	 * this if any error occurs during retrieval of a (potentially nested)
	 * identifier.
	 * <p>
	 * This method will return a collection structure mirroring that of the
	 * specified T2Reference, client code should use T2Reference.getDepth() to
	 * determine the depth of this structure; a reference with depth of 0 means
	 * that the object returned is of the specified class, one of depth 1 is a
	 * list of this class and so on.
	 * <p>
	 * If the T2Reference contains or is an error this method will not retrieve
	 * it, and instead throws ReferenceServiceException
	 * 
	 * @see StreamToValueConverterSPI
	 * @see ValueCarryingExternalReference
	 * @param id
	 *            the T2Reference to render to a POJO
	 * @param leafClass
	 *            the java class for leaves in the resulting POJO structure
	 * @param context
	 *            a reference context, potentially used if required by the
	 *            openStream methods of ExternalReferenceSPI implementations
	 *            used as sources for the POJO construction <br/>
	 *            If null the implementation should insert a new empty context
	 *            and proceed.
	 * @return a java structure as defined above
	 * @throws ReferenceServiceException
	 *             if anything fails during this process
	 */
	Object renderIdentifier(T2Reference id, Class<?> leafClass,
			ReferenceContext context) throws ReferenceServiceException;

	/**
	 * The top level registration method is used to register either as yet
	 * unregistered ErrorDocuments and ReferenceSets (if these are passed in and
	 * already have an identifier this call does nothing) and arbitrarily nested
	 * Lists of the same. In addition any ExternalReferenceSPI instances found
	 * will be wrapped in a single item ReferenceSet and registered, and any
	 * Throwables will be wrapped in an ErrorDocument and registered. Lists will
	 * be converted to IdentifiedList&lt;T2Reference&gt; and registered if all
	 * children can be (or were already) appropriately named.
	 * <p>
	 * This method is only valid on parameters of the following type :
	 * <ol>
	 * <li>{@link T2Reference} - returned immediately as itself, this is needed
	 * because it means we can register lists of existing T2Reference</li>
	 * <li>{@link ReferenceSet} - registered if not already registered,
	 * otherwise returns existing T2Reference</li>
	 * <li>{@link ErrorDocument} - same behaviour as ReferenceSet</li>
	 * <li>{@link ExternalReferenceSPI} - wrapped in ReferenceSet, registered
	 * and ID returned</li>
	 * <li>Throwable - wrapped in {@link ErrorDocument} with no message,
	 * registered and ID returned</li>
	 * <li>List - all children are first registered, if this succeeds the list
	 * is itself registered as an {@link IdentifiedList} of {@link T2Reference}
	 * and its reference returned.</li>
	 * </ol>
	 * The exception to this is if the useConvertorSPI parameter is set to true
	 * - in this case any objects which do not match the above allowed list will
	 * be run through any available ValueToReferenceConvertorSPI instances in
	 * turn until one succeeds or all fail, which may result in the creation of
	 * ExternalReferenceSPI instances. As these can be registered such objects
	 * will not cause an exception to be thrown.
	 * 
	 * @see ValueToReferenceConverterSPI
	 * @param o
	 *            the object to register with the reference system, must comply
	 *            with and will be interpreted as shown in the type list above.
	 * @param targetDepth
	 *            the depth of the top level object supplied. This is needed
	 *            when registering empty collections and error documents,
	 *            whether as top level types or as members of a collection
	 *            within the top level type. If registering a collection this is
	 *            the collection depth, so a List of ReferenceSchemeSPI would be
	 *            depth 1. Failing to specify this correctly will result in
	 *            serious problems downstream so be careful! We can't catch all
	 *            potential problems in this method (although some errors will
	 *            be trapped).
	 * @param useConverterSPI
	 *            whether to attempt to use the ValueToReferenceConvertorSPI
	 *            registry (if defined and available) to map arbitrary objects
	 *            to ExternalReferenceSPI instances on the fly. The registry of
	 *            converters is generally injected into the implementation of
	 *            this service.
	 * @param context
	 *            ReferenceContext to use if required by component services,
	 *            this is most likely to be used by the object to reference
	 *            converters if engaged. <br/>
	 *            If null the implementation should insert a new empty context
	 *            and proceed.
	 * @return a T2Reference to the registered object
	 * @throws ReferenceServiceException
	 *             if the object type (or, for collections, the recursive type
	 *             of its contents) is not in the allowed list or if a problem
	 *             occurs during registration. Also thrown if attempting to use
	 *             the converter SPI without an attached registry.
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	T2Reference register(Object o, int targetDepth, boolean useConverterSPI,
			ReferenceContext context) throws ReferenceServiceException;

	/**
	 * Given a string representation of a T2Reference create a new T2Reference
	 * with the correct depth etc.
	 * 
	 * @param reference
	 * @return a new T2Reference parsed from the original
	 */
	T2Reference referenceFromString(String reference);

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(T2Reference reference) throws ReferenceServiceException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(List<T2Reference> references)
			throws ReferenceServiceException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	void deleteReferencesForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException;

	/**
	 * Returns the {@link ErrorDocumentService} this ReferenceService uses, use
	 * this when you need functionality from that service explicitly.
	 */
	ErrorDocumentService getErrorDocumentService();

	/**
	 * Returns the {@link ReferenceSetService} this ReferenceService uses, use
	 * this when you need functionality from that service explicitly.
	 */
	ReferenceSetService getReferenceSetService();

	/**
	 * Returns the {@link ListService} this ReferenceService uses, use this when
	 * you need functionality from that service explicitly.
	 */
	ListService getListService();

	/**
	 * Initiates a traversal of the specified t2reference, traversing to
	 * whatever level of depth is required such that all identifiers returned
	 * within the iterator have the specified depth. The context (i.e. the index
	 * path from the originally specified reference to each reference within the
	 * iteration) is included through use of the ContextualizedT2Reference
	 * wrapper class
	 * 
	 * @param source
	 *            the T2Reference from which to traverse. In general this is the
	 *            root of a collection structure.
	 * @param desiredDepth
	 *            the desired depth of all returned T2References, must be less
	 *            than or equal to that of the source reference.
	 * @throws ReferenceServiceException
	 *             if unable to create the iterator for some reason. Note that
	 *             implementations are free to lazily perform the iteration so
	 *             this method may succeed but the iterator produced can fail
	 *             when used. If the iterator fails it will do so by throwing
	 *             one of the underlying sub-service exceptions.
	 */
	@Transactional(propagation = SUPPORTS, readOnly = true)
	Iterator<ContextualizedT2Reference> traverseFrom(T2Reference source,
			int desiredDepth);
}
