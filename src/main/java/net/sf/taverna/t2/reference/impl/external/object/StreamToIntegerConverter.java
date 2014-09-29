/**
 * 
 */
package net.sf.taverna.t2.reference.impl.external.object;

import java.io.InputStream;

import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.StreamToValueConverterSPI;

/**
 * @author alanrw
 */
public class StreamToIntegerConverter implements
		StreamToValueConverterSPI<Integer> {
	@Override
	public Class<Integer> getPojoClass() {
		return Integer.class;
	}

	@Override
	public Integer renderFrom(InputStream stream,
			ReferencedDataNature dataNature, String charset) {
		StreamToStringConverter stringConverter = new StreamToStringConverter();
		String s = stringConverter.renderFrom(stream, dataNature, charset);
		Integer result = Integer.valueOf(s.trim());
		return result;
	}
}
