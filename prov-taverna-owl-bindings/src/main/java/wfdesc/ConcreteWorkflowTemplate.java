package wfdesc;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;

/** A ConcreteWorkflowTemplate is a workflow template, the constituent processes of which are all bound to implementations. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#WorkflowTemplate"})
@comment({"A ConcreteWorkflowTemplate is a workflow template, the constituent processes of which are all bound to implementations."})
@Iri("http://purl.org/wf4ever/wfdesc#ConcreteWorkflowTemplate")
public interface ConcreteWorkflowTemplate extends WorkflowTemplate {
}
