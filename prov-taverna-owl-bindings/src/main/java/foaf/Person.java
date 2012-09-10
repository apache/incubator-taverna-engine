package foaf;

import geo.SpatialThing;
import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.Thing;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import vs.term_status;

/** A person. */
@label({"Person"})
@term_status({"stable"})
@subClassOf({"http://www.w3.org/2000/10/swap/pim/contact#Person", "http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing", "http://xmlns.com/foaf/0.1/Agent"})
@comment({"A person."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/Person")
public interface Person extends con.Person, SpatialThing, Agent {
	/** A current project this person works on. */
	@label({"current project"})
	@term_status({"testing"})
	@comment({"A current project this person works on."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/currentProject")
	Set<Thing> getFoafCurrentProjects();
	/** A current project this person works on. */
	@label({"current project"})
	@term_status({"testing"})
	@comment({"A current project this person works on."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/currentProject")
	void setFoafCurrentProjects(Set<? extends Thing> foafCurrentProjects);

	/** The family name of some person. */
	@label({"familyName"})
	@term_status({"testing"})
	@comment({"The family name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/familyName")
	Set<Object> getFoafFamilyNames();
	/** The family name of some person. */
	@label({"familyName"})
	@term_status({"testing"})
	@comment({"The family name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/familyName")
	void setFoafFamilyNames(Set<?> foafFamilyNames);

	/** The family name of some person. */
	@label({"family_name"})
	@term_status({"archaic"})
	@comment({"The family name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/family_name")
	Set<Object> getFoafFamily_names();
	/** The family name of some person. */
	@label({"family_name"})
	@term_status({"archaic"})
	@comment({"The family name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/family_name")
	void setFoafFamily_names(Set<?> foafFamily_names);

	/** The first name of a person. */
	@label({"firstName"})
	@term_status({"testing"})
	@comment({"The first name of a person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/firstName")
	Set<Object> getFoafFirstNames();
	/** The first name of a person. */
	@label({"firstName"})
	@term_status({"testing"})
	@comment({"The first name of a person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/firstName")
	void setFoafFirstNames(Set<?> foafFirstNames);

	/** A textual geekcode for this person, see http://www.geekcode.com/geek.html */
	@label({"geekcode"})
	@term_status({"archaic"})
	@comment({"A textual geekcode for this person, see http://www.geekcode.com/geek.html"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/geekcode")
	Set<Object> getFoafGeekcodes();
	/** A textual geekcode for this person, see http://www.geekcode.com/geek.html */
	@label({"geekcode"})
	@term_status({"archaic"})
	@comment({"A textual geekcode for this person, see http://www.geekcode.com/geek.html"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/geekcode")
	void setFoafGeekcodes(Set<?> foafGeekcodes);

	/** An image that can be used to represent some thing (ie. those depictions which are particularly representative of something, eg. one's photo on a homepage). */
	@label({"image"})
	@term_status({"testing"})
	@comment({"An image that can be used to represent some thing (ie. those depictions which are particularly representative of something, eg. one's photo on a homepage)."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/depiction"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/img")
	Set<Image> getFoafImgs();
	/** An image that can be used to represent some thing (ie. those depictions which are particularly representative of something, eg. one's photo on a homepage). */
	@label({"image"})
	@term_status({"testing"})
	@comment({"An image that can be used to represent some thing (ie. those depictions which are particularly representative of something, eg. one's photo on a homepage)."})
	@subPropertyOf({"http://xmlns.com/foaf/0.1/depiction"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/img")
	void setFoafImgs(Set<? extends Image> foafImgs);

	/** A person known by this person (indicating some level of reciprocated interaction between the parties). */
	@label({"knows"})
	@term_status({"stable"})
	@comment({"A person known by this person (indicating some level of reciprocated interaction between the parties)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/knows")
	Set<Person> getFoafKnows();
	/** A person known by this person (indicating some level of reciprocated interaction between the parties). */
	@label({"knows"})
	@term_status({"stable"})
	@comment({"A person known by this person (indicating some level of reciprocated interaction between the parties)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/knows")
	void setFoafKnows(Set<? extends Person> foafKnows);

	/** The last name of a person. */
	@label({"lastName"})
	@term_status({"testing"})
	@comment({"The last name of a person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/lastName")
	Set<Object> getFoafLastNames();
	/** The last name of a person. */
	@label({"lastName"})
	@term_status({"testing"})
	@comment({"The last name of a person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/lastName")
	void setFoafLastNames(Set<?> foafLastNames);

	/** A Myers Briggs (MBTI) personality classification. */
	@label({"myersBriggs"})
	@term_status({"testing"})
	@comment({"A Myers Briggs (MBTI) personality classification."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/myersBriggs")
	Set<Object> getFoafMyersBriggs();
	/** A Myers Briggs (MBTI) personality classification. */
	@label({"myersBriggs"})
	@term_status({"testing"})
	@comment({"A Myers Briggs (MBTI) personality classification."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/myersBriggs")
	void setFoafMyersBriggs(Set<?> foafMyersBriggs);

	/** A project this person has previously worked on. */
	@label({"past project"})
	@term_status({"testing"})
	@comment({"A project this person has previously worked on."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/pastProject")
	Set<Thing> getFoafPastProjects();
	/** A project this person has previously worked on. */
	@label({"past project"})
	@term_status({"testing"})
	@comment({"A project this person has previously worked on."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/pastProject")
	void setFoafPastProjects(Set<? extends Thing> foafPastProjects);

	/** A .plan comment, in the tradition of finger and '.plan' files. */
	@label({"plan"})
	@term_status({"testing"})
	@comment({"A .plan comment, in the tradition of finger and '.plan' files."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/plan")
	Set<Object> getFoafPlans();
	/** A .plan comment, in the tradition of finger and '.plan' files. */
	@label({"plan"})
	@term_status({"testing"})
	@comment({"A .plan comment, in the tradition of finger and '.plan' files."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/plan")
	void setFoafPlans(Set<?> foafPlans);

	/** A link to the publications of this person. */
	@label({"publications"})
	@term_status({"testing"})
	@comment({"A link to the publications of this person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/publications")
	Set<Document> getFoafPublications();
	/** A link to the publications of this person. */
	@label({"publications"})
	@term_status({"testing"})
	@comment({"A link to the publications of this person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/publications")
	void setFoafPublications(Set<? extends Document> foafPublications);

	/** A homepage of a school attended by the person. */
	@label({"schoolHomepage"})
	@term_status({"testing"})
	@comment({"A homepage of a school attended by the person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/schoolHomepage")
	Set<Document> getFoafSchoolHomepages();
	/** A homepage of a school attended by the person. */
	@label({"schoolHomepage"})
	@term_status({"testing"})
	@comment({"A homepage of a school attended by the person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/schoolHomepage")
	void setFoafSchoolHomepages(Set<? extends Document> foafSchoolHomepages);

	/** The surname of some person. */
	@label({"Surname"})
	@term_status({"archaic"})
	@comment({"The surname of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/surname")
	Set<Object> getFoafSurnames();
	/** The surname of some person. */
	@label({"Surname"})
	@term_status({"archaic"})
	@comment({"The surname of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/surname")
	void setFoafSurnames(Set<?> foafSurnames);

	/** A work info homepage of some person; a page about their work for some organization. */
	@label({"work info homepage"})
	@term_status({"testing"})
	@comment({"A work info homepage of some person; a page about their work for some organization."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/workInfoHomepage")
	Set<Document> getFoafWorkInfoHomepages();
	/** A work info homepage of some person; a page about their work for some organization. */
	@label({"work info homepage"})
	@term_status({"testing"})
	@comment({"A work info homepage of some person; a page about their work for some organization."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/workInfoHomepage")
	void setFoafWorkInfoHomepages(Set<? extends Document> foafWorkInfoHomepages);

	/** A workplace homepage of some person; the homepage of an organization they work for. */
	@label({"workplace homepage"})
	@term_status({"testing"})
	@comment({"A workplace homepage of some person; the homepage of an organization they work for."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/workplaceHomepage")
	Set<Document> getFoafWorkplaceHomepages();
	/** A workplace homepage of some person; the homepage of an organization they work for. */
	@label({"workplace homepage"})
	@term_status({"testing"})
	@comment({"A workplace homepage of some person; the homepage of an organization they work for."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/workplaceHomepage")
	void setFoafWorkplaceHomepages(Set<? extends Document> foafWorkplaceHomepages);

}
