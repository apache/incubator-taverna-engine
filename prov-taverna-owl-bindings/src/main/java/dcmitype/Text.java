package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include books, letters, dissertations, poems, newspapers, articles, archives of mailing lists. Note that facsimiles or images of texts are still of the genre Text. */
@label({"Text"})
@definition({"A resource consisting primarily of words for reading."})
@comment({"Examples include books, letters, dissertations, poems, newspapers, articles, archives of mailing lists. Note that facsimiles or images of texts are still of the genre Text."})
@Iri("http://purl.org/dc/dcmitype/Text")
public interface Text {
}
