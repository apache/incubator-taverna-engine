package prov;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;

/** An instance of prov:Source provides additional descriptions about the binary prov:hadPrimarySource relation from some secondary prov:Entity to an earlier, primary prov:Entity. For example, :blog prov:hadPrimarySource :newsArticle; prov:qualified [ a prov:Source; prov:entity :newsArticle; :foo :bar ] . */
@category({"qualified"})
@label({"Source"})
@dm({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-dm.html#term-primary-source"})
@component({"derivations"})
@n({"http://dvcs.w3.org/hg/prov/raw-file/default/model/prov-n.html#expression-original-source"})
@subClassOf({"http://www.w3.org/ns/prov#EntityInfluence"})
@comment({"An instance of prov:Source provides additional descriptions about the binary prov:hadPrimarySource relation from some secondary prov:Entity to an earlier, primary prov:Entity. For example, :blog prov:hadPrimarySource :newsArticle; prov:qualified [ a prov:Source; prov:entity :newsArticle; :foo :bar ] ."})
@definition({"A primary source for a topic refers to something produced by some agent with direct experience and knowledge about the topic, at the time of the topic's study, without benefit from hindsight.\n" + 
	"\n" + 
	"Because of the directness of primary sources, they 'speak for themselves' in ways that cannot be captured through the filter of secondary sources. As such, it is important for secondary sources to reference those primary sources from which they were derived, so that their reliability can be investigated.\n" + 
	"\n" + 
	"A primary source relation is a particular case of derivation of secondary materials from their primary sources. It is recognized that the determination of primary sources can be up to interpretation, and should be done according to conventions accepted within the application's domain."})
@isDefinedBy({"http://www.w3.org/ns/prov#"})
@unqualifiedForm({"http://www.w3.org/ns/prov#hadPrimarySource"})
@Iri("http://www.w3.org/ns/prov#Source")
public interface Source extends EntityInfluence {
}
