package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.DispatchStackImpl;

/**
 * 
 * Message saying that a processor with a given owning process has finished with execution.
 * See {@link ProcessorImpl} and {@link DispatchStackImpl#finishedWith(String owningProcess)} method.
 * 
 * @author Alex Nenadic
 *
 */
public class ProcessorFinishedMessage {

	private String owningProcess;
	
	public ProcessorFinishedMessage(String owningProcess) {
		this.setOwningProcess(owningProcess);
	}

	public void setOwningProcess(String owningProcess) {
		this.owningProcess = owningProcess;
	}

	public String getOwningProcess() {
		return owningProcess;
	}

}
