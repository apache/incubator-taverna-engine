package dct;

import org.openrdf.annotations.Iri;
import owl.equivalentClass;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import skos.definition;

/** 
 * A resource that acts or has the power to act.
 * Examples of Agent include person, organization, and software agent.
 */
@label({"Agent", "Agent"})
@equivalentClass({"http://xmlns.com/foaf/0.1/Agent"})
@description({"Examples of Agent include person, organization, and software agent."})
@definition({"A resource that acts or has the power to act."})
@comment({"A resource that acts or has the power to act.", "Examples of Agent include person, organization, and software agent."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/Agent")
public interface Agent {
}
