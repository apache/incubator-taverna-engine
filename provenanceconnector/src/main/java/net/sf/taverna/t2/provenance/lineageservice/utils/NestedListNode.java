/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * @author paolo
 *
 */
public class NestedListNode {

	String collectionT2Reference;
	String parentCollectionT2Reference;
	String workflowRunId;
	String processorName;
	String portName;
	String iteration;
	/**
	 * @return the collectionT2Reference
	 */
	public String getCollectionT2Reference() {
		return collectionT2Reference;
	}
	/**
	 * @param collectionT2Reference the collectionT2Reference to set
	 */
	public void setCollectionT2Reference(String collectionT2Reference) {
		this.collectionT2Reference = collectionT2Reference;
	}
	/**
	 * @return the parentCollIdRef
	 */
	public String getParentCollIdRef() {
		return parentCollectionT2Reference;
	}
	/**
	 * @param parentCollIdRef the parentCollIdRef to set
	 */
	public void setParentCollIdRef(String parentCollIdRef) {
		this.parentCollectionT2Reference = parentCollIdRef;
	}
	/**
	 * @return the workflowRunId
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}
	/**
	 * @param workflowRunId the workflowRunId to set
	 */
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}
	/**
	 * @return the processorNameRef
	 */
	public String getProcessorName() {
		return processorName;
	}
	/**
	 * @param nameRef the processorNameRef to set
	 */
	public void setProcessorName(String nameRef) {
		processorName = nameRef;
	}
	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}
	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}
	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	
	
}
