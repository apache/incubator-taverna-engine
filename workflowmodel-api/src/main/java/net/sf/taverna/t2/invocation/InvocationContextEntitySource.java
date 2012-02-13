/**
 * 
 */
package net.sf.taverna.t2.invocation;

/**
 * 
 * This class is used so that entities can be placed in the InvocationContext when it is created.
 * 
 * @author alanrw
 *
 */
public interface InvocationContextEntitySource {
	
	Object getEntity();

}
