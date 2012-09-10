package wfprov;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.seeAlso;
import rdfs.subPropertyOf;
import wfdesc.Parameter;

/** 
 * Artifact is a general concept that represents immutable piece of state, which may have a physical embodiment in a physical object, or a digital representation in a computer system. In the case of wfprov, an artifact is used as input to a process run, or produced by the output of a process run.
 * @see http://purl.org/wf4ever/ro#ResearchObject
 * @see http://purl.org/wf4ever/ro#Resource
 * @see wfprov.ProcessRun
 * @see wfprov.ProcessRun#getWfprovUsedInputs
 * @see wfprov.Artifact#getWfprovWasOutputFrom
 * @see http://purl.org/wf4ever/wfprov#workflowRun
 */
@seeAlso({"http://purl.org/wf4ever/ro#ResearchObject", "http://purl.org/wf4ever/ro#Resource", "http://purl.org/wf4ever/wfprov#ProcessRun", "http://purl.org/wf4ever/wfprov#usedInput", "http://purl.org/wf4ever/wfprov#wasOutputFrom", "http://purl.org/wf4ever/wfprov#workflowRun"})
@comment({"Artifact is a general concept that represents immutable piece of state, which may have a physical embodiment in a physical object, or a digital representation in a computer system. In the case of wfprov, an artifact is used as input to a process run, or produced by the output of a process run."})
@Iri("http://purl.org/wf4ever/wfprov#Artifact")
public interface Artifact {
	/** This object property is used to associate a wfprov:Artifact to the wfdesc:Parameter description. */
	@comment({"This object property is used to associate a wfprov:Artifact to the wfdesc:Parameter description."})
	@Iri("http://purl.org/wf4ever/wfprov#describedByParameter")
	Set<Parameter> getWfprovDescribedByParameters();
	/** This object property is used to associate a wfprov:Artifact to the wfdesc:Parameter description. */
	@comment({"This object property is used to associate a wfprov:Artifact to the wfdesc:Parameter description."})
	@Iri("http://purl.org/wf4ever/wfprov#describedByParameter")
	void setWfprovDescribedByParameters(Set<? extends Parameter> wfprovDescribedByParameters);

	/** This property specifies that a wfprov:Artifact was generated as an output from a wfprov:ProcessRun */
	@comment({"This property specifies that a wfprov:Artifact was generated as an output from a wfprov:ProcessRun"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasGeneratedBy"})
	@Iri("http://purl.org/wf4ever/wfprov#wasOutputFrom")
	Set<ProcessRun> getWfprovWasOutputFrom_1();
	/** This property specifies that a wfprov:Artifact was generated as an output from a wfprov:ProcessRun */
	@comment({"This property specifies that a wfprov:Artifact was generated as an output from a wfprov:ProcessRun"})
	@subPropertyOf({"http://www.w3.org/ns/prov#wasGeneratedBy"})
	@Iri("http://purl.org/wf4ever/wfprov#wasOutputFrom")
	void setWfprovWasOutputFrom_1(Set<? extends ProcessRun> wfprovWasOutputFrom_1);

}
