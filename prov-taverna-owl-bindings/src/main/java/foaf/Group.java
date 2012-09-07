package foaf;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** A class of Agents. */
@label({"Group"})
@term_status({"stable"})
@subClassOf({"http://xmlns.com/foaf/0.1/Agent"})
@comment({"A class of Agents."})
@Iri("http://xmlns.com/foaf/0.1/Group")
public interface Group extends Agent {
	/** Indicates a member of a Group */
	@label({"member"})
	@term_status({"stable"})
	@comment({"Indicates a member of a Group"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/member")
	Set<Agent> getFoafMembers();
	/** Indicates a member of a Group */
	@label({"member"})
	@term_status({"stable"})
	@comment({"Indicates a member of a Group"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/member")
	void setFoafMembers(Set<? extends Agent> foafMembers);

}
