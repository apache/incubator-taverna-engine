package scufl2;

import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@label({"DataLink"})
@subClassOf({"http://purl.org/wf4ever/wfdesc#DataLink"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#DataLink")
public interface DataLink extends wfdesc.DataLink {
	@label({"receives from"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSource"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#receivesFrom")
	SenderPort getScufl2ReceivesFrom();
	@label({"receives from"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSource"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#receivesFrom")
	void setScufl2ReceivesFrom(SenderPort scufl2ReceivesFrom);

	@label({"sends to"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSink"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#sendsTo")
	ReceiverPort getScufl2SendsTo();
	@label({"sends to"})
	@subPropertyOf({"http://purl.org/wf4ever/wfdesc#hasSink"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#sendsTo")
	void setScufl2SendsTo(ReceiverPort scufl2SendsTo);

}
