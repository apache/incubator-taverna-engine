package wfdesc;

import java.util.Set;
import org.openrdf.annotations.Iri;
import prov.Entity;
import rdfs.comment;
import rdfs.subClassOf;
import tavernaprov.Content;

/** Artifact is a general concept that represents immutable piece of state, which may have a physical embodiment in a physical object, or a digital representation in a computer system. In the case of wfdesc, artifact is used to provide information about parameters, e.g., data type, structure, etc. */
@subClassOf({"http://www.w3.org/ns/prov#Entity"})
@comment({"Artifact is a general concept that represents immutable piece of state, which may have a physical embodiment in a physical object, or a digital representation in a computer system. In the case of wfdesc, artifact is used to provide information about parameters, e.g., data type, structure, etc."})
@Iri("http://purl.org/wf4ever/wfdesc#Artifact")
public interface Artifact extends Entity {
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/content")
	Set<Content> getTavernaprovContents();
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/content")
	void setTavernaprovContents(Set<? extends Content> tavernaprovContents);

}
