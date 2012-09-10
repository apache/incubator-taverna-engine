package rdfs;

import org.openrdf.annotations.Iri;
import rdf.Property;

/** 
 * The class of container membership properties, rdf:_1, rdf:_2, ...,
 *                     all of which are sub-properties of 'member'.
 */
@label({"ContainerMembershipProperty"})
@subClassOf({"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property"})
@comment({"The class of container membership properties, rdf:_1, rdf:_2, ...,\n" + 
	"                    all of which are sub-properties of 'member'."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty")
public interface ContainerMembershipProperty extends Property {
}
