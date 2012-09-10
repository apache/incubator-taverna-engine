/** 
 * The wfprov ontology shows how to express minimal provenance information about the execution of a workflow described using the wf ontology. 
 * 
 * Here the concern is mainly the provenance which affects the research object, so in particular how some ro:Resource's might have been generated or used by the execution of a wfdesc:Workflow.
 * 
 * The main class here is a wfprov:WorkflowRun which shows how wfprov:Artifact instances (the data) wfprov:wasOutputFrom a wfprov:ProcessRun for generated artifacts, or wfprov:usedInput for consumed artifacts. The WorkflowRun is also a ProcessRun, and so the overall inputs and outputs of thw workflow execution can be described in the same manner.
 * 
 * These provenance details are linked to the corresponding wfdesc descriptions using wfdesc:describedbyProcess, wfdesc:describedByWorkflow and wfdesc:describedByParameter.
 * 
 * 
 * This ontology can be further linked to more specific provenance ontologies like OPM-V or W3C PROV-O, but this should be done separately.
 * @see http://purl.org/wf4ever/ro
 */
@seeAlso({"http://purl.org/wf4ever/ro"})
@comment({"The wfprov ontology shows how to express minimal provenance information about the execution of a workflow described using the wf ontology. \n" + 
	"\n" + 
	"Here the concern is mainly the provenance which affects the research object, so in particular how some ro:Resource's might have been generated or used by the execution of a wfdesc:Workflow.\n" + 
	"\n" + 
	"The main class here is a wfprov:WorkflowRun which shows how wfprov:Artifact instances (the data) wfprov:wasOutputFrom a wfprov:ProcessRun for generated artifacts, or wfprov:usedInput for consumed artifacts. The WorkflowRun is also a ProcessRun, and so the overall inputs and outputs of thw workflow execution can be described in the same manner.\n" + 
	"\n" + 
	"These provenance details are linked to the corresponding wfdesc descriptions using wfdesc:describedbyProcess, wfdesc:describedByWorkflow and wfdesc:describedByParameter.\n" + 
	"\n" + 
	"\n" + 
	"This ontology can be further linked to more specific provenance ontologies like OPM-V or W3C PROV-O, but this should be done separately."})
@Prefix("wfprov")
@Iri("http://purl.org/wf4ever/wfprov#")
package wfprov;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Prefix;
import rdfs.comment;
import rdfs.seeAlso;

