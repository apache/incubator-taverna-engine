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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.taverna.reference.DereferenceException;
import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceSet;

/**
 * A path from one external reference to another along with a total estimated
 * path cost through one or more reference translators.
 */
public class TranslationPath implements Comparable<TranslationPath>,
		Iterable<ExternalReferenceTranslatorSPI<?, ?>> {
	private List<ExternalReferenceTranslatorSPI<?, ?>> translators = new ArrayList<>();
	private ExternalReferenceBuilderSPI<?> initialBuilder = null;
	private ExternalReferenceSPI sourceReference = null;
	private List<ExternalReferenceBuilderSPI<?>> builders;

	public TranslationPath() {
	}

	/**
	 * Return a human readable representation of this translation path, used by
	 * the logging methods to print trace information.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getPathCost() + " ");
		if (getSourceReference() != null && getInitialBuilder() != null) {
			sb.append(getSourceReference()).append("->bytes(")
					.append(getSourceReference().getResolutionCost())
					.append(")->");
			String builderClassName = getInitialBuilder().getClass()
					.getSimpleName();
			String builtType = getInitialBuilder().getReferenceType()
					.getSimpleName();
			sb.append("builder:").append(builderClassName).append("(")
					.append(getInitialBuilder().getConstructionCost())
					.append("):<").append(builtType).append(">");
		} else if (!getTranslators().isEmpty())
			sb.append("<")
					.append(getTranslators().get(0).getSourceReferenceType()
							.getSimpleName()).append(">");
		for (ExternalReferenceTranslatorSPI translator : getTranslators())
			sb.append("-")
					.append(translator.getClass().getSimpleName())
					.append("(")
					.append(translator.getTranslationCost())
					.append(")-<")
					.append(translator.getTargetReferenceType().getSimpleName())
					.append(">");
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<ExternalReferenceSPI> doTranslation(ReferenceSet rs,
			ReferenceContext context) {
		Set<ExternalReferenceSPI> results = new HashSet<>();
		/*
		 * Firstly check whether we have an initial reference and builder
		 * defined
		 */
		ExternalReferenceSPI currentReference = null;
		if (getInitialBuilder() != null && getSourceReference() != null)
			try (InputStream stream = getSourceReference().openStream(context)) {
				ExternalReferenceSPI builtReference = getInitialBuilder()
						.createReference(stream, context);
				results.add(builtReference);
				currentReference = builtReference;
			} catch (IOException e) {
				throw new DereferenceException(
						"Can't create reference from stream", e);
			}
		if (!getTranslators().isEmpty() && currentReference == null)
			/*
			 * If there are translators in the path (there may not be if this is
			 * a pure 'dereference and build' type path) and the
			 * currentReference hasn't been set then search the existing
			 * references for an appropriate starting point for the translation.
			 */
			for (ExternalReferenceSPI er : rs.getExternalReferences())
				if (er.getClass().equals(
						getTranslators().get(0).getSourceReferenceType())) {
					currentReference = er;
					break;
				}
		if (currentReference == null)
			throw new RuntimeException(
					"Can't locate a starting reference for the"
							+ " translation path");

		for (ExternalReferenceTranslatorSPI translator : getTranslators()) {
			ExternalReferenceSPI translatedReference = translator
					.createReference(currentReference, context);
			results.add(translatedReference);
			currentReference = translatedReference;
		}
		return results;
	}

	/**
	 * Sum of translation costs of all translators in path
	 */
	public float getPathCost() {
		float cost = 0.0f;
		for (ExternalReferenceTranslatorSPI<?, ?> ert : this)
			cost += ert.getTranslationCost();
		/*
		 * If the source reference and initial builder are non-null then we're
		 * going to start this translation path by downloading a byte stream
		 * from the specified (current) reference and using it to construct the
		 * starting point for the translation path via the specified builder.
		 */
		if (getSourceReference() != null)
			cost += getSourceReference().getResolutionCost();
		if (getInitialBuilder() != null)
			cost += getInitialBuilder().getConstructionCost();
		return cost;
	}

	/**
	 * Return a list of translation paths based on this one but which start at
	 * an existing reference within the supplied reference set. Will only
	 * function if there is a reference builder registered that can build the
	 * initial reference type used by this translation path, otherwise it
	 * returns an empty list.
	 * 
	 * @param rs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<TranslationPath> getDereferenceBasedPaths(ReferenceSet rs) {
		List<TranslationPath> results = new ArrayList<>();
		for (ExternalReferenceBuilderSPI erb : getBuilders())
			/*
			 * Check for each reference builder to see if it can build the
			 * source type for this path
			 */
			if (erb.getReferenceType().equals(this.getSourceType()))
				/*
				 * The builder can construct the type used by the start of this
				 * translation path, so we can in general create a path from a
				 * fooreference to the target by de-referencing the fooreference
				 * and building the start type from it.
				 */
				for (ExternalReferenceSPI er : rs.getExternalReferences()) {
					/*
					 * For each external reference in the existing reference
					 * set, check whether that type is already going to be
					 * created in the translation path - if so then there's not
					 * much point in emiting the modified path, as you'd have
					 * something like bytes->a->b->a->result which wouldn't make
					 * any sense
					 */
					boolean overlapsExistingType = false;
					for (ExternalReferenceTranslatorSPI translationStep : this)
						if (translationStep.getSourceReferenceType().equals(
								er.getClass())) {
							overlapsExistingType = true;
							break;
						}
					if (!overlapsExistingType) {
						/*
						 * The type wasn't found anywhere within the translation
						 * path, so we're not generating obviously stupid
						 * candidate paths.
						 */
						TranslationPath newPath = new TranslationPath();
						newPath.setBuilders(getBuilders());
						newPath.setTranslators(getTranslators());
						newPath.setInitialBuilder(erb);
						newPath.setSourceReference(er);
						results.add(newPath);
					}
				}
		return results;
	}

	public List<ExternalReferenceTranslatorSPI<?, ?>> pathSteps() {
		return getTranslators();
	}

	/**
	 * Order by total path cost
	 */
	@Override
	public int compareTo(TranslationPath tp) {
		float tpCost = tp.getPathCost();
		float myCost = getPathCost();
		if (tpCost > myCost)
			return -1;
		if (tpCost < myCost)
			return 1;
		return 0;
	}

	/**
	 * Wrap translator list iterator for convenience
	 */
	@Override
	public Iterator<ExternalReferenceTranslatorSPI<?, ?>> iterator() {
		return getTranslators().iterator();
	}

	public Class<? extends ExternalReferenceSPI> getSourceType() {
		if (!getTranslators().isEmpty())
			return getTranslators().get(0).getSourceReferenceType();
		if (getSourceReference() != null)
			return getSourceReference().getClass();
		return null;
	}

	public Class<? extends ExternalReferenceSPI> getTargetType() {
		if (!getTranslators().isEmpty())
			return getTranslators().get(getTranslators().size() - 1)
					.getTargetReferenceType();
		if (getInitialBuilder() != null)
			return getInitialBuilder().getReferenceType();
		return null;
	}

	public List<ExternalReferenceTranslatorSPI<?, ?>> getTranslators() {
		return translators;
	}

	public void setTranslators(
			List<ExternalReferenceTranslatorSPI<?, ?>> translators) {
		this.translators = translators;
	}

	public ExternalReferenceBuilderSPI<?> getInitialBuilder() {
		return initialBuilder;
	}

	public void setInitialBuilder(ExternalReferenceBuilderSPI<?> initialBuilder) {
		this.initialBuilder = initialBuilder;
	}

	public ExternalReferenceSPI getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(ExternalReferenceSPI sourceReference) {
		this.sourceReference = sourceReference;
	}

	public List<ExternalReferenceBuilderSPI<?>> getBuilders() {
		return builders;
	}

	public void setBuilders(List<ExternalReferenceBuilderSPI<?>> builders) {
		this.builders = builders;
	}
}