package foaf;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.Thing;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An online account. */
@label({"Online Account"})
@term_status({"testing"})
@subClassOf({"http://www.w3.org/2002/07/owl#Thing"})
@comment({"An online account."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/OnlineAccount")
public interface OnlineAccount extends Thing {
	/** Indicates the name (identifier) associated with this online account. */
	@label({"account name"})
	@term_status({"testing"})
	@comment({"Indicates the name (identifier) associated with this online account."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/accountName")
	Set<Object> getFoafAccountNames();
	/** Indicates the name (identifier) associated with this online account. */
	@label({"account name"})
	@term_status({"testing"})
	@comment({"Indicates the name (identifier) associated with this online account."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/accountName")
	void setFoafAccountNames(Set<?> foafAccountNames);

	/** Indicates a homepage of the service provide for this online account. */
	@label({"account service homepage"})
	@term_status({"testing"})
	@comment({"Indicates a homepage of the service provide for this online account."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/accountServiceHomepage")
	Set<Document> getFoafAccountServiceHomepages();
	/** Indicates a homepage of the service provide for this online account. */
	@label({"account service homepage"})
	@term_status({"testing"})
	@comment({"Indicates a homepage of the service provide for this online account."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/accountServiceHomepage")
	void setFoafAccountServiceHomepages(Set<? extends Document> foafAccountServiceHomepages);

}
