package scufl2;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import wfdesc.Process;

@label({"Processor"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Child", "http://ns.taverna.org.uk/2010/scufl2#Configurable", "http://ns.taverna.org.uk/2010/scufl2#Named", "http://purl.org/wf4ever/wfdesc#Process"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Processor")
public interface Processor extends Child, Configurable, Named, Process {
	@label({"dispatchStack"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#dispatchStack")
	DispatchStack getScufl2DispatchStack();
	@label({"dispatchStack"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#dispatchStack")
	void setScufl2DispatchStack(DispatchStack scufl2DispatchStack);

	@label({"Processor input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#processorPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputProcessorPort")
	Set<InputProcessorPort> getScufl2InputProcessorPorts();
	@label({"Processor input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#processorPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputProcessorPort")
	void setScufl2InputProcessorPorts(Set<? extends InputProcessorPort> scufl2InputProcessorPorts);

	@label({"iterationStrategyStack"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#iterationStrategyStack")
	IterationStrategyStack getScufl2IterationStrategyStack();
	@label({"iterationStrategyStack"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#iterationStrategyStack")
	void setScufl2IterationStrategyStack(IterationStrategyStack scufl2IterationStrategyStack);

	@label({"Processor output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#processorPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputProcessorPort")
	Set<OutputProcessorPort> getScufl2OutputProcessorPorts();
	@label({"Processor output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#processorPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputProcessorPort")
	void setScufl2OutputProcessorPorts(Set<? extends OutputProcessorPort> scufl2OutputProcessorPorts);

	@label({"Processor port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processorPort")
	Set<ProcessorPort> getScufl2ProcessorPorts();
	@label({"Processor port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processorPort")
	void setScufl2ProcessorPorts(Set<? extends ProcessorPort> scufl2ProcessorPorts);

	@label({"startConditions"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#startConditions")
	Set<StartCondition> getScufl2StartConditions();
	@label({"startConditions"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#startConditions")
	void setScufl2StartConditions(Set<? extends StartCondition> scufl2StartConditions);

}
