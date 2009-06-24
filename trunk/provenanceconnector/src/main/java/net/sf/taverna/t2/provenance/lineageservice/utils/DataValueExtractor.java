/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * @author paolo
 *
 */
public interface DataValueExtractor {

	/**
	 * extracts a printable string from a more complex object. This is not the same as toString() as 
	 * it is applied to an object, rather than being a method on the object itself
	 * @param complexContent should really be a byte array FIXME
	 * @return
	 */
	public String extractString(Object complexContent);
	
}
