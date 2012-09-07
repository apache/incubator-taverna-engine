package dct;

import org.openrdf.annotations.Iri;
import rdfs.Class;
import rdfs.comment;
import rdfs.isDefinedBy;
import rdfs.label;
import rdfs.subClassOf;
import skos.definition;

/** 
 * A group of agents.
 * Examples of Agent Class include groups seen as classes, such as students, women, charities, lecturers.
 */
@label({"Agent Class", "Agent Class"})
@description({"Examples of Agent Class include groups seen as classes, such as students, women, charities, lecturers."})
@definition({"A group of agents."})
@subClassOf({"http://www.w3.org/2000/01/rdf-schema#Class"})
@comment({"A group of agents.", "Examples of Agent Class include groups seen as classes, such as students, women, charities, lecturers."})
@isDefinedBy({"http://purl.org/dc/terms/"})
@Iri("http://purl.org/dc/terms/AgentClass")
public interface AgentClass extends Class {
}
