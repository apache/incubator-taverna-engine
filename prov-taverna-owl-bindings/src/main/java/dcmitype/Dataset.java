package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include lists, tables, and databases. A dataset may be useful for direct machine processing. */
@label({"Dataset"})
@definition({"Data encoded in a defined structure."})
@comment({"Examples include lists, tables, and databases. A dataset may be useful for direct machine processing."})
@Iri("http://purl.org/dc/dcmitype/Dataset")
public interface Dataset {
}
