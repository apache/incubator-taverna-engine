package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import net.sf.taverna.t2.reference.DereferenceException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ExternalReferenceTranslatorSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.T2Reference;

import org.junit.Test;

public class ReferenceSetAugmentorTranslationPathTest {

	@SuppressWarnings("rawtypes")
	private final class DummyTranslator implements
			ExternalReferenceTranslatorSPI {
		@Override
		public ExternalReferenceSPI createReference(
				ExternalReferenceSPI sourceReference,
				ReferenceContext context) {
			return new TargetReference();
		}

		@Override
		public Class getSourceReferenceType() {
			return SourceReference.class;
		}

		@Override
		public Class getTargetReferenceType() {
			return TargetReference.class;
		}

		@Override
		public boolean isEnabled(ReferenceContext context) {
			return true;
		}

		@Override
		public float getTranslationCost() {
			return 1.5f;
		}
	}


	private final class DummyReferenceSet implements ReferenceSet {
		@Override
		public T2Reference getId() {
			return null;
		}

		@Override
		public Set<ExternalReferenceSPI> getExternalReferences() {
			return Collections.<ExternalReferenceSPI>singleton(new SourceReference());
		}

		@Override
		public Long getApproximateSizeInBytes() {
			return null;
		}
	}


	class DummyReference implements
			ExternalReferenceSPI {
		@Override
		public InputStream openStream(ReferenceContext context)
				throws DereferenceException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public float getResolutionCost() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ReferencedDataNature getDataNature() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCharset() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Long getApproximateSizeInBytes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ExternalReferenceSPI clone() {
			return null;
		}
	}
	
	class SourceReference extends DummyReference {
		
	}

	class TargetReference extends DummyReference {
	}
	
	private final class ReferenceSetAugmentorWithPath extends
			ReferenceSetAugmentorImpl {
		protected TranslationPath path  = new TranslationPath();
	}


	
	private ReferenceSetAugmentorWithPath augmentor = new ReferenceSetAugmentorWithPath();
	
	
	
	@Test
	public void doTranslationWithTranslator() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		ReferenceSet rs = new DummyReferenceSet();		
		augmentor.path.translators.add(new DummyTranslator());
		Set<ExternalReferenceSPI> set = augmentor.path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof TargetReference);
		
		
	}
}
