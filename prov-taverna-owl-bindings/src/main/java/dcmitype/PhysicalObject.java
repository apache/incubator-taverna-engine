package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Note that digital representations of, or surrogates for, these objects should use Image, Text or one of the other types. */
@label({"Physical Object"})
@definition({"An inanimate, three-dimensional object or substance."})
@comment({"Note that digital representations of, or surrogates for, these objects should use Image, Text or one of the other types."})
@Iri("http://purl.org/dc/dcmitype/PhysicalObject")
public interface PhysicalObject {
}
