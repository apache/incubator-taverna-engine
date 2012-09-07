package wfprov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.seeAlso;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import wfdesc.WorkflowTemplate;

/** 
 * A workflow run is a wfprov:ProcessRun which have been enacted by a wfprov:WorkflowEngine, according to a workflow definition (which could be wfdesc:describedByWorkflow a wfdesc:Workflow). Such a process typically contains several subprocesses (wfprov:wasPartOfWorkflowRun) corresponding to wfdesc:Process descriptions
 * @see wfdesc.Process
 * @see http://purl.org/wf4ever/wfdesc#Workflow
 * @see wfprov.WorkflowEngine
 */
@seeAlso({"http://purl.org/wf4ever/wfdesc#Process", "http://purl.org/wf4ever/wfdesc#Workflow", "http://purl.org/wf4ever/wfprov#WorkflowEngine"})
@subClassOf({"http://purl.org/wf4ever/wfprov#ProcessRun"})
@comment({"A workflow run is a wfprov:ProcessRun which have been enacted by a wfprov:WorkflowEngine, according to a workflow definition (which could be wfdesc:describedByWorkflow a wfdesc:Workflow). Such a process typically contains several subprocesses (wfprov:wasPartOfWorkflowRun) corresponding to wfdesc:Process descriptions"})
@Iri("http://purl.org/wf4ever/wfprov#WorkflowRun")
public interface WorkflowRun extends ProcessRun {
	/** This property associates a wfprov:WorkflowRun to its corresponding wfdesc:Workflow description. */
	@comment({"This property associates a wfprov:WorkflowRun to its corresponding wfdesc:Workflow description."})
	@subPropertyOf({"http://purl.org/wf4ever/wfprov#describedByProcess"})
	@Iri("http://purl.org/wf4ever/wfprov#describedByWorkflow")
	Set<WorkflowTemplate> getWfprovDescribedByWorkflows();
	/** This property associates a wfprov:WorkflowRun to its corresponding wfdesc:Workflow description. */
	@comment({"This property associates a wfprov:WorkflowRun to its corresponding wfdesc:Workflow description."})
	@subPropertyOf({"http://purl.org/wf4ever/wfprov#describedByProcess"})
	@Iri("http://purl.org/wf4ever/wfprov#describedByWorkflow")
	void setWfprovDescribedByWorkflows(Set<? extends WorkflowTemplate> wfprovDescribedByWorkflows);

}
