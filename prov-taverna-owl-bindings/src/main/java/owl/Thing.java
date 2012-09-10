package owl;

import foaf.Agent;
import foaf.Document;
import foaf.Image;
import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subPropertyOf;
import vs.term_status;

@label({"Thing"})
@Iri("http://www.w3.org/2002/07/owl#Thing")
public interface Thing {
	/** A depiction of some thing. */
	@label({"depiction"})
	@term_status({"testing"})
	@comment({"A depiction of some thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/depiction")
	Set<Image> getFoafDepictions();
	/** A depiction of some thing. */
	@label({"depiction"})
	@term_status({"testing"})
	@comment({"A depiction of some thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/depiction")
	void setFoafDepictions(Set<? extends Image> foafDepictions);

	/** An organization funding a project or person. */
	@label({"funded by"})
	@term_status({"archaic"})
	@comment({"An organization funding a project or person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/fundedBy")
	Set<Thing> getFoafFundedBy();
	/** An organization funding a project or person. */
	@label({"funded by"})
	@term_status({"archaic"})
	@comment({"An organization funding a project or person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/fundedBy")
	void setFoafFundedBy(Set<? extends Thing> foafFundedBy);

	/** A homepage for some thing. */
	@label({"homepage"})
	@term_status({"stable"})
	@comment({"A homepage for some thing."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/isPrimaryTopicOf", "http://xmlns.com/foaf/0.1/page"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/homepage")
	Set<Document> getFoafHomepages();
	/** A homepage for some thing. */
	@label({"homepage"})
	@term_status({"stable"})
	@comment({"A homepage for some thing."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/isPrimaryTopicOf", "http://xmlns.com/foaf/0.1/page"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/homepage")
	void setFoafHomepages(Set<? extends Document> foafHomepages);

	/** A document that this thing is the primary topic of. */
	@label({"is primary topic of"})
	@term_status({"stable"})
	@comment({"A document that this thing is the primary topic of."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/page"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/isPrimaryTopicOf")
	Set<Document> getFoafIsPrimaryTopicOf();
	/** A document that this thing is the primary topic of. */
	@label({"is primary topic of"})
	@term_status({"stable"})
	@comment({"A document that this thing is the primary topic of."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/page"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/isPrimaryTopicOf")
	void setFoafIsPrimaryTopicOf(Set<? extends Document> foafIsPrimaryTopicOf);

	/** A logo representing some thing. */
	@label({"logo"})
	@term_status({"testing"})
	@comment({"A logo representing some thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/logo")
	Set<Thing> getFoafLogos();
	/** A logo representing some thing. */
	@label({"logo"})
	@term_status({"testing"})
	@comment({"A logo representing some thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/logo")
	void setFoafLogos(Set<? extends Thing> foafLogos);

	/** An agent that  made this thing. */
	@label({"maker"})
	@term_status({"stable"})
	@comment({"An agent that  made this thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/maker")
	Set<Agent> getFoafMakers();
	/** An agent that  made this thing. */
	@label({"maker"})
	@term_status({"stable"})
	@comment({"An agent that  made this thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/maker")
	void setFoafMakers(Set<? extends Agent> foafMakers);

	/** A name for some thing. */
	@label({"name"})
	@term_status({"testing"})
	@comment({"A name for some thing."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#label"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/name")
	Set<Object> getFoafNames();
	/** A name for some thing. */
	@label({"name"})
	@term_status({"testing"})
	@comment({"A name for some thing."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#label"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/name")
	void setFoafNames(Set<?> foafNames);

	/** A page or document about this thing. */
	@label({"page"})
	@term_status({"testing"})
	@comment({"A page or document about this thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/page")
	Set<Document> getFoafPages();
	/** A page or document about this thing. */
	@label({"page"})
	@term_status({"testing"})
	@comment({"A page or document about this thing."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/page")
	void setFoafPages(Set<? extends Document> foafPages);

	/** A theme. */
	@label({"theme"})
	@term_status({"archaic"})
	@comment({"A theme."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/theme")
	Set<Thing> getFoafThemes();
	/** A theme. */
	@label({"theme"})
	@term_status({"archaic"})
	@comment({"A theme."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/theme")
	void setFoafThemes(Set<? extends Thing> foafThemes);

}
