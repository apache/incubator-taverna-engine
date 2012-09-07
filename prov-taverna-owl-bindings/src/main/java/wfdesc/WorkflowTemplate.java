package wfdesc;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

/** A workflow template is used to describe a workflow. It can be thought of as  a directed graph in which the nodes are processes and the edges are data links that specify the flow of data between the constituent processes. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#Process"})
@comment({"A workflow template is used to describe a workflow. It can be thought of as  a directed graph in which the nodes are processes and the edges are data links that specify the flow of data between the constituent processes."})
@Iri("http://purl.org/wf4ever/wfdesc#WorkflowTemplate")
public interface WorkflowTemplate extends Process {
	/** This property is used to identify the data links that belong to a given workflow template. */
	@comment({"This property is used to identify the data links that belong to a given workflow template."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart", "http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasDataLink")
	Set<DataLink> getWfdescHasDataLink();
	/** This property is used to identify the data links that belong to a given workflow template. */
	@comment({"This property is used to identify the data links that belong to a given workflow template."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart", "http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasDataLink")
	void setWfdescHasDataLink(Set<? extends DataLink> wfdescHasDataLink);

	/** This object property is used to capture nesting between processes. Specifically, it is used to specify that the given process contains a given process. */
	@comment({"This object property is used to capture nesting between processes. Specifically, it is used to specify that the given process contains a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSubProcess")
	Set<Process> getWfdescHasSubProcesses();
	/** This object property is used to capture nesting between processes. Specifically, it is used to specify that the given process contains a given process. */
	@comment({"This object property is used to capture nesting between processes. Specifically, it is used to specify that the given process contains a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSubProcess")
	void setWfdescHasSubProcesses(Set<? extends Process> wfdescHasSubProcesses);

	/** This object property associates a workflow template with another workflow template, specifying that the former contains the latter. */
	@comment({"This object property associates a workflow template with another workflow template, specifying that the former contains the latter."})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSubProcess"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSubWorkflow")
	Set<WorkflowTemplate> getWfdescHasSubWorkflows();
	/** This object property associates a workflow template with another workflow template, specifying that the former contains the latter. */
	@comment({"This object property associates a workflow template with another workflow template, specifying that the former contains the latter."})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSubProcess"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSubWorkflow")
	void setWfdescHasSubWorkflows(Set<? extends WorkflowTemplate> wfdescHasSubWorkflows);

}
