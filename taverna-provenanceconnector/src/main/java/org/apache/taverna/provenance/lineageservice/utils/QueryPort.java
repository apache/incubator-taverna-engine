/**
 * 
 */
package org.apache.taverna.provenance.lineageservice.utils;

/**
 * @author Paolo Missier
 *         <p/>
 * 
 */
public class QueryPort {
	private String workflowRunId;
	private String workflowId;
	private String processorName;
	private String portName;
	private String path;
	private String value;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PORT: ****").append("\nworkflow: " + getWorkflowId())
				.append("\nprocessor: " + getProcessorName())
				.append("\nport: " + getPortName())
				.append("\npath to value: " + getPath());

		return sb.toString();
	}

	/**
	 * @return the processorName
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * @param processorName
	 *            the processorName to set
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	/**
	 * @return the vname
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param vname
	 *            the vname to set
	 */
	public void setPortName(String vname) {
		this.portName = vname;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the workflowRunId
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}

	/**
	 * @param workflowRunId
	 *            the workflowRunId to set
	 */
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	/**
	 * @return the workflowId
	 */
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * @param workflowId
	 *            the workflowId to set
	 */
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}