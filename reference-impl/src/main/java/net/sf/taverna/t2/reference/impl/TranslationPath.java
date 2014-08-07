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

	private List<ExternalReferenceTranslatorSPI<?, ?>> translators = new ArrayList<ExternalReferenceTranslatorSPI<?, ?>>();
	private ExternalReferenceBuilderSPI<?> initialBuilder = null;
	private ExternalReferenceSPI sourceReference = null;
	private InstanceRegistry<ExternalReferenceBuilderSPI<?>> builders;

	
	public TranslationPath() {		
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
		if (getSourceReference() != null && getInitialBuilder() != null) {
			sb.append(getSourceReference().toString() + "->bytes("
					+ getSourceReference().getResolutionCost() + ")->");
			String builderClassName = getInitialBuilder().getClass()
					.getSimpleName();
			String builtType = getInitialBuilder().getReferenceType()
					.getSimpleName();
			sb.append("builder:" + builderClassName + "("
					+ getInitialBuilder().getConstructionCost() + "):<"
					+ builtType + ">");
		} else if (! getTranslators().isEmpty()) {
			sb.append("<"
					+ getTranslators().get(0).getSourceReferenceType()
							.getSimpleName() + ">");
		}
		for (ExternalReferenceTranslatorSPI translator : getTranslators()) {
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
		if (getInitialBuilder() != null && getSourceReference() != null) {
			ExternalReferenceSPI builtReference = getInitialBuilder()
					.createReference(getSourceReference().openStream(context),
							context);
			results.add(builtReference);
			currentReference = builtReference;
		}
		if (!getTranslators().isEmpty() && currentReference == null) {
			// If there are translators in the path (there may not be if
			// this is a pure 'dereference and build' type path) and the
			// currentReference hasn't been set then search the existing
			// references for an appropriate starting point for the
			// translation.
			for (ExternalReferenceSPI er : rs.getExternalReferences()) {
				if (er.getClass().equals(
						getTranslators().get(0).getSourceReferenceType())) {
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
			for (ExternalReferenceTranslatorSPI translator : getTranslators()) {
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
		if (getSourceReference() != null) {
			cost += getSourceReference().getResolutionCost();
		}
		if (getInitialBuilder() != null) {
			cost += getInitialBuilder().getConstructionCost();
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
		for (ExternalReferenceBuilderSPI erb : getBuilders()) {
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
						TranslationPath newPath = new TranslationPath();
						newPath.setBuilders(getBuilders());
						newPath.setTranslators(getTranslators());
						newPath.setInitialBuilder(erb);
						newPath.setSourceReference(er);
						results.add(newPath);
					}
				}
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
		return getTranslators().iterator();
	}

	public Class<? extends ExternalReferenceSPI> getSourceType() {
		if (! getTranslators().isEmpty()) {
			return getTranslators().get(0).getSourceReferenceType();
		} else if (this.getSourceReference() != null) {
			return this.getSourceReference().getClass();
		} else {
			return null;
		}
	}

	public Class<? extends ExternalReferenceSPI> getTargetType() {
		if (! getTranslators().isEmpty()) {
			return getTranslators().get(getTranslators().size() - 1)
					.getTargetReferenceType();
		} else if (this.getInitialBuilder() != null) {
			return this.getInitialBuilder().getReferenceType();
		} else {
			return null;
		}
	}

	public List<ExternalReferenceTranslatorSPI<?, ?>> getTranslators() {
		return translators;
	}

	public void setTranslators(List<ExternalReferenceTranslatorSPI<?, ?>> translators) {
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

	public InstanceRegistry<ExternalReferenceBuilderSPI<?>> getBuilders() {
		return builders;
	}

	public void setBuilders(InstanceRegistry<ExternalReferenceBuilderSPI<?>> builders) {
		this.builders = builders;
	}

}