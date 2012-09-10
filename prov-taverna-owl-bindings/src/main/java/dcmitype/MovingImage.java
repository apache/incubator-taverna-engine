package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** Examples include animations, movies, television programs, videos, zoetropes, or visual output from a simulation. Instances of the type Moving Image must also be describable as instances of the broader type Image. */
@label({"Moving Image"})
@definition({"A series of visual representations imparting an impression of motion when shown in succession."})
@subClassOf({"http://purl.org/dc/dcmitype/Image"})
@comment({"Examples include animations, movies, television programs, videos, zoetropes, or visual output from a simulation. Instances of the type Moving Image must also be describable as instances of the broader type Image."})
@Iri("http://purl.org/dc/dcmitype/MovingImage")
public interface MovingImage extends Image {
}
