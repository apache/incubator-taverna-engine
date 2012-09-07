package wfdesc;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;

/** ConfigurationInput is an input parameter that is used to tune the task performed by the associated processor, e.g., to specify the algorithm used by the processor or the underlying database accessed, etc. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#Input"})
@comment({"ConfigurationInput is an input parameter that is used to tune the task performed by the associated processor, e.g., to specify the algorithm used by the processor or the underlying database accessed, etc."})
@Iri("http://purl.org/wf4ever/wfdesc#ConfigurationInput")
public interface ConfigurationInput extends Input {
}
