package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include forms on Web pages, applets, multimedia learning objects, chat services, or virtual reality environments. */
@label({"Interactive Resource"})
@definition({"A resource requiring interaction from the user to be understood, executed, or experienced."})
@comment({"Examples include forms on Web pages, applets, multimedia learning objects, chat services, or virtual reality environments."})
@Iri("http://purl.org/dc/dcmitype/InteractiveResource")
public interface InteractiveResource {
}
