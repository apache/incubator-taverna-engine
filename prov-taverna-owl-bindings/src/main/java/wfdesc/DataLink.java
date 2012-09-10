package wfdesc;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subPropertyOf;

/** DataLink is used to define data dependencies between processes. Specifically, a data link is used to connect an output parameter to an input parameter, specifying that the artifacts produced by the former are used to feed the latter. */
@comment({"DataLink is used to define data dependencies between processes. Specifically, a data link is used to connect an output parameter to an input parameter, specifying that the artifacts produced by the former are used to feed the latter."})
@Iri("http://purl.org/wf4ever/wfdesc#DataLink")
public interface DataLink {
	/** This property is used to specify the input parameter that acts as a sink from a given data Link. */
	@comment({"This property is used to specify the input parameter that acts as a sink from a given data Link."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSink")
	Set<Input> getWfdescHasSink();
	/** This property is used to specify the input parameter that acts as a sink from a given data Link. */
	@comment({"This property is used to specify the input parameter that acts as a sink from a given data Link."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSink")
	void setWfdescHasSink(Set<? extends Input> wfdescHasSink);

	/** This property is used to specify the output parameter that acts as a source to a given data link. */
	@comment({"This property is used to specify the output parameter that acts as a source to a given data link."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSource")
	Set<Output> getWfdescHasSources();
	/** This property is used to specify the output parameter that acts as a source to a given data link. */
	@comment({"This property is used to specify the output parameter that acts as a source to a given data link."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#topObjectProperty"})
	@Iri("http://purl.org/wf4ever/wfdesc#hasSource")
	void setWfdescHasSources(Set<? extends Output> wfdescHasSources);

}
