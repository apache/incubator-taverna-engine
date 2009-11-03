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
public class StreamToIntegerConverter implements StreamToValueConverterSPI<Integer> {

	public Class<Integer> getPojoClass() {
		return Integer.class;
	}

	public Integer renderFrom(InputStream stream) {
		StreamToStringConverter stringConverter = new StreamToStringConverter();
		String s = stringConverter.renderFrom(stream);
		Integer result  = Integer.valueOf(s);
		return result;
	}

}
