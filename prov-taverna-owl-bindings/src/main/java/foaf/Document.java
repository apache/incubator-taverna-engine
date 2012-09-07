package foaf;

import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.Thing;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import vs.term_status;

/** A document. */
@label({"Document"})
@term_status({"testing"})
@comment({"A document."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/Document")
public interface Document {
	/** The primary topic of some page or document. */
	@label({"primary topic"})
	@term_status({"stable"})
	@comment({"The primary topic of some page or document."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/primaryTopic")
	Thing getFoafPrimaryTopic();
	/** The primary topic of some page or document. */
	@label({"primary topic"})
	@term_status({"stable"})
	@comment({"The primary topic of some page or document."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/primaryTopic")
	void setFoafPrimaryTopic(Thing foafPrimaryTopic);

	/** A sha1sum hash, in hex. */
	@label({"sha1sum (hex)"})
	@term_status({"unstable"})
	@comment({"A sha1sum hash, in hex."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/sha1")
	Set<Object> getFoafSha1s();
	/** A sha1sum hash, in hex. */
	@label({"sha1sum (hex)"})
	@term_status({"unstable"})
	@comment({"A sha1sum hash, in hex."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/sha1")
	void setFoafSha1s(Set<?> foafSha1s);

	/** A topic of some page or document. */
	@label({"topic"})
	@term_status({"testing"})
	@comment({"A topic of some page or document."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/topic")
	Set<Thing> getFoafTopics();
	/** A topic of some page or document. */
	@label({"topic"})
	@term_status({"testing"})
	@comment({"A topic of some page or document."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/topic")
	void setFoafTopics(Set<? extends Thing> foafTopics);

}
