/**
 * 
 */
package net.sf.taverna.t2.reference.impl.external.object;

import java.io.InputStream;

import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.StreamToValueConverterSPI;

/**
 * @author alanrw
 * 
 */
public class StreamToBooleanConverter implements
		StreamToValueConverterSPI<Boolean> {

	@Override
	public Class<Boolean> getPojoClass() {
		return Boolean.class;
	}

	@Override
	public Boolean renderFrom(InputStream stream,
			ReferencedDataNature dataNature, String charset) {
		StreamToStringConverter stringConverter = new StreamToStringConverter();
		String s = stringConverter.renderFrom(stream, dataNature, charset);
		Boolean result = Boolean.valueOf(s);
		return result;

	}

}
