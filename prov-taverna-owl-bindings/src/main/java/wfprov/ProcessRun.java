package wfprov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import prov.Activity;
import rdfs.comment;
import rdfs.seeAlso;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import wfdesc.Process;

/** 
 * A process run is a particular execution of a wfdesc:Process description (wfprov:describedByProcess), which can wfprov:usedInput some wfprov:Artifact instances, and produce new artifacts (wfprov:wasOutputFrom). A wfprov:WorkflowRun is a specialisation of this class.
 * @see wfdesc.Process
 * @see wfprov.Artifact
 * @see wfprov.WorkflowRun
 * @see wfprov.ProcessRun#getWfprovDescribedByProcesses
 * @see wfprov.ProcessRun#getWfprovUsedInputs
 * @see wfprov.Artifact#getWfprovWasOutputFrom
 */
@seeAlso({"http://purl.org/wf4ever/wfdesc#Process", "http://purl.org/wf4ever/wfprov#Artifact", "http://purl.org/wf4ever/wfprov#WorkflowRun", "http://purl.org/wf4ever/wfprov#describedByProcess", "http://purl.org/wf4ever/wfprov#usedInput", "http://purl.org/wf4ever/wfprov#wasOutputFrom"})
@subClassOf({"http://www.w3.org/ns/prov#Activity"})
@comment({"A process run is a particular execution of a wfdesc:Process description (wfprov:describedByProcess), which can wfprov:usedInput some wfprov:Artifact instances, and produce new artifacts (wfprov:wasOutputFrom). A wfprov:WorkflowRun is a specialisation of this class."})
@Iri("http://purl.org/wf4ever/wfprov#ProcessRun")
public interface ProcessRun extends Activity {
	/** This object property associate a wfprov:Processrun to its wfdesc:Process description . */
	@comment({"This object property associate a wfprov:Processrun to its wfdesc:Process description ."})
	@Iri("http://purl.org/wf4ever/wfprov#describedByProcess")
	Set<Process> getWfprovDescribedByProcesses_1();
	/** This object property associate a wfprov:Processrun to its wfdesc:Process description . */
	@comment({"This object property associate a wfprov:Processrun to its wfdesc:Process description ."})
	@Iri("http://purl.org/wf4ever/wfprov#describedByProcess")
	void setWfprovDescribedByProcesses_1(Set<? extends Process> wfprovDescribedByProcesses_1);

	/** This property specifies that a wfprov:ProcessRun used an wfprov:Artifact as an input */
	@comment({"This property specifies that a wfprov:ProcessRun used an wfprov:Artifact as an input"})
	@subPropertyOf({"http://www.w3.org/ns/prov#used"})
	@Iri("http://purl.org/wf4ever/wfprov#usedInput")
	Set<Artifact> getWfprovUsedInputs_1();
	/** This property specifies that a wfprov:ProcessRun used an wfprov:Artifact as an input */
	@comment({"This property specifies that a wfprov:ProcessRun used an wfprov:Artifact as an input"})
	@subPropertyOf({"http://www.w3.org/ns/prov#used"})
	@Iri("http://purl.org/wf4ever/wfprov#usedInput")
	void setWfprovUsedInputs_1(Set<? extends Artifact> wfprovUsedInputs_1);

	/** wfprov:wasEnactedBy associates a wfprov:ProcessRun with a wfprov:WorkflowEngine, specifying that the execution of the process was enacted by the engine. */
	@comment({"wfprov:wasEnactedBy associates a wfprov:ProcessRun with a wfprov:WorkflowEngine, specifying that the execution of the process was enacted by the engine."})
	@Iri("http://purl.org/wf4ever/wfprov#wasEnactedBy")
	Set<WorkflowEngine> getWfprovWasEnactedBy();
	/** wfprov:wasEnactedBy associates a wfprov:ProcessRun with a wfprov:WorkflowEngine, specifying that the execution of the process was enacted by the engine. */
	@comment({"wfprov:wasEnactedBy associates a wfprov:ProcessRun with a wfprov:WorkflowEngine, specifying that the execution of the process was enacted by the engine."})
	@Iri("http://purl.org/wf4ever/wfprov#wasEnactedBy")
	void setWfprovWasEnactedBy(Set<? extends WorkflowEngine> wfprovWasEnactedBy);

	/** This property specifies that a wfprov:ProcessRun was executed as part of a wfprov:WorkflowRun. This typically corresponds to wfdesc:hasSubProcess in the workflow description. */
	@comment({"This property specifies that a wfprov:ProcessRun was executed as part of a wfprov:WorkflowRun. This typically corresponds to wfdesc:hasSubProcess in the workflow description."})
	@Iri("http://purl.org/wf4ever/wfprov#wasPartOfWorkflowRun")
	Set<WorkflowRun> getWfprovWasPartOfWorkflowRun();
	/** This property specifies that a wfprov:ProcessRun was executed as part of a wfprov:WorkflowRun. This typically corresponds to wfdesc:hasSubProcess in the workflow description. */
	@comment({"This property specifies that a wfprov:ProcessRun was executed as part of a wfprov:WorkflowRun. This typically corresponds to wfdesc:hasSubProcess in the workflow description."})
	@Iri("http://purl.org/wf4ever/wfprov#wasPartOfWorkflowRun")
	void setWfprovWasPartOfWorkflowRun(Set<? extends WorkflowRun> wfprovWasPartOfWorkflowRun);

}
