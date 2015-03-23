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

package org.apache.taverna.reference.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetAugmentationException;
import org.apache.taverna.reference.ReferenceSetAugmentor;
import org.apache.taverna.reference.ReferenceSetAugmentorCallback;

import org.apache.log4j.Logger;

/**
 * Implementation of ReferenceSetAugmentor using Dijkstra's shortest path
 * algorithm over a type graph built from SPI instance registries of reference
 * builders and reference translators.
 * 
 * @author Tom Oinn
 */
public class ReferenceSetAugmentorImpl implements ReferenceSetAugmentor {
	private final Logger log = Logger
			.getLogger(ReferenceSetAugmentorImpl.class);

	/**
	 * A list of ExternalReferenceBuilderSPI instances used to construct
	 * ExternalReferenceSPI instances from byte streams
	 */
	private List<ExternalReferenceBuilderSPI<?>> builders;

	/**
	 * A list of ExternalReferenceTranslatorSPI instances used to construct
	 * ExternalReferenceSPI instances from existing ExternalReferenceSPI
	 * instances.
	 */
	private List<ExternalReferenceTranslatorSPI<?, ?>> translators;

	private boolean cacheValid = false;

	private final Set<Class<ExternalReferenceSPI>> knownReferenceTypes = new HashSet<>();
	@SuppressWarnings("rawtypes")
	private final Map<Class<ExternalReferenceSPI>, Set<ExternalReferenceTranslatorSPI>> adjacencySets = new HashMap<>();
	private final Map<Class<ExternalReferenceSPI>, ShortestPathSolver> solvers = new HashMap<>();

	/**
	 * Default constructor to make life easier when using Spring. To be
	 * functional this implementation should be injected with InstanceRegistry
	 * implementations containing lists of known implementations of the
	 * ExternalReferenceBuilderSPI and ExternalReferenceTranslatorSPI
	 * interfaces.
	 */
	public ReferenceSetAugmentorImpl() {
		super();
	}

	public void buildersUpdated(Object service, Map<?, ?> properties) {
		cacheValid = false;
	}

	public void translatorsUpdated(Object service, Map<?, ?> properties) {
		cacheValid = false;
	}

	/**
	 * Inject a list containing all known implementations of
	 * ExternalReferenceBuilderSPI.
	 * 
	 * @throws IllegalStateException
	 *             if this has already been set, the instance registries should
	 *             only be set on bean construction.
	 */
	public synchronized void setBuilders(
			List<ExternalReferenceBuilderSPI<?>> builders) {
		if (this.builders != null) {
			log.error("Builder registry already injected, invalid operation");
			throw new IllegalStateException(
					"Can't inject the external reference builder registry "
							+ "multiple times.");
		}

		this.builders = builders;
		if (log.isDebugEnabled()) {
			log.debug("* Builders injected :");
			int counter = 0;
			for (ExternalReferenceBuilderSPI<?> builder : builders)
				log.debug("*   " + (++counter) + ") "
						+ builder.getClass().getSimpleName() + ", builds "
						+ builder.getReferenceType().getSimpleName());
		}
		cacheValid = false;
	}

	/**
	 * Inject a list containing all known implementations of
	 * ExternalReferenceTranslatorSPI.
	 * 
	 * @throws IllegalStateException
	 *             if this has already been set, the instance registries should
	 *             only be set on bean construction.
	 */
	public synchronized void setTranslators(
			List<ExternalReferenceTranslatorSPI<?, ?>> translators) {
		if (this.translators == null) {
			this.translators = translators;
			if (log.isDebugEnabled()) {
				log.debug("* Translators injected :");
				int counter = 0;
				for (ExternalReferenceTranslatorSPI<?, ?> translator : translators)
					log.debug("*   "
							+ (++counter)
							+ ") "
							+ translator.getClass().getSimpleName()
							+ ", translates "
							+ translator.getSourceReferenceType()
									.getSimpleName()
							+ " to "
							+ translator.getTargetReferenceType()
									.getSimpleName());
			}
			cacheValid = false;
		} else {
			log.error("Translator registry already injected, invalid operation");
			throw new IllegalStateException(
					"Can't inject the translator registry multiple times.");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected synchronized final void update() {
		if (builders == null || translators == null || cacheValid)
			return;
		log.debug("# Refreshing shortest path cache");
		knownReferenceTypes.clear();
		solvers.clear();
		adjacencySets.clear();
		for (ExternalReferenceBuilderSPI erb : builders)
			knownReferenceTypes.add(erb.getReferenceType());
		for (ExternalReferenceTranslatorSPI ert : translators) {
			knownReferenceTypes.add(ert.getSourceReferenceType());
			knownReferenceTypes.add(ert.getTargetReferenceType());
			getNeighbours(ert.getTargetReferenceType()).add(ert);
		}
		for (Class<ExternalReferenceSPI> type : knownReferenceTypes)
			try {
				solvers.put(type, new ShortestPathSolver(type));
			} catch (Throwable t) {
				log.error(t);
				if (t instanceof RuntimeException)
					throw (RuntimeException) t;
			}
		log.debug("# Path cache refresh done");
		cacheValid = true;
	}

	@SuppressWarnings("rawtypes")
	Set<ExternalReferenceTranslatorSPI> getNeighbours(
			Class<ExternalReferenceSPI> node) {
		Set<ExternalReferenceTranslatorSPI> adjacentTo = adjacencySets
				.get(node);
		if (adjacentTo != null)
			return adjacentTo;

		HashSet<ExternalReferenceTranslatorSPI> neighbours = new HashSet<>();
		adjacencySets.put(node, neighbours);
		return neighbours;
	}

	@Override
	public final Set<ExternalReferenceSPI> augmentReferenceSet(
			ReferenceSet references,
			Set<Class<ExternalReferenceSPI>> targetReferenceTypes,
			ReferenceContext context) throws ReferenceSetAugmentationException {
		synchronized (this) {
			if (!cacheValid)
				update();
		}

		// Synchronize on the reference set itself
		synchronized (references) {
			/*
			 * First check whether we actually need to modify the reference set
			 * at all - it's perfectly valid to call the augmentor when nothing
			 * actually needs to be done (ideally you wouldn't do this, but it's
			 * likely to happen)
			 */
			for (ExternalReferenceSPI er : references.getExternalReferences())
				if (targetReferenceTypes.contains(er.getClass()))
					return new HashSet<>();

			// Need to perform augmentation if we reach this point
			List<TranslationPath> candidatePaths = new ArrayList<>();
			for (Class<ExternalReferenceSPI> target : targetReferenceTypes) {
				ShortestPathSolver solver = solvers.get(target);
				if (solver == null) {
					solver = new ShortestPathSolver(target);
					solvers.put(target, solver);
				}
				if (solver != null)
					for (TranslationPath path : solver.getTranslationPaths()) {
						for (ExternalReferenceSPI er : references
								.getExternalReferences())
							if (er.getClass().equals(path.getSourceType()))
								candidatePaths.add(path);
						for (TranslationPath dereferenceBasedPath : path
								.getDereferenceBasedPaths(references))
							candidatePaths.add(dereferenceBasedPath);
					}
			}
			/*
			 * Now add candidate paths to represent a no-translator 'direct from
			 * byte stream source' path for each target type compatible
			 * reference builder
			 */
			for (ExternalReferenceBuilderSPI<?> builder : builders)
				if (targetReferenceTypes.contains(builder.getReferenceType()))
					/*
					 * The builder can construct one of the target types, add
					 * paths for all possible pairs of 'de-reference existing
					 * reference' and the builder
					 */
					for (ExternalReferenceSPI er : references
							.getExternalReferences()) {
						TranslationPath newPath = new TranslationPath();
						newPath.setBuilders(builders);
						newPath.setInitialBuilder(builder);
						newPath.setSourceReference(er);
						candidatePaths.add(newPath);
					}

			/*
			 * Got a list of candidate paths sorted by estimated overall path
			 * cost
			 */
			Collections.sort(candidatePaths);
			if (log.isDebugEnabled()) {
				log.debug("Found "
						+ candidatePaths.size()
						+ " contextual translation path(s) including builder based :");
				int counter = 0;
				for (TranslationPath path : candidatePaths)
					log.debug("  " + (++counter) + ") " + path.toString());
			}

			if (candidatePaths.isEmpty()) {
				log.warn("No candidate paths found for augmentation");
				throw new ReferenceSetAugmentationException(
						"No candidate translation paths were found");
			}

			log.debug("Performing augmentation :");
			int counter = 0;
			for (TranslationPath path : candidatePaths)
				try {
					counter++;
					Set<ExternalReferenceSPI> newReferences = path
							.doTranslation(references, context);
					if (log.isDebugEnabled())
						log.debug("  Success (" + counter + "), created "
								+ printRefSet(newReferences));
					return newReferences;
				} catch (Exception ex) {
					log.debug("  Failed (" + counter + ")");
					log.trace(ex);
					// Use next path...
				}
			log.warn("  No paths succeeded, augmentation failed");
			throw new ReferenceSetAugmentationException(
					"All paths threw exceptions, can't perform augmentation");
		}
	}

	private String printRefSet(Set<ExternalReferenceSPI> set) {
		StringBuilder sb = new StringBuilder("[");
		String sep = "";
		for (ExternalReferenceSPI ref : set) {
			sb.append(sep).append(ref.toString());
			sep = ",";
		}
		return sb.append("]").toString();
	}

	@Override
	public final void augmentReferenceSetAsynch(final ReferenceSet references,
			final Set<Class<ExternalReferenceSPI>> targetReferenceTypes,
			final ReferenceContext context,
			final ReferenceSetAugmentorCallback callback)
			throws ReferenceSetAugmentationException {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					callback.augmentationCompleted(augmentReferenceSet(
							references, targetReferenceTypes, context));
				} catch (ReferenceSetAugmentationException rsae) {
					callback.augmentationFailed(rsae);
				}
			}
		};
		executeRunnable(r);
	}

	/**
	 * Schedule a runnable for execution - current naive implementation uses a
	 * new thread and executes immediately, but this is where any thread pool
	 * logic would go if we wanted to add that.
	 * 
	 * @param r
	 */
	private void executeRunnable(Runnable r) {
		new Thread(r).start();
	}

	class ShortestPathSolver {
		private Map<Class<ExternalReferenceSPI>, Class<ExternalReferenceSPI>> predecessors;
		private Map<Class<ExternalReferenceSPI>, ExternalReferenceTranslatorSPI<?, ?>> translators;
		private Map<Class<ExternalReferenceSPI>, Float> shortestDistances;
		private final Comparator<Class<ExternalReferenceSPI>> shortestDistanceComparator = new Comparator<Class<ExternalReferenceSPI>>() {
			@Override
			public int compare(Class<ExternalReferenceSPI> left,
					Class<ExternalReferenceSPI> right) {
				float shortestDistanceLeft = shortestDistances.get(left);
				float shortestDistanceRight = shortestDistances.get(right);
				if (shortestDistanceLeft > shortestDistanceRight)
					return +1;
				if (shortestDistanceLeft < shortestDistanceRight)
					return -1;
				return left.getCanonicalName().compareTo(
						right.getCanonicalName());
			}
		};
		private final PriorityQueue<Class<ExternalReferenceSPI>> unsettledNodes = new PriorityQueue<>(
				10, shortestDistanceComparator);
		private final Set<Class<ExternalReferenceSPI>> settledNodes = new HashSet<>();

		private final List<TranslationPath> translationPaths = new ArrayList<>();

		public List<TranslationPath> getTranslationPaths() {
			return this.translationPaths;
		}

		public ShortestPathSolver(Class<ExternalReferenceSPI> targetType) {
			log.debug("# Constructing shortest paths to '"
					+ targetType.getSimpleName() + "'");
			predecessors = new HashMap<>();
			translators = new HashMap<>();
			shortestDistances = new HashMap<>();
			setShortestDistance(targetType, 0.0f);
			unsettledNodes.add(targetType);
			while (!unsettledNodes.isEmpty()) {
				Class<ExternalReferenceSPI> u = extractMin();
				settledNodes.add(u);
				relaxNeighbours(u);
			}
			for (Class<ExternalReferenceSPI> c : settledNodes)
				if (!c.equals(targetType)) {
					// Don't calculate a path to itself!
					TranslationPath p = new TranslationPath();
					p.setBuilders(builders);
					Class<ExternalReferenceSPI> node = c;
					while (predecessors.get(node) != null) {
						p.pathSteps().add(translators.get(node));
						// Recurse, should terminate at the target type
						node = predecessors.get(node);
					}
					translationPaths.add(p);
				}
			Collections.sort(translationPaths);
			if (translationPaths.isEmpty())
				log.debug("#   no paths discovered, type not reachable through translation");
			else if (log.isDebugEnabled()) {
				log.debug("#   found " + translationPaths.size()
						+ " distinct path(s) :");
				int counter = 0;
				for (TranslationPath path : translationPaths)
					log.debug("#     " + (++counter) + ") " + path);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void relaxNeighbours(Class<ExternalReferenceSPI> u) {
			log.trace("#     relaxing node " + u.getSimpleName());
			Set<Class<ExternalReferenceSPI>> alreadySeen = new HashSet<>();
			for (ExternalReferenceTranslatorSPI ert : getNeighbours(u)) {
				// all the translators that translate *to* u
				Class<ExternalReferenceSPI> v = ert.getSourceReferenceType();
				log.trace("#     translator found from from '" + v + "' : "
						+ ert.getClass().getSimpleName());
				if (!alreadySeen.contains(v) && !isSettled(v)) {
					/*
					 * Avoid duplicate edges, always take the first one where
					 * such duplicates exist
					 */
					alreadySeen.add(v);
					if (getShortestDistance(v) > getShortestDistance(u)
							+ ert.getTranslationCost()) {
						setShortestDistance(
								v,
								getShortestDistance(u)
										+ ert.getTranslationCost());
						setPredecessor(v, u, ert);
						unsettledNodes.add(v);
					}
				}
			}
		}

		private boolean isSettled(Class<ExternalReferenceSPI> node) {
			return settledNodes.contains(node);
		}

		private void setShortestDistance(Class<ExternalReferenceSPI> node,
				float distance) {
			shortestDistances.put(node, distance);
		}

		private float getShortestDistance(Class<ExternalReferenceSPI> node) {
			Float d = shortestDistances.get(node);
			return (d == null) ? Float.MAX_VALUE : d;
		}

		private Class<ExternalReferenceSPI> extractMin() {
			return unsettledNodes.poll();
		}

		private void setPredecessor(Class<ExternalReferenceSPI> child,
				Class<ExternalReferenceSPI> parent,
				ExternalReferenceTranslatorSPI<?, ?> translator) {
			predecessors.put(child, parent);
			translators.put(child, translator);
		}
	}
}
