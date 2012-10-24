/**
 * 
 */
package uk.org.taverna.platform.data.api;

/**
 * @author alanrw
 * 
 */
public enum DataNature {
	/**
	 * NOT_YET_ARRIVED indicates that during the workflow run it is expected
	 * that a corresponding data value or reference to a value will be
	 * generated. This occurs for example when the elements in a list are not
	 * generated in order.
	 * 
	 * WILL_NOT_COME indicates that no value or reference will ever be
	 * generated. This normally happens because a service invocation upstream
	 * has generated an error.
	 * 
	 * NULL indicates that an actual null has been generated, for example if a
	 * service invocation indicates that no value was generated. It is not an
	 * error for a null value to be generated.
	 * 
	 * TEXT_VALUE indicates that the value is text or that the references of the
	 * Data are to text.
	 * 
	 * BINARY_VALUE indicates that the value of the Data should be handled as a
	 * binary value.
	 * 
	 * LIST indicates that the Data contains a possibly empty list of elements.
	 */
	NOT_YET_ARRIVED,
	WILL_NOT_COME,
	// I am not convinced NULL is needed
	NULL,
	TEXT_VALUE,
	BINARY_VALUE,
	LIST

}
