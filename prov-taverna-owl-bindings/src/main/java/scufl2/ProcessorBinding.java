package scufl2;

import java.math.BigInteger;
import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;

@label({"ProcessorBinding"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#WorkflowElement"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#ProcessorBinding")
public interface ProcessorBinding extends WorkflowElement {
	@label({"activityPosition"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityPosition")
	BigInteger getScufl2ActivityPosition();
	@label({"activityPosition"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityPosition")
	void setScufl2ActivityPosition(BigInteger scufl2ActivityPosition);

	@label({"boundActivity"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundActivity")
	Activity getScufl2BoundActivity();
	@label({"boundActivity"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundActivity")
	void setScufl2BoundActivity(Activity scufl2BoundActivity);

	@label({"bound processor"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundProcessor")
	Processor getScufl2BoundProcessor();
	@label({"bound processor"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#boundProcessor")
	void setScufl2BoundProcessor(Processor scufl2BoundProcessor);

	@label({"inputPortBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputPortBinding")
	Set<InputPortBinding> getScufl2InputPortBinding();
	@label({"inputPortBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputPortBinding")
	void setScufl2InputPortBinding(Set<? extends InputPortBinding> scufl2InputPortBinding);

	@label({"outputPortBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputPortBinding")
	Set<OutputPortBinding> getScufl2OutputPortBinding();
	@label({"outputPortBindings"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputPortBinding")
	void setScufl2OutputPortBinding(Set<? extends OutputPortBinding> scufl2OutputPortBinding);

}
