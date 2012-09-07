package wfdesc;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.subClassOf;

/** This class represents an input parameter of a wfdesc:Process. */
@subClassOf({"http://purl.org/wf4ever/wfdesc#Parameter"})
@comment({"This class represents an input parameter of a wfdesc:Process."})
@Iri("http://purl.org/wf4ever/wfdesc#Input")
public interface Input extends Parameter {
}
