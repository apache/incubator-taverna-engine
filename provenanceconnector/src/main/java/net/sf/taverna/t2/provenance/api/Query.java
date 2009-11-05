/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

/**
 * @author paolo
 *
 */
public class Query {

	String workflowName;
	List<QueryVar> targetVars;
	List<String> runIDList;
	List<ProvenanceProcessor> selectedProcessors;


	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("QUERY SCOPE: ****\n").
		append("\tworkflow name: ").append(getWorkflowName()).

		append("\n\truns: ");
		for (String r:getRunIDList()) {
			sb.append("\n"+r);
		}

		sb.append("\nTARGET PORTS: ***\n");
		for (QueryVar v:getTargetVars()) {
			sb.append("\n"+v.toString());
		}

		sb.append("\nSELECTED PROCESSORS: ");
		for (ProvenanceProcessor pp:getSelectedProcessors()) {
			sb.append("\n"+pp.toString());
		}

		return sb.toString();
	}

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
	 * @return the selectedProcessors
	 */
	public List<ProvenanceProcessor>  getSelectedProcessors() {
		return selectedProcessors;
	}
	/**
	 * @param selectedProcessors the selectedProcessors to set
	 */
	public void setSelectedProcessors(List<ProvenanceProcessor>  selectedProcessors) {
		this.selectedProcessors = selectedProcessors;
	}
	/**
	 * @return the runIDList
	 */
	public List<String> getRunIDList() {
		return runIDList;
	}
	/**
	 * @param runIDList the runIDList to set
	 */
	public void setRunIDList(List<String> runIDList) {
		this.runIDList = runIDList;
	}

	/**
	 * @return the workflowName
	 */
	public String getWorkflowName() {
		return workflowName;
	}

	/**
	 * @param workflowName the workflowName to set
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
}
