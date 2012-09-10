package dcmitype;

import org.openrdf.annotations.Iri;
import rdfs.comment;
import rdfs.label;
import skos.definition;

/** Metadata for an event provides descriptive information that is the basis for discovery of the purpose, location, duration, and responsible agents associated with an event. Examples include an exhibition, webcast, conference, workshop, open day, performance, battle, trial, wedding, tea party, conflagration. */
@label({"Event"})
@definition({"A non-persistent, time-based occurrence."})
@comment({"Metadata for an event provides descriptive information that is the basis for discovery of the purpose, location, duration, and responsible agents associated with an event. Examples include an exhibition, webcast, conference, workshop, open day, performance, battle, trial, wedding, tea party, conflagration."})
@Iri("http://purl.org/dc/dcmitype/Event")
public interface Event {
}
