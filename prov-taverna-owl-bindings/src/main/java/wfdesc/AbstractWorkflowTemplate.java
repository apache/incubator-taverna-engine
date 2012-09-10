package wfdesc;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;

/** AbstractWorkflowTemplate is a workflow template where some or all of the processes of the workflow are not bound to a specific implementations. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#WorkflowTemplate"})
@comment({"AbstractWorkflowTemplate is a workflow template where some or all of the processes of the workflow are not bound to a specific implementations."})
@Iri("http://purl.org/wf4ever/wfdesc#AbstractWorkflowTemplate")
public interface AbstractWorkflowTemplate extends WorkflowTemplate {
}
