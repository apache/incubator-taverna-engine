/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

/**
 * @author paolo
 *
 */
public class NestedListNode {

	String collId;
	String parentCollIdRef;
	String wfInstanceRef;
	String processorNameRef;
	String varNameRef;
	String iteration;
	/**
	 * @return the collId
	 */
	public String getCollId() {
		return collId;
	}
	/**
	 * @param collId the collId to set
	 */
	public void setCollId(String collId) {
		this.collId = collId;
	}
	/**
	 * @return the parentCollIdRef
	 */
	public String getParentCollIdRef() {
		return parentCollIdRef;
	}
	/**
	 * @param parentCollIdRef the parentCollIdRef to set
	 */
	public void setParentCollIdRef(String parentCollIdRef) {
		this.parentCollIdRef = parentCollIdRef;
	}
	/**
	 * @return the wfInstanceRef
	 */
	public String getWfInstanceRef() {
		return wfInstanceRef;
	}
	/**
	 * @param wfInstanceRef the wfInstanceRef to set
	 */
	public void setWfInstanceRef(String wfInstanceRef) {
		this.wfInstanceRef = wfInstanceRef;
	}
	/**
	 * @return the processorNameRef
	 */
	public String getprocessorNameRef() {
		return processorNameRef;
	}
	/**
	 * @param nameRef the processorNameRef to set
	 */
	public void setprocessorNameRef(String nameRef) {
		processorNameRef = nameRef;
	}
	/**
	 * @return the varNameRef
	 */
	public String getVarNameRef() {
		return varNameRef;
	}
	/**
	 * @param varNameRef the varNameRef to set
	 */
	public void setVarNameRef(String varNameRef) {
		this.varNameRef = varNameRef;
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
