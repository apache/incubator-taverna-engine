package net.sf.taverna.t2.workflowmodel;

/**
 * 
 * An event saying that a processor with a given owning process has finished with execution
 * (that includes the whole dispatch stack - iterations of the processor and all).
 * 
 * @author Alex Nenadic
 *
 */
public class ProcessorFinishedEvent {

	private Processor processor;
	private String owningProcess;
	
	public ProcessorFinishedEvent(Processor processor, String owningProcess) {
		this.setProcessor(processor);
		this.setOwningProcess(owningProcess);
	}

	public void setOwningProcess(String owningProcess) {
		this.owningProcess = owningProcess;
	}

	public String getOwningProcess() {
		return owningProcess;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Processor getProcessor() {
		return processor;
	}

}
