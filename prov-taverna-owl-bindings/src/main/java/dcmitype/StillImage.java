package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** Examples include paintings, drawings, graphic designs, plans and maps. Recommended best practice is to assign the type Text to images of textual materials. Instances of the type Still Image must also be describable as instances of the broader type Image. */
@label({"Still Image"})
@definition({"A static visual representation."})
@subClassOf({"http://purl.org/dc/dcmitype/Image"})
@comment({"Examples include paintings, drawings, graphic designs, plans and maps. Recommended best practice is to assign the type Text to images of textual materials. Instances of the type Still Image must also be describable as instances of the broader type Image."})
@Iri("http://purl.org/dc/dcmitype/StillImage")
public interface StillImage extends Image {
}
