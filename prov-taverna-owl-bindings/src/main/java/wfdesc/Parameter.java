package wfdesc;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.equivalentClass;
import prov.Role;
import rdfs.comment;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

/** This class represents a parameter of a process. A parameter can be a input, an output. Note also that the same parameter can be both the input for one process and the output for another. This specifically occurs, when a workflow is nested within another workflow. */
@equivalentClass({"http://purl.org/wf4ever/wfdesc#Parameter"})
@subClassOf({"http://www.w3.org/ns/prov#Role"})
@comment({"This class represents a parameter of a process. A parameter can be a input, an output. Note also that the same parameter can be both the input for one process and the output for another. This specifically occurs, when a workflow is nested within another workflow."})
@Iri("http://purl.org/wf4ever/wfdesc#Parameter")
public interface Parameter extends Role {
	/** This property associates a parameter with an artifact. The artifact is used to describe the parameter, e.g., its data type, data structure, etc. */
	@comment({"This property associates a parameter with an artifact. The artifact is used to describe the parameter, e.g., its data type, data structure, etc."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasArtifact")
	Set<Artifact> getWfdescHasArtifacts();
	/** This property associates a parameter with an artifact. The artifact is used to describe the parameter, e.g., its data type, data structure, etc. */
	@comment({"This property associates a parameter with an artifact. The artifact is used to describe the parameter, e.g., its data type, data structure, etc."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasArtifact")
	void setWfdescHasArtifacts(Set<? extends Artifact> wfdescHasArtifacts);

}
