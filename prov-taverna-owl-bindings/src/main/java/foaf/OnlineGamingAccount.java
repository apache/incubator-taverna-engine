package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An online gaming account. */
@label({"Online Gaming Account"})
@term_status({"unstable"})
@subClassOf({"http://xmlns.com/foaf/0.1/OnlineAccount"})
@comment({"An online gaming account."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/OnlineGamingAccount")
public interface OnlineGamingAccount extends OnlineAccount {
}
