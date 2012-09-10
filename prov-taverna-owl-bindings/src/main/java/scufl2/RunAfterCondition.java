package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"RunAfterCondition"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#ProcessorControlledStartCondition"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#RunAfterCondition")
public interface RunAfterCondition extends ProcessorControlledStartCondition {
}
