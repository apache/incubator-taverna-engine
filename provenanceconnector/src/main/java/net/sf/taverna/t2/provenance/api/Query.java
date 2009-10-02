/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

/**
 * @author paolo
 *
 */
public class Query {
	 
	List<QueryVar> targetVars;
	String runID;
	Map<String, List<String>> selectedProcessors;
	
	/**
	 * @return the targetVars
	 */
	public List<QueryVar> getTargetVars() {
		return targetVars;
	}
	/**
	 * @param targetVars the targetVars to set
	 */
	public void setTargetVars(List<QueryVar> targetVars) {
		this.targetVars = targetVars;
	}
	/**
	 * @return the runID
	 */
	public String getRunID() {
		return runID;
	}
	/**
	 * @param runID the runID to set
	 */
	public void setRunID(String runID) {
		this.runID = runID;
	}
	/**
	 * @return the selectedProcessors
	 */
	public Map<String, List<String>> getSelectedProcessors() {
		return selectedProcessors;
	}
	/**
	 * @param selectedProcessors the selectedProcessors to set
	 */
	public void setSelectedProcessors(Map<String, List<String>> selectedProcessors) {
		this.selectedProcessors = selectedProcessors;
	}
}
