package scufl2;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@label({"Activity"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Configurable"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#Activity")
public interface Activity extends Configurable {
	@label({"Activity port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityPort")
	Set<ActivityPort> getScufl2ActivityPorts();
	@label({"Activity port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#port"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityPort")
	void setScufl2ActivityPorts(Set<? extends ActivityPort> scufl2ActivityPorts);

	@label({"Activity type"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityType")
	ActivityType getScufl2ActivityType();
	@label({"Activity type"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activityType")
	void setScufl2ActivityType(ActivityType scufl2ActivityType);

	@label({"Activity input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#activityPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputActivityPort")
	Set<InputActivityPort> getScufl2InputActivityPorts();
	@label({"Activity input port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#activityPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#inputActivityPort")
	void setScufl2InputActivityPorts(Set<? extends InputActivityPort> scufl2InputActivityPorts);

	@label({"Activity output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#activityPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputActivityPort")
	Set<OutputActivityPort> getScufl2OutputActivityPorts();
	@label({"Activity output port"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#activityPort"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#outputActivityPort")
	void setScufl2OutputActivityPorts(Set<? extends OutputActivityPort> scufl2OutputActivityPorts);

}
