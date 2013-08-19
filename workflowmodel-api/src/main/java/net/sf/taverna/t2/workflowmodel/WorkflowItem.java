package net.sf.taverna.t2.workflowmodel;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;

/**
 * An item that forms a structural part of a Workflow.
 * 
 * Workflow item are {@link Dataflow}, its {@link Processor} and {@link Port}
 * s, and other deeper structural parts like {@link DispatchStack} and
 * {@link IterationStrategy}.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public interface WorkflowItem {

    // TODO: Implement this for every WorkflowItem
    
//    /**
//     * Mark this item (and its child WorkflowItems) as immutable.
//     * 
//     * Subsequent edits to its structural components will
//     * throw a RuntimeException like UnsupportedOperationException.
//     * 
//     */
//    public void setImmutable();
    
}
