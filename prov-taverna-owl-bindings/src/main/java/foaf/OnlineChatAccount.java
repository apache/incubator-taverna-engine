package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An online chat account. */
@label({"Online Chat Account"})
@term_status({"unstable"})
@subClassOf({"http://xmlns.com/foaf/0.1/OnlineAccount"})
@comment({"An online chat account."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/OnlineChatAccount")
public interface OnlineChatAccount extends OnlineAccount {
}
