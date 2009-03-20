package net.sf.taverna.t2.workflowmodel;

/**
 * An input or output {@link Port} for a {@link Merge}.
 * 
 * @see MergeInputPort
 * @see MergeOutputPort
 * @author Stian Soiland-Reyes
 *
 */
public interface MergePort extends Port {
	
	/**
	 * @return the Merge instance the port is associated with. 
	 */
	public Merge getMerge();

}
