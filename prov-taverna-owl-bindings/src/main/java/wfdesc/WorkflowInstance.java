package wfdesc;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;

/** A workflow instance is a concrete workflow template, in which all the inputs required to enact the workflow template are bound. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#ConcreteWorkflowTemplate"})
@comment({"A workflow instance is a concrete workflow template, in which all the inputs required to enact the workflow template are bound."})
@Iri("http://purl.org/wf4ever/wfdesc#WorkflowInstance")
public interface WorkflowInstance extends ConcreteWorkflowTemplate {
}
