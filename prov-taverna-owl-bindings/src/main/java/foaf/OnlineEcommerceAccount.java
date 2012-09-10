package foaf;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An online e-commerce account. */
@label({"Online E-commerce Account"})
@term_status({"unstable"})
@subClassOf({"http://xmlns.com/foaf/0.1/OnlineAccount"})
@comment({"An online e-commerce account."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/OnlineEcommerceAccount")
public interface OnlineEcommerceAccount extends OnlineAccount {
}
