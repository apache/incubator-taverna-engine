package net.sf.taverna.t2.workflowmodel.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.raven.appconfig.ApplicationRuntime;
import net.sf.taverna.raven.spi.SpiRegistry;
import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.AnnotationChainImpl;
import net.sf.taverna.t2.annotation.AppliesTo;
import net.sf.taverna.t2.annotation.annotationbeans.AbstractTextualValueAssertion;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;

import org.apache.log4j.Logger;

public class AnnotationTools {

	private static Logger logger = Logger.getLogger(AnnotationTools.class);

	@SuppressWarnings("unchecked")
	private Iterable<Class> annotationBeanRegistry;

	private Edits edits;

	public AnnotationTools() {
		setAnnotationBeanRegistry(getSpiRegistry());
		setEdits(EditsRegistry.getEdits());
	}

	protected static Iterable<Class> getSpiRegistry() {
		return new SpiRegistry(ApplicationRuntime
				.getInstance().getRavenRepository(), AnnotationBeanSPI.class
				.getCanonicalName(), AnnotationTools.class.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	public AnnotationTools(Iterable<Class> annotationBeanRegistry, Edits edits) {
		setAnnotationBeanRegistry(annotationBeanRegistry);
		setEdits(edits);
	}

	public Edit<?> addAnnotation(Annotated<?> annotated, AnnotationBeanSPI a) {
		return getEdits().getAddAnnotationChainEdit(annotated, a);
	}

	private Edits getEdits() {
		return edits;
	}

	@SuppressWarnings("unchecked")
	public AnnotationBeanSPI getAnnotation(Annotated<?> annotated,
			Class annotationClass) {
		AnnotationBeanSPI result = null;
		Date latestDate = null;
		for (AnnotationChain chain : annotated.getAnnotations()) {
			for (AnnotationAssertion<?> assertion : chain.getAssertions()) {
				AnnotationBeanSPI detail = assertion.getDetail();
				if (annotationClass.isInstance(detail)) {
					Date assertionDate = assertion.getCreationDate();
					if ((latestDate == null)
							|| latestDate.before(assertionDate)) {
						result = detail;
						latestDate = assertionDate;
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Iterable<Class<? extends AnnotationBeanSPI>> getAnnotationBeanClasses() {
		// Mega casting mega trick!
		Iterable registry = getAnnotationBeanRegistry();
		return (Iterable<Class<? extends AnnotationBeanSPI>>) registry;
	}

	@SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> getAnnotationBeanClasses(
			Class<T> superClass) {
		List<Class<? extends T>> results = new ArrayList<Class<? extends T>>();
		for (Class<? extends AnnotationBeanSPI> annotationBeanClass : getAnnotationBeanClasses()) {
			if (superClass.isAssignableFrom(annotationBeanClass)) {
				results.add((Class<? extends T>) annotationBeanClass);
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<Class> getAnnotatingClasses(Annotated annotated) {
		List<Class> result = new ArrayList<Class>();
		for (Class<? extends AbstractTextualValueAssertion> c : getAnnotationBeanClasses(AbstractTextualValueAssertion.class)) {
			AppliesTo appliesToAnnotation = (AppliesTo) c
					.getAnnotation(AppliesTo.class);
			if (appliesToAnnotation == null) {
				continue;
			}
			for (Class<?> target : appliesToAnnotation.targetObjectType()) {
				if (target.isInstance(annotated)) {
					result.add(c);
				}
			}
		}
		return result;
	}

	public Edit<?> setAnnotationString(Annotated<?> annotated, Class<?> c,
			String value) {
		AbstractTextualValueAssertion a = null;
		try {
			logger.info("Setting " + c.getCanonicalName() + " to " + value);
			a = (AbstractTextualValueAssertion) c.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		a.setText(value);
		return (addAnnotation(annotated, a));
	}

	@SuppressWarnings("unchecked")
	public String getAnnotationString(Annotated<?> annotated,
			Class annotationClass, String missingValue) {
		AbstractTextualValueAssertion a = (AbstractTextualValueAssertion) getAnnotation(
				annotated, annotationClass);
		if (a == null) {
			return missingValue;
		}
		return a.getText();
	}

	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	public void setAnnotationBeanRegistry(Iterable<Class> annotationBeanRegistry) {
		this.annotationBeanRegistry = annotationBeanRegistry;
	}

	public Iterable<Class> getAnnotationBeanRegistry() {
		return annotationBeanRegistry;
	}

    /**
     * Remove out of date annotations unless many of that class are allowed, or it is explicitly not pruned
     */
	public static void pruneAnnotations(Annotated<?> annotated) {
		Map<Class<AnnotationBeanSPI>, AnnotationAssertion> remainder = new HashMap<Class<AnnotationBeanSPI>, AnnotationAssertion>();
		Set<AnnotationChain> newChains = new HashSet<AnnotationChain>();
		for (AnnotationChain chain : annotated.getAnnotations()) {
			AnnotationChainImpl newChain = new AnnotationChainImpl();
			for (AnnotationAssertion assertion : chain.getAssertions()) {
				AnnotationBeanSPI annotation = assertion.getDetail();
				Class annotationClass = annotation.getClass();
				AppliesTo appliesToAnnotation = (AppliesTo) annotationClass
						.getAnnotation(AppliesTo.class);
				if ((appliesToAnnotation == null) || appliesToAnnotation.many()
						|| !appliesToAnnotation.pruned()) {
					newChain.addAnnotationAssertion(assertion);
				} else {
					if (remainder.containsKey(annotationClass)) {
						AnnotationAssertion currentAssertion = remainder
								.get(annotationClass);
						if (assertion.getCreationDate().compareTo(
								currentAssertion.getCreationDate()) > 0) {
							remainder.put(annotationClass, assertion);
						}
					} else {
						remainder.put(annotationClass, assertion);
					}
				}
			}
			if (!newChain.getAssertions().isEmpty()) {
				newChains.add(newChain);
			}
		}
		for (AnnotationAssertion assertion : remainder.values()) {
			AnnotationChainImpl newChain = new AnnotationChainImpl();
			newChain.addAnnotationAssertion(assertion);
			newChains.add(newChain);
		}
		annotated.setAnnotations(newChains);
	}

}
