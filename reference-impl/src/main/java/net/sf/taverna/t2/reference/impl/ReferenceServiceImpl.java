/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.reference.ContextualizedT2Reference;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentServiceException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.Identified;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListServiceException;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetServiceException;
import net.sf.taverna.t2.reference.StreamToValueConverterSPI;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.ValueCarryingExternalReference;
import net.sf.taverna.t2.reference.ValueToReferenceConversionException;
import net.sf.taverna.t2.reference.ValueToReferenceConverterSPI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of ReferenceService, inject with ReferenceSetService,
 * ErrorDocumentService and ListService to enable.
 * 
 * @author Tom Oinn
 * @author Alan R Williams
 * @author Stuart Owen
 */
public class ReferenceServiceImpl extends AbstractReferenceServiceImpl
		implements ReferenceService {

	private final Log log = LogFactory.getLog(ReferenceServiceImpl.class);

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
	 * <li>{@link ReferenceSet} - registered if not already registered,
	 * otherwise returns existing T2Reference</li>
	 * <li>{@link ErrorDocument} - same behaviour as ReferenceSet</li>
	 * <li>{@link ExternalReferenceSPI} - wrapped in ReferenceSet, registered
	 * and ID returned</li>
	 * <li>Throwable - wrapped in ErrorDocument with no message, registered and
	 * ID returned</li>
	 * <li>List - all children are first registered, if this succeeds the list
	 * is itself registered as an IdentifiedList of T2Reference and its
	 * reference returned.</li>
	 * </ol>
	 * The exception to this is if the useConvertorSPI parameter is set to true
	 * - in this case any objects which do not match the above allowed list will
	 * be run through any available ValueToReferenceConvertorSPI instances in
	 * turn until one succeeds or all fail, which may result in the creation of
	 * ExternalReferenceSPI instances. As these can be registered such objects
	 * will not cause an exception to be thrown.
	 * 
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
	 *            converters if engaged.
	 *            <p>
	 *            If the context is null a new empty reference context is
	 *            inserted.
	 * @return a T2Reference to the registered object
	 * @throws ReferenceServiceException
	 *             if the object type (or, for collections, the recursive type
	 *             of its contents) is not in the allowed list or if a problem
	 *             occurs during registration. Also thrown if attempting to use
	 *             the converter SPI without an attached registry.
	 */
	public T2Reference register(Object o, int targetDepth,
			boolean useConverterSPI, ReferenceContext context)
			throws ReferenceServiceException {
		checkServices();
		if (context == null) {
			context = new EmptyReferenceContext();
		}
		if (useConverterSPI) {
			checkConverterRegistry();
		}
		return getNameForObject(o, targetDepth, useConverterSPI, context);
	}

	private T2Reference getNameForObject(Object o, int currentDepth,
			boolean useConverterSPI, ReferenceContext context)
			throws ReferenceServiceException {
		if (currentDepth < 0) {
			throw new ReferenceServiceException("Cannot register at depth " + currentDepth + ": " + o);
		}
		// First check whether this is an Identified, and if so whether it
		// already has an ID. If this is the case then return it, we assume that
		// anything which has an identifier already allocated must have been
		// registered (this is implicit in the contract for the various
		// sub-services
		if (o instanceof Identified) {
			Identified i = (Identified) o;
			if (i.getId() != null) {
				return i.getId();
			}
		}
		// Then check whether the item *is* a T2Reference, in which case we can
		// just return it (useful for when registering lists of existing
		// references)
		if (o instanceof T2Reference) {
			return (T2Reference) o;
		}
		
		if (o.getClass().isArray()) {
			Class<?> elementType = o.getClass().getComponentType();
			if (elementType.getCanonicalName().equals("char")) {
				char[] cArray = (char[]) o;
				List<Character> cList = new ArrayList<Character>();
				for (char c : cArray) {
					cList.add(new Character(c));
				}
				o = cList;
			} else if (elementType.getCanonicalName().equals("short")) {
				short[] cArray = (short[]) o;
				List<Short> cList = new ArrayList<Short>();
				for (short c : cArray) {
					cList.add(new Short(c));
				}			
				o = cList;
			} else if (elementType.getCanonicalName().equals("int")) {
				int[] cArray = (int[]) o;
				List<Integer> cList = new ArrayList<Integer>();
				for (int c : cArray) {
					cList.add(new Integer(c));
				}			
				o = cList;
			}  else if (elementType.getCanonicalName().equals("long")) {
				long[] cArray = (long[]) o;
				List<Long> cList = new ArrayList<Long>();
				for (long c : cArray) {
					cList.add(new Long(c));
				}			
				o = cList;
			} else if (elementType.getCanonicalName().equals("float")) {
				float[] cArray = (float[]) o;
				List<Float> cList = new ArrayList<Float>();
				for (float c : cArray) {
					cList.add(new Float(c));
				}			
				o = cList;
			} else if (elementType.getCanonicalName().equals("double")) {
				double[] cArray = (double[]) o;
				List<Double> cList = new ArrayList<Double>();
				for (double c : cArray) {
					cList.add(new Double(c));
				}			
				o = cList;
			}  else if (elementType.getCanonicalName().equals("boolean")) {
				boolean[] cArray = (boolean[]) o;
				List<Boolean> cList = new ArrayList<Boolean>();
				for (boolean c : cArray) {
					cList.add(new Boolean(c));
				}			
				o = cList;
			} else if (!elementType.getCanonicalName().equals("byte")){
				// Covert arrays of objects
				Object[] cArray = (Object[]) o;
				List<Object> cList = new ArrayList<Object>();
				for (Object c : cArray) {
					cList.add(c);
				}			
				o = cList;
			}
		}
		
		// If a Collection but not a List
		if ((o instanceof Collection) && !(o instanceof List)) {
			List<Object> cList = new ArrayList<Object>();
			cList.addAll((Collection)o);
			o = cList;
		}
		// Next check lists.
		if (o instanceof List) {
			if (currentDepth < 1) {
				throw new ReferenceServiceException("Cannot register list at depth " + currentDepth);
			}
			List<?> l = (List<?>) o;
			// If the list is empty then register a new empty list of the
			// appropriate depth and return it
			if (l.isEmpty()) {
				try {
					IdentifiedList<T2Reference> newList = listService
							.registerEmptyList(currentDepth);
					return newList.getId();
				} catch (ListServiceException lse) {
					throw new ReferenceServiceException(lse);
				}
			}
			// Otherwise construct a new list of T2Reference and register it,
			// calling the getNameForObject method on all children of the list
			// to construct the list of references
			else {
				List<T2Reference> references = new ArrayList<T2Reference>();
				for (Object item : l) {
					// Recursively call this method with a depth one lower than
					// the current depth
					references.add(getNameForObject(item, currentDepth - 1,
							useConverterSPI, context));
				}
				try {
					IdentifiedList<T2Reference> newList = listService
							.registerList(references);
					return newList.getId();
				} catch (ListServiceException lse) {
					throw new ReferenceServiceException(lse);
				}
			}
		} else {
			// Neither a list nor an already identified object, first thing is
			// to engage the converters if enabled. Only engage if we don't
			// already have a Throwable or an ExternalReferenceSPI instance
			if (useConverterSPI && (o instanceof Throwable == false)
					&& (o instanceof ExternalReferenceSPI == false)) {
				for (ValueToReferenceConverterSPI converter : converterRegistry) {
					if (converter.canConvert(o, context)) {
						try {
							ExternalReferenceSPI ers = converter.convert(o,
									context);
							o = ers;
							break;
						} catch (ValueToReferenceConversionException vtrce) {
							// Fail, but that doesn't matter at the moment as
							// there may be more converters to try. TODO - log
							// this!
						}
					}
				}
			}
			// If the object is neither a Throwable nor an ExternalReferenceSPI
			// instance at this point we should fail the registration process,
			// this means either that the conversion process wasn't enabled or
			// that it failed to map the object type correctly.
			if ((o instanceof Throwable == false)
					&& (o instanceof ExternalReferenceSPI == false)) {
				throw new ReferenceServiceException(
						"Failed to register POJO, found a type '"
								+ o.getClass().getCanonicalName()
								+ "' which cannot be registered with the reference manager");
			}
			// Have either a Throwable or an ExternalReferenceSPI
			else {
				if (o instanceof Throwable) {
					// Wrap in an ErrorDocument and return the ID
					try {
						ErrorDocument doc = errorDocumentService.registerError(
								(Throwable) o, currentDepth);
						return doc.getId();
					} catch (ErrorDocumentServiceException edse) {
						throw new ReferenceServiceException(edse);
					}
				} else if (o instanceof ExternalReferenceSPI) {
					try {
						Set<ExternalReferenceSPI> references = new HashSet<ExternalReferenceSPI>();
						references.add((ExternalReferenceSPI) o);
						ReferenceSet rs = referenceSetService
								.registerReferenceSet(references);
						return rs.getId();
					} catch (ReferenceSetServiceException rsse) {
						throw new ReferenceServiceException(rsse);
					}
				}
			}
		}
		throw new ReferenceServiceException(
				"Should never see this, reference registration"
						+ " logic has fallen off the end of the"
						+ " world, check the code!");
	}

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
	 *            recursively resolved identifiers
	 *            <p>
	 *            If the context is null a new EmptyReferenceContext is inserted
	 *            in its place.
	 * @return fully resolved Identified subclass - this is either a (recursive)
	 *         IdentifiedList of Identified, a ReferenceSet or an ErrorDocument
	 * @throws ReferenceServiceException
	 *             if any problems occur during resolution
	 */
	public Identified resolveIdentifier(T2Reference id,
			Set<Class<ExternalReferenceSPI>> ensureTypes,
			ReferenceContext context) throws ReferenceServiceException {
		checkServices();
		if (context == null) {
			context = new EmptyReferenceContext();
		}
		switch (id.getReferenceType()) {

		case ReferenceSet:
			try {
				ReferenceSet rs;
				if (ensureTypes == null) {
					rs = referenceSetService.getReferenceSet(id);
				} else {
					rs = referenceSetService.getReferenceSetWithAugmentation(
							id, ensureTypes, context);
				}
				return rs;
			} catch (ReferenceSetServiceException rsse) {
				throw new ReferenceServiceException(rsse);
			}

		case ErrorDocument:
			try {
				ErrorDocument ed = errorDocumentService.getError(id);
				return ed;
			} catch (ErrorDocumentServiceException edse) {
				throw new ReferenceServiceException(edse);
			}

		case IdentifiedList:
			try {
				IdentifiedList<T2Reference> idList = listService.getList(id);
				// Construct a new list, and populate with the result of
				// resolving each ID in turn
				IdentifiedArrayList<Identified> newList = new IdentifiedArrayList<Identified>();
				for (T2Reference item : idList) {
					newList.add(resolveIdentifier(item, ensureTypes, context));
				}
				newList.setTypedId(T2ReferenceImpl.getAsImpl(id));
				return newList;
			} catch (ListServiceException lse) {
				throw new ReferenceServiceException(lse);
			}

		default:
			throw new ReferenceServiceException("Unsupported ID type : "
					+ id.getReferenceType());
		}
	}

	public Object renderIdentifier(T2Reference id, Class<?> leafClass,
			ReferenceContext context) throws ReferenceServiceException {
		// Check we have the services installed
		checkServices();

		// Insert an empty context if context was null
		if (context == null) {
			context = new EmptyReferenceContext();
		}

		// Reject if the source reference contains errors
		if (id.containsErrors()) {
			throw new ReferenceServiceException(
					"Can't render an identifier which contains errors to a POJO");
		}

		// Attempt to find an appropriate StreamToValueConverterSPI instance to
		// build the specified class
		StreamToValueConverterSPI<?> converter = null;
		if (valueBuilderRegistry != null) {
			for (StreamToValueConverterSPI<?> stvc : valueBuilderRegistry) {
				Class<?> builtClass = stvc.getPojoClass();
				if (leafClass.isAssignableFrom(builtClass)) {
					converter = stvc;
					break;
				}
			}
		}
		if (converter == null) {
			log.warn("No stream->value converters found for type '"
					+ leafClass.getCanonicalName() + "'");
		}

		// Render the identifier
		return renderIdentifierInner(id, leafClass, context, converter);
	}

	private Object renderIdentifierInner(T2Reference id, Class<?> leafClass,
			ReferenceContext context, StreamToValueConverterSPI<?> converter)
			throws ReferenceServiceException {
		checkServices();

		switch (id.getReferenceType()) {

		case IdentifiedList:
			try {
				IdentifiedList<T2Reference> idList = listService.getList(id);
				List<Object> result = new ArrayList<Object>();
				for (T2Reference child : idList) {
					result.add(renderIdentifierInner(child, leafClass, context,
							converter));
				}
				return result;
			} catch (ListServiceException lse) {
				throw new ReferenceServiceException(lse);
			}

		case ReferenceSet:
			try {
				ReferenceSet rs = referenceSetService.getReferenceSet(id);
				// Check that there are references in the set
				if (rs.getExternalReferences().isEmpty()) {
					throw new ReferenceServiceException(
							"Can't render an empty reference set to a POJO");
				}
				// If we can't directly map to an appropriate value keep track
				// of the cheapest reference from which to try to build the pojo
				// from a stream
				ExternalReferenceSPI cheapestReference = null;
				float cheapestReferenceCost = 10.0f;
				for (ExternalReferenceSPI ers : rs.getExternalReferences()) {
					if (ers instanceof ValueCarryingExternalReference<?>) {
						ValueCarryingExternalReference<?> vcer = (ValueCarryingExternalReference<?>) ers;
						if (leafClass.isAssignableFrom(vcer.getValueType())) {
							return vcer.getValue();
						}
					}
					// Got here so this wasn't an appropriate value type
					if (cheapestReference == null
							|| ers.getResolutionCost() < cheapestReferenceCost) {
						cheapestReference = ers;
						cheapestReferenceCost = ers.getResolutionCost();
					}
				}
				if (converter != null) {
					return converter.renderFrom(cheapestReference
							.openStream(context));
				}
			} catch (Exception e) {
				throw new ReferenceServiceException(e);
			}
			throw new ReferenceServiceException(
					"No converter found, and reference set didn't contain"
							+ " an appropriate value carrying reference, cannot render to POJO");

		default:
			throw new ReferenceServiceException("Unsupported ID type : "
					+ id.getReferenceType());
		}
	}

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
	public Iterator<ContextualizedT2Reference> traverseFrom(T2Reference source,
			int desiredDepth) throws ReferenceServiceException {
		checkServices();
		if (desiredDepth < 0) {
			throw new ReferenceServiceException(
					"Cannot traverse to a negative depth");
		}
		List<ContextualizedT2Reference> workingSet = new ArrayList<ContextualizedT2Reference>();
		workingSet.add(new ContextualizedT2ReferenceImpl(source, new int[0]));
		int currentDepth = source.getDepth();
		while (currentDepth > desiredDepth) {
			List<ContextualizedT2Reference> newSet = new ArrayList<ContextualizedT2Reference>();
			for (ContextualizedT2Reference ci : workingSet) {
				T2ReferenceImpl ref = (T2ReferenceImpl) ci.getReference();
				switch (ref.getReferenceType()) {
				case IdentifiedList:
					try {
						List<T2Reference> children = getListService().getList(
								ref);
						int position = 0;
						for (T2Reference child : children) {
							newSet.add(new ContextualizedT2ReferenceImpl(child,
									addIndex(ci.getIndex(), position++)));
						}
					} catch (ListServiceException lse) {
						throw new ReferenceServiceException(lse);
					}
					break;
				case ReferenceSet:
					throw new ReferenceServiceException(
							"Should never be trying to drill inside a data document identifier");
				case ErrorDocument:
					newSet.add(new ContextualizedT2ReferenceImpl(ref
							.getDeeperErrorReference(), addIndex(ci.getIndex(),
							0)));
					break;
				default:
					throw new ReferenceServiceException(
							"Fallen off end of case statement, unknown reference type!");
				}
			}
			currentDepth--;
			workingSet = newSet;
		}
		return workingSet.iterator();
	}

	/**
	 * Append to an int[]
	 * 
	 * @param current
	 *            current int[]
	 * @param head
	 *            new int item to append to the current array
	 * @return new array of int with the head added
	 */
	private static int[] addIndex(int[] current, int head) {
		int[] result = new int[current.length + 1];
		System.arraycopy(current, 0, result, 0, current.length);
		result[current.length] = head;
		return result;
	}

	/**
	 * Parse the reference contained in the string and return a
	 * {@link T2Reference} with the correct properties
	 */
	public T2Reference referenceFromString(String reference) {
		T2ReferenceImpl newRef = new T2ReferenceImpl();
		Map<String, String> parseRef = parseRef(reference);
		newRef.setNamespacePart(parseRef.get("namespace"));
		newRef.setLocalPart(parseRef.get("localPart"));
		String type = parseRef.get("type");
		if (type.equals("ref")) {
			newRef.setReferenceType(T2ReferenceType.ReferenceSet);
		} else if (type.equals("list")) {
			newRef.setReferenceType(T2ReferenceType.IdentifiedList);
			newRef.setContainsErrors(Boolean
					.parseBoolean(parseRef.get("error")));
			newRef.setDepth(Integer.parseInt(parseRef.get("depth")));
		} else if (type.equals("error")) {
			newRef.setReferenceType(T2ReferenceType.ErrorDocument);
			newRef.setDepth(Integer.parseInt(parseRef.get("depth")));
		} else {
			return null;
			// should throw an error
		}

		return newRef;
	}

	/**
	 * Parse the reference and return a map with localPart, namespace, depth,
	 * contains errors and the type
	 * 
	 * @param ref
	 * @return
	 */
	private Map<String, String> parseRef(String ref) {
		String[] split = ref.split("\\?");
		// get the bit before and after the final '/' ie. the local part and the
		// depth, there might not be a split1[1] since it might not be a list
		String[] split2 = split[1].split("/");
		// get the t2:abc:// and the namespace
		String[] split3 = split[0].split("//");
		// get the t2 bit and the reference type bit
		String[] split4 = split3[0].split(":");

		Map<String, String> refPartsMap = new HashMap<String, String>();
		refPartsMap.put("type", split4[1]);
		refPartsMap.put("namespace", split3[1]);
		refPartsMap.put("localPart", split2[0]);

		if (refPartsMap.get("type").equals("list")) {
			refPartsMap.put("error", split2[1]);
			refPartsMap.put("depth", split2[2]);
		}
		if (refPartsMap.get("type").equals("error")) {
			refPartsMap.put("depth", split2[1]);
		}

		return refPartsMap;

	}	
	

	public boolean delete(List<T2Reference> references)
			throws ReferenceServiceException {
		boolean result=true;
		for (T2Reference reference : references) {
			delete(reference);
		}
		return result;
	}

	public boolean delete(T2Reference reference)
			throws ReferenceServiceException {
		boolean result=false;
		switch (reference.getReferenceType()) {
		case IdentifiedList:
			result=listService.delete(reference);
			break;
		case ReferenceSet:
			result=referenceSetService.delete(reference);
			break;
		case ErrorDocument:
			result=referenceSetService.delete(reference);
			break;
		default:
			throw new ReferenceServiceException(
					"Unknown reference type!");
		}
		return result;
	}

}
