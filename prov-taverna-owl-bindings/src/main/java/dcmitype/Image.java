package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include images and photographs of physical objects, paintings, prints, drawings, other images and graphics, animations and moving pictures, film, diagrams, maps, musical notation. Note that Image may include both electronic and physical representations. */
@label({"Image"})
@definition({"A visual representation other than text."})
@comment({"Examples include images and photographs of physical objects, paintings, prints, drawings, other images and graphics, animations and moving pictures, film, diagrams, maps, musical notation. Note that Image may include both electronic and physical representations."})
@Iri("http://purl.org/dc/dcmitype/Image")
public interface Image {
}
