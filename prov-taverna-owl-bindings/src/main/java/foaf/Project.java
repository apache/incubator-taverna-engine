package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import vs.term_status;

/** A project (a collective endeavour of some kind). */
@label({"Project"})
@term_status({"testing"})
@comment({"A project (a collective endeavour of some kind)."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/Project")
public interface Project {
}
