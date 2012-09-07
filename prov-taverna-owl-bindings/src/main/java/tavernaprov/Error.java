package tavernaprov;

import dct.description;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.String;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;
import skos.definition;
import wfdesc.Artifact;

@subClassOf({"http://purl.org/wf4ever/wfdesc#Artifact"})
@Iri("http://ns.taverna.org.uk/2012/tavernaprov/Error")
public interface Error extends Artifact {
	@subPropertyOf({"http://purl.org/dc/terms/description"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/errorMessage")
	Set<CharSequence> getTavernaprovErrorMessage();
	@subPropertyOf({"http://purl.org/dc/terms/description"})
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/errorMessage")
	void setTavernaprovErrorMessage(Set<? extends CharSequence> tavernaprovErrorMessage);

	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/stackTrace")
	Set<String> getTavernaprovStackTrace();
	@Iri("http://ns.taverna.org.uk/2012/tavernaprov/stackTrace")
	void setTavernaprovStackTrace(Set<? extends String> tavernaprovStackTrace);

	/** 
	 * An account of the resource.
	 * Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource.
	 */
	@label({"Description", "Description"})
	@description({"Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@definition({"An account of the resource."})
	@comment({"An account of the resource.", "Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/description")
	Set<Object> getDctermsDescriptions();
	/** 
	 * An account of the resource.
	 * Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource.
	 */
	@label({"Description", "Description"})
	@description({"Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@definition({"An account of the resource."})
	@comment({"An account of the resource.", "Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/description")
	void setDctermsDescriptions(Set<?> dctermsDescriptions);

}
