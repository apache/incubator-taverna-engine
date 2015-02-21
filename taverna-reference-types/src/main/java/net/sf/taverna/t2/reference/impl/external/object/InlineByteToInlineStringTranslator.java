package net.sf.taverna.t2.reference.impl.external.object;

import java.io.UnsupportedEncodingException;

import net.sf.taverna.t2.reference.ExternalReferenceConstructionException;
import net.sf.taverna.t2.reference.ExternalReferenceTranslatorSPI;
import net.sf.taverna.t2.reference.ReferenceContext;

public class InlineByteToInlineStringTranslator
		implements
		ExternalReferenceTranslatorSPI<InlineByteArrayReference, InlineStringReference> {
	@Override
	public InlineStringReference createReference(
			InlineByteArrayReference sourceReference, ReferenceContext context) {
		String contents;
		try {
			String charset = sourceReference.getCharset();
			if (charset == null)
				// usual fallback:
				charset = "UTF-8";
			contents = new String(sourceReference.getValue(), charset);
		} catch (UnsupportedEncodingException e) {
			String msg = "Unknown character set "
					+ sourceReference.getCharset();
			throw new ExternalReferenceConstructionException(msg, e);
		}
		InlineStringReference ref = new InlineStringReference();
		ref.setContents(contents);
		return ref;
	}

	@Override
	public Class<InlineByteArrayReference> getSourceReferenceType() {
		return InlineByteArrayReference.class;
	}

	@Override
	public Class<InlineStringReference> getTargetReferenceType() {
		return InlineStringReference.class;
	}

	@Override
	public boolean isEnabled(ReferenceContext context) {
		return true;
	}

	@Override
	public float getTranslationCost() {
		return 0.001f;
	}
}
