package geo;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import vs.term_status;

@label({"Spatial Thing"})
@Iri("http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing")
public interface SpatialThing {
	/** A location that something is based near, for some broadly human notion of near. */
	@label({"based near"})
	@term_status({"testing"})
	@comment({"A location that something is based near, for some broadly human notion of near."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/based_near")
	Set<SpatialThing> getFoafBased_near();
	/** A location that something is based near, for some broadly human notion of near. */
	@label({"based near"})
	@term_status({"testing"})
	@comment({"A location that something is based near, for some broadly human notion of near."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/based_near")
	void setFoafBased_near(Set<? extends SpatialThing> foafBased_near);

}
