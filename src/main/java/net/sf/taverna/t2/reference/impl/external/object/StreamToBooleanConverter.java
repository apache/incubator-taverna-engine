/**
 * 
 */
package net.sf.taverna.t2.reference.impl.external.object;

import java.io.InputStream;

import net.sf.taverna.t2.reference.StreamToValueConverterSPI;

/**
 * @author alanrw
 *
 */
public class StreamToBooleanConverter implements StreamToValueConverterSPI<Boolean> {

	public Class<Boolean> getPojoClass() {
		return Boolean.class;
	}

	public Boolean renderFrom(InputStream stream) {
		StreamToStringConverter stringConverter = new StreamToStringConverter();
		String s = stringConverter.renderFrom(stream);
		Boolean result  = Boolean.valueOf(s);
		return result;
	}

}
