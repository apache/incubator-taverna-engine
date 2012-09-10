/** 
 * This ontology ("wfdesc") describes an abstract workflow description structure, which on the top level is defined as a wfdesc:Workflow. 
 * 
 * A wfdesc:Workflow contains several wfdesc:Process instances, associated using the wfdesc:hasSubProcess property. Each of these (and the workflow itself) wfdesc:hasInput and wfdesc:hasOutput some wfdesc:Parameter (wfdesc:Input or wfdesc:Output). An wfdesc:Artifact is associated with a wfdesc:Parameter using wfdesc:hasArtifact. The wfdesc:Workflow also wfdesc:hasDataLink several wfdesc:DataLink instances, which forms the connection between parameters.
 * 
 * Thus this ontology allows the description a direct acyclic graph, or a dataflow. 
 * 
 * This ontology is meant as an upper ontology for more specific workflow definitions, and as a way to express abstract workflows. The wfprov ontology shows how to link these descriptions to a provenance trace of a workflow execution.
 */
@comment({"This ontology (\"wfdesc\") describes an abstract workflow description structure, which on the top level is defined as a wfdesc:Workflow. \n" + 
	"\n" + 
	"A wfdesc:Workflow contains several wfdesc:Process instances, associated using the wfdesc:hasSubProcess property. Each of these (and the workflow itself) wfdesc:hasInput and wfdesc:hasOutput some wfdesc:Parameter (wfdesc:Input or wfdesc:Output). An wfdesc:Artifact is associated with a wfdesc:Parameter using wfdesc:hasArtifact. The wfdesc:Workflow also wfdesc:hasDataLink several wfdesc:DataLink instances, which forms the connection between parameters.\n" + 
	"\n" + 
	"Thus this ontology allows the description a direct acyclic graph, or a dataflow. \n" + 
	"\n" + 
	"This ontology is meant as an upper ontology for more specific workflow definitions, and as a way to express abstract workflows. The wfprov ontology shows how to link these descriptions to a provenance trace of a workflow execution."})
@Prefix("wfdesc")
@Iri("http://purl.org/wf4ever/wfdesc#")
package wfdesc;

import org.openrdf.annotations.Iri;
import org.openrdf.annotations.Prefix;
import rdfs.comment;

