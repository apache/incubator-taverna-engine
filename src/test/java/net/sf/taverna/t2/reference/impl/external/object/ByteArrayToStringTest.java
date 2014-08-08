package net.sf.taverna.t2.reference.impl.external.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Set;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.impl.EmptyReferenceContext;
import net.sf.taverna.t2.reference.impl.TranslationPath;
import net.sf.taverna.t2referencetest.DummyReferenceSet;

import org.junit.Test;

public class ByteArrayToStringTest {

	protected TranslationPath path  = new TranslationPath();
	private final Charset UTF8 = Charset.forName("UTF-8");
//	
	private final String string = "Ferronni\u00e8re";
	
	
//	@Test
//	public void doTranslationWithTranslator() throws Exception {
//		ReferenceContext context = new EmptyReferenceContext();
//		InlineStringReference inlineString = new InlineStringReference();
//		inlineString.setContents("string");
//		//path.setSourceReference(inlineString);		
//		ReferenceSet rs = new DummyReferenceSet(inlineString);		
//		path.getTranslators().add(new GreenToRed());
//		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
//		assertEquals(1, set.size());
//		assertTrue(set.iterator().next() instanceof RedReference);
//	}
//	
	@Test
	public void translateStringToByteArrayBuilder() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		InlineStringReference inlineString = new InlineStringReference();
		inlineString.setContents(string);
		path.setSourceReference(inlineString);
		ReferenceSet rs = new DummyReferenceSet(path.getSourceReference());
		path.setInitialBuilder(new InlineByteArrayReferenceBuilder());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		InlineByteArrayReference byteRef = (InlineByteArrayReference) set.iterator().next();
		
		assertEquals(string, new String(byteRef.getValue(), UTF8));		
	}

	@Test
	public void translateByteArrayToStringBuilder() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		InlineByteArrayReference inlineByte = new InlineByteArrayReference();
		inlineByte.setValue(string.getBytes(UTF8));
		path.setSourceReference(inlineByte);
		ReferenceSet rs = new DummyReferenceSet(path.getSourceReference());
		path.setInitialBuilder(new InlineStringReferenceBuilder());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof InlineStringReference);
		InlineStringReference inlineString = (InlineStringReference) set.iterator().next();
		assertEquals(string,  inlineString.getContents());
		
	}
	
}
