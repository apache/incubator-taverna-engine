package skos;

import java.lang.Object;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

@label({"Ordered Collection"})
@definition({"An ordered collection of concepts, where both the grouping and the ordering are meaningful."})
@subClassOf({"http://www.w3.org/2004/02/skos/core#Collection"})
@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
@scopeNote({"Ordered collections can be used where you would like a set of concepts to be displayed in a specific order, and optionally under a 'node label'."})
@Iri("http://www.w3.org/2004/02/skos/core#OrderedCollection")
public interface OrderedCollection extends Collection {
	/** 
	 * For any resource, every item in the list given as the value of the
	 *       skos:memberList property is also a value of the skos:member property.
	 */
	@label({"has member list"})
	@definition({"Relates an ordered collection to the RDF list containing its members."})
	@comment({"For any resource, every item in the list given as the value of the\n" + 
		"      skos:memberList property is also a value of the skos:member property."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#memberList")
	Object getSkosMemberList();
	/** 
	 * For any resource, every item in the list given as the value of the
	 *       skos:memberList property is also a value of the skos:member property.
	 */
	@label({"has member list"})
	@definition({"Relates an ordered collection to the RDF list containing its members."})
	@comment({"For any resource, every item in the list given as the value of the\n" + 
		"      skos:memberList property is also a value of the skos:member property."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#memberList")
	void setSkosMemberList(Object skosMemberList);

}
