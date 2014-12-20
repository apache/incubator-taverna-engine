package net.sf.taverna.t2referencetest;

import java.util.Collections;
import java.util.Set;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.T2Reference;

public class DummyReferenceSet implements ReferenceSet {
	
	private Set<ExternalReferenceSPI> refs;

	public DummyReferenceSet(ExternalReferenceSPI ref) {
		refs = Collections.singleton(ref);
	}
	
	@Override
	public T2Reference getId() {
		return null;
	}

	@Override
	public Set<ExternalReferenceSPI> getExternalReferences() {
		return refs;
	}

	@Override
	public Long getApproximateSizeInBytes() {
		return null;
	}
}