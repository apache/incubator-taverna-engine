package scufl2;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import wfdesc.WorkflowTemplate;

@label({"Workflow"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Named", "http://purl.org/wf4ever/wfdesc#WorkflowTemplate"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Workflow")
public interface Workflow extends Named, WorkflowTemplate {
	@label({"datalink"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasDataLink"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#datalink")
	Set<DataLink> getScufl2Datalinks();
	@label({"datalink"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasDataLink"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#datalink")
	void setScufl2Datalinks(Set<? extends DataLink> scufl2Datalinks);

	@label({"Workflow input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflowPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputWorkflowPort")
	Set<InputWorkflowPort> getScufl2InputWorkflowPorts();
	@label({"Workflow input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflowPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputWorkflowPort")
	void setScufl2InputWorkflowPorts(Set<? extends InputWorkflowPort> scufl2InputWorkflowPorts);

	@label({"Workflow output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflowPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputWorkflowPort")
	Set<OutputWorkflowPort> getScufl2OutputWorkflowPorts();
	@label({"Workflow output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflowPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputWorkflowPort")
	void setScufl2OutputWorkflowPorts(Set<? extends OutputWorkflowPort> scufl2OutputWorkflowPorts);

	@label({"processor"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processor")
	Set<Processor> getScufl2Processors();
	@label({"processor"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#processor")
	void setScufl2Processors(Set<? extends Processor> scufl2Processors);

	@label({"Workflow identifier"})
	@subPropertyOf({"http://purl.org/dc/terms/identifier"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflowIdentifier")
	Object getScufl2WorkflowIdentifier();
	@label({"Workflow identifier"})
	@subPropertyOf({"http://purl.org/dc/terms/identifier"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflowIdentifier")
	void setScufl2WorkflowIdentifier(Object scufl2WorkflowIdentifier);

	@label({"Workflow port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflowPort")
	Set<WorkflowPort> getScufl2WorkflowPorts();
	@label({"Workflow port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflowPort")
	void setScufl2WorkflowPorts(Set<? extends WorkflowPort> scufl2WorkflowPorts);

}
