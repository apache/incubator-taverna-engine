package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@label({"IterationStrategyNode"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#IterationStrategyNode"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#IterationStrategyNodeWithChildren")
public interface IterationStrategyNodeWithChildren extends IterationStrategyNode {
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#iterationStrategyChildren")
	IterationStrategyChildren getScufl2IterationStrategyChildren();
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#iterationStrategyChildren")
	void setScufl2IterationStrategyChildren(IterationStrategyChildren scufl2IterationStrategyChildren);

}
