/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.processor.activity;

import net.sf.taverna.t2.workflowmodel.Dataflow;

/**
 * @author alanrw
 *
 */
public interface NestedDataflowSource<T extends NestedDataflow> {

	public T getNestedDataflow();

	public Dataflow getParentDataflow();

	@Override
	public String toString();

}
