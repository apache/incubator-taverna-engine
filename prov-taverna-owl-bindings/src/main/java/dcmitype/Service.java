package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include a photocopying service, a banking service, an authentication service, interlibrary loans, a Z39.50 or Web server. */
@label({"Service"})
@definition({"A system that provides one or more functions."})
@comment({"Examples include a photocopying service, a banking service, an authentication service, interlibrary loans, a Z39.50 or Web server."})
@Iri("http://purl.org/dc/dcmitype/Service")
public interface Service {
}
