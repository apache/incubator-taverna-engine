package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An organization. */
@label({"Organization"})
@term_status({"stable"})
@subClassOf({"http://xmlns.com/foaf/0.1/Agent"})
@comment({"An organization."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/Organization")
public interface Organization extends Agent {
}
