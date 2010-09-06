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
public class StreamToDoubleConverter implements StreamToValueConverterSPI<Double> {

	public Class<Double> getPojoClass() {
		return Double.class;
	}

	public Double renderFrom(InputStream stream) {
		StreamToStringConverter stringConverter = new StreamToStringConverter();
		String s = stringConverter.renderFrom(stream);
		Double result  = Double.valueOf(s);
		return result;
	}

}
