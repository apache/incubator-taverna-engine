package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2referencetest.DummyReferenceSet;
import net.sf.taverna.t2referencetest.GreenBuilder;
import net.sf.taverna.t2referencetest.GreenReference;
import net.sf.taverna.t2referencetest.GreenToRed;
import net.sf.taverna.t2referencetest.RedReference;

import org.junit.Test;

public class ReferenceSetAugmentorTranslationPathTest {

	private final class ReferenceSetAugmentorWithPath extends
			ReferenceSetAugmentorImpl {
		protected TranslationPath path  = new TranslationPath();
	}

	private ReferenceSetAugmentorWithPath augmentor = new ReferenceSetAugmentorWithPath();
	
	@Test
	public void doTranslationWithTranslator() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		ReferenceSet rs = new DummyReferenceSet(new GreenReference("green"));		
		augmentor.path.translators.add(new GreenToRed());
		Set<ExternalReferenceSPI> set = augmentor.path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof RedReference);
	}
	
	@Test
	public void doTranslationByReadingStream() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		augmentor.path.sourceReference = new RedReference("red");
		ReferenceSet rs = new DummyReferenceSet(augmentor.path.sourceReference);
		augmentor.path.initialBuilder = new GreenBuilder();
		//augmentor.path.translators.add(new DummyTranslator());
		Set<ExternalReferenceSPI> set = augmentor.path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof GreenReference);
	}

	
}
