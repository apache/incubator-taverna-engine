package foaf;

import java.util.Set;
import org.openrdf.annotations.Iri;
import owl.Thing;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import vs.term_status;

/** An image. */
@label({"Image"})
@term_status({"testing"})
@subClassOf({"http://xmlns.com/foaf/0.1/Document"})
@comment({"An image."})
@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
@Iri("http://xmlns.com/foaf/0.1/Image")
public interface Image extends Document {
	/** A thing depicted in this representation. */
	@label({"depicts"})
	@term_status({"testing"})
	@comment({"A thing depicted in this representation."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/depicts")
	Set<Thing> getFoafDepicts();
	/** A thing depicted in this representation. */
	@label({"depicts"})
	@term_status({"testing"})
	@comment({"A thing depicted in this representation."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/depicts")
	void setFoafDepicts(Set<? extends Thing> foafDepicts);

	/** A derived thumbnail image. */
	@label({"thumbnail"})
	@term_status({"testing"})
	@comment({"A derived thumbnail image."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/thumbnail")
	Set<Image> getFoafThumbnails();
	/** A derived thumbnail image. */
	@label({"thumbnail"})
	@term_status({"testing"})
	@comment({"A derived thumbnail image."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/thumbnail")
	void setFoafThumbnails(Set<? extends Image> foafThumbnails);

}
