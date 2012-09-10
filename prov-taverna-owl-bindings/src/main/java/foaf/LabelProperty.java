package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import vs.term_status;

/** A foaf:LabelProperty is any RDF property with texual values that serve as labels. */
@label({"Label Property"})
@term_status({"unstable"})
@comment({"A foaf:LabelProperty is any RDF property with texual values that serve as labels."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/LabelProperty")
public interface LabelProperty {
}
