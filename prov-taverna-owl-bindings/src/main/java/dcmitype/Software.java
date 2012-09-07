package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Examples include a C source file, MS-Windows .exe executable, or Perl script. */
@label({"Software"})
@definition({"A computer program in source or compiled form."})
@comment({"Examples include a C source file, MS-Windows .exe executable, or Perl script."})
@Iri("http://purl.org/dc/dcmitype/Software")
public interface Software {
}
