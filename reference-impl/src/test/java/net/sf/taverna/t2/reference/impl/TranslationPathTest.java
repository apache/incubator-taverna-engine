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

public class TranslationPathTest {

	protected TranslationPath path  = new TranslationPath(null);
	
	@Test
	public void doTranslationWithTranslator() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		ReferenceSet rs = new DummyReferenceSet(new GreenReference("green"));		
		path.translators.add(new GreenToRed());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof RedReference);
	}
	
	@Test
	public void doTranslationByReadingStream() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		path.sourceReference = new RedReference("red");
		ReferenceSet rs = new DummyReferenceSet(path.sourceReference);
		path.initialBuilder = new GreenBuilder();
		//augmentor.path.translators.add(new DummyTranslator());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof GreenReference);
	}

	
}
