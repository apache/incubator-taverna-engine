package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.taverna.raven.spi.InstanceRegistry;
import net.sf.taverna.t2.reference.ExternalReferenceBuilderSPI;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ExternalReferenceTranslatorSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceSet;

/**
 * A path from one external reference to another along with a total
 * estimated path cost through one or more reference translators.
 */
public class TranslationPath implements Comparable<TranslationPath>,
		Iterable<ExternalReferenceTranslatorSPI<?, ?>> {

	List<ExternalReferenceTranslatorSPI<?, ?>> translators = new ArrayList<ExternalReferenceTranslatorSPI<?, ?>>();
	ExternalReferenceBuilderSPI<?> initialBuilder = null;
	ExternalReferenceSPI sourceReference = null;
	InstanceRegistry<ExternalReferenceBuilderSPI<?>> builders;

	
	public TranslationPath(InstanceRegistry<ExternalReferenceBuilderSPI<?>> builders) {
		this.builders = builders;
	}

	/**
	 * Return a human readable representation of this translation path, used
	 * by the logging methods to print trace information.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getPathCost() + " ");
		if (sourceReference != null && initialBuilder != null) {
			sb.append(sourceReference.toString() + "->bytes("
					+ sourceReference.getResolutionCost() + ")->");
			String builderClassName = initialBuilder.getClass()
					.getSimpleName();
			String builtType = initialBuilder.getReferenceType()
					.getSimpleName();
			sb.append("builder:" + builderClassName + "("
					+ initialBuilder.getConstructionCost() + "):<"
					+ builtType + ">");
		} else if (! translators.isEmpty()) {
			sb.append("<"
					+ translators.get(0).getSourceReferenceType()
							.getSimpleName() + ">");
		}
		for (ExternalReferenceTranslatorSPI translator : translators) {
			sb.append("-" + translator.getClass().getSimpleName() + "("
					+ translator.getTranslationCost() + ")" + "-");
			sb.append("<"
					+ translator.getTargetReferenceType().getSimpleName()
					+ ">");
		}
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<ExternalReferenceSPI> doTranslation(ReferenceSet rs,
			ReferenceContext context) {
		Set<ExternalReferenceSPI> results = new HashSet<ExternalReferenceSPI>();
		// Firstly check whether we have an initial reference and builder
		// defined
		ExternalReferenceSPI currentReference = null;
		if (initialBuilder != null && sourceReference != null) {
			ExternalReferenceSPI builtReference = initialBuilder
					.createReference(sourceReference.openStream(context),
							context);
			results.add(builtReference);
			currentReference = builtReference;
		}
		if (!translators.isEmpty() && currentReference == null) {
			// If there are translators in the path (there may not be if
			// this is a pure 'dereference and build' type path) and the
			// currentReference hasn't been set then search the existing
			// references for an appropriate starting point for the
			// translation.
			for (ExternalReferenceSPI er : rs.getExternalReferences()) {
				if (er.getClass().equals(
						translators.get(0).getSourceReferenceType())) {
					currentReference = er;
					break;
				}
			}
		}
		if (currentReference == null) {
			throw new RuntimeException(
					"Can't locate a starting reference for the"
							+ " translation path");
		} else {
			for (ExternalReferenceTranslatorSPI translator : translators) {
				ExternalReferenceSPI translatedReference = translator
						.createReference(currentReference, context);
				results.add(translatedReference);
				currentReference = translatedReference;
			}
		}
		return results;
	}

	/**
	 * Sum of translation costs of all translators in path
	 */
	public float getPathCost() {
		float cost = 0.0f;
		for (ExternalReferenceTranslatorSPI<?, ?> ert : this) {
			cost += ert.getTranslationCost();
		}
		// If the source reference and initial builder are non-null then
		// we're going to start this translation path by downloading a byte
		// stream from the specified (current) reference and using it to
		// construct the starting point for the translation path via the
		// specified builder.
		if (sourceReference != null) {
			cost += sourceReference.getResolutionCost();
		}
		if (initialBuilder != null) {
			cost += initialBuilder.getConstructionCost();
		}
		return cost;
	}

	/**
	 * Return a list of translation paths based on this one but which start
	 * at an existing reference within the supplied reference set. Will only
	 * function if there is a reference builder registered that can build
	 * the initial reference type used by this translation path, otherwise
	 * it returns an empty list.
	 * 
	 * @param rs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<TranslationPath> getDereferenceBasedPaths(ReferenceSet rs) {
		List<TranslationPath> results = new ArrayList<TranslationPath>();
		for (ExternalReferenceBuilderSPI erb : builders) {
			// Check for each reference builder to see if it can build the
			// source type for this path
			if (erb.getReferenceType().equals(this.getSourceType())) {
				// The builder can construct the type used by the start of
				// this translation path, so we can in general create a path
				// from a fooreference to the target by de-referencing the
				// fooreference and building the start type from it.
				for (ExternalReferenceSPI er : rs.getExternalReferences()) {
					// For each external reference in the existing reference
					// set, check whether that type is already going to be
					// created in the translation path - if so then there's
					// not much point in emiting the modified path, as you'd
					// have something like bytes->a->b->a->result which
					// wouldn't make any sense
					boolean overlapsExistingType = false;
					for (ExternalReferenceTranslatorSPI translationStep : this) {
						if (translationStep.getSourceReferenceType()
								.equals(er.getClass())) {
							overlapsExistingType = true;
							break;
						}
					}
					if (!overlapsExistingType) {
						// The type wasn't found anywhere within the
						// translation path, so we're not generating
						// obviously stupid candidate paths.
						TranslationPath newPath = new TranslationPath(builders);
						newPath.translators = this.translators;
						newPath.initialBuilder = erb;
						newPath.sourceReference = er;
						results.add(newPath);
					}
				}
			}
		}
		return results;
	}

	public List<ExternalReferenceTranslatorSPI<?, ?>> pathSteps() {
		return translators;
	}

	/**
	 * Order by total path cost
	 */
	public int compareTo(TranslationPath tp) {
		if (tp.getPathCost() > this.getPathCost()) {
			return -1;
		} else if (tp.getPathCost() < this.getPathCost()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Wrap translator list iterator for convenience
	 */
	public Iterator<ExternalReferenceTranslatorSPI<?, ?>> iterator() {
		return translators.iterator();
	}

	public Class<? extends ExternalReferenceSPI> getSourceType() {
		if (! translators.isEmpty()) {
			return translators.get(0).getSourceReferenceType();
		} else if (this.sourceReference != null) {
			return this.sourceReference.getClass();
		} else {
			return null;
		}
	}

	public Class<? extends ExternalReferenceSPI> getTargetType() {
		if (! translators.isEmpty()) {
			return translators.get(translators.size() - 1)
					.getTargetReferenceType();
		} else if (this.initialBuilder != null) {
			return this.initialBuilder.getReferenceType();
		} else {
			return null;
		}
	}

}