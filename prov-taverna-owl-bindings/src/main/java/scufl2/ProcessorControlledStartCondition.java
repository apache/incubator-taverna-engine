package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"ProcessorControlledStartCondition"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#StartCondition"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#ProcessorControlledStartCondition")
public interface ProcessorControlledStartCondition extends StartCondition {
	@label({"controllingProcessor"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#controllingProcessor")
	Processor getScufl2ControllingProcessor();
	@label({"controllingProcessor"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#controllingProcessor")
	void setScufl2ControllingProcessor(Processor scufl2ControllingProcessor);

}
