package wfdesc;

import java.util.Set;
import org.openrdf.annotations.Iri;
import prov.Plan;
import rdfs.comment;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

/** 
 * A wfdesc:Process is used to describe a task that when enacted gives rise to a process run. A process can have 0 or more parameters.
 * 
 * It is out of scope for wfdesc to classify or specify the nature of the process, this should be possible through subclassing and using additional properties, for instance ex:perlScript or ex:restServiceURI.
 */
@subClassOf({"http://www.w3.org/ns/prov#Plan"})
@comment({"A wfdesc:Process is used to describe a task that when enacted gives rise to a process run. A process can have 0 or more parameters.\n" + 
	"\n" + 
	"It is out of scope for wfdesc to classify or specify the nature of the process, this should be possible through subclassing and using additional properties, for instance ex:perlScript or ex:restServiceURI."})
@Iri("http://purl.org/wf4ever/wfdesc#Process")
public interface Process extends Plan {
	/** This object property is used to specify the input parameter of a given process. */
	@comment({"This object property is used to specify the input parameter of a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasInput")
	Set<Input> getWfdescHasInputs();
	/** This object property is used to specify the input parameter of a given process. */
	@comment({"This object property is used to specify the input parameter of a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasInput")
	void setWfdescHasInputs(Set<? extends Input> wfdescHasInputs);

	/** This object property is used to specify the output parameter of a given process. */
	@comment({"This object property is used to specify the output parameter of a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasOutput")
	Set<Output> getWfdescHasOutputs();
	/** This object property is used to specify the output parameter of a given process. */
	@comment({"This object property is used to specify the output parameter of a given process."})
	@subPropertyOf({"http://purl.org/dc/terms/hasPart"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasOutput")
	void setWfdescHasOutputs(Set<? extends Output> wfdescHasOutputs);

}
