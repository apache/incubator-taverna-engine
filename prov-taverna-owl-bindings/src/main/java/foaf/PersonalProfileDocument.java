package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** A personal profile RDF document. */
@label({"PersonalProfileDocument"})
@term_status({"testing"})
@subClassOf({"http://xmlns.com/foaf/0.1/Document"})
@comment({"A personal profile RDF document."})
@Iri("http://xmlns.com/foaf/0.1/PersonalProfileDocument")
public interface PersonalProfileDocument extends Document {
}
