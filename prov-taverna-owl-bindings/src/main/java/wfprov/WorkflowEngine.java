package wfprov;

import org.openrdf.annotations.Iri;
import prov.Agent;
import rdfs.comment;
import rdfs.seeAlso;
import rdfs.subClassOf;

/** 
 * A workflow engine is an foaf:Agent that is responsible for enacting a workflow definition (which could be described in a wfdesc:Workflow). The result of workflow enactment gives rise to a wfprov:WorkflowRun.
 * @see http://purl.org/wf4ever/wfdesc#Workflow
 * @see wfprov.WorkflowRun
 */
@seeAlso({"http://purl.org/wf4ever/wfdesc#Workflow", "http://purl.org/wf4ever/wfprov#WorkflowRun"})
@subClassOf({"http://www.w3.org/ns/prov#Agent", "http://xmlns.com/foaf/0.1/Agent"})
@comment({"A workflow engine is an foaf:Agent that is responsible for enacting a workflow definition (which could be described in a wfdesc:Workflow). The result of workflow enactment gives rise to a wfprov:WorkflowRun."})
@Iri("http://purl.org/wf4ever/wfprov#WorkflowEngine")
public interface WorkflowEngine extends Agent, foaf.Agent {
}
