package scufl2;

import java.util.Set;
import org.openrdf.annotations.Iri;
import rdfs.label;
import rdfs.subClassOf;
import rdfs.subPropertyOf;

@label({"Workflow bundle"})
@subClassOf({"http://ns.taverna.org.uk/2010/scufl2#Named"})
@Iri("http://ns.taverna.org.uk/2010/scufl2#WorkflowBundle")
public interface WorkflowBundle extends Named {
	@label({"activities"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activities")
	Set<Activity> getScufl2Activities();
	@label({"activities"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#activities")
	void setScufl2Activities(Set<? extends Activity> scufl2Activities);

	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#basedOnWorkflowBundle")
	Set<WorkflowBundle> getScufl2BasedOnWorkflowBundles();
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#basedOnWorkflowBundle")
	void setScufl2BasedOnWorkflowBundles(Set<? extends WorkflowBundle> scufl2BasedOnWorkflowBundles);

	@label({"main profile"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#profile"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#mainProfile")
	Profile getScufl2MainProfile();
	@label({"main profile"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#profile"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#mainProfile")
	void setScufl2MainProfile(Profile scufl2MainProfile);

	@label({"main workflow"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflow"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#mainWorkflow")
	Workflow getScufl2MainWorkflow();
	@label({"main workflow"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#workflow"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#mainWorkflow")
	void setScufl2MainWorkflow(Workflow scufl2MainWorkflow);

	@label({"profile"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#profile")
	Set<Profile> getScufl2Profiles();
	@label({"profile"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#profile")
	void setScufl2Profiles(Set<? extends Profile> scufl2Profiles);

	@label({"workflow"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflow")
	Set<Workflow> getScufl2Workflows();
	@label({"workflow"})
	@subPropertyOf({"http://ns.taverna.org.uk/2010/scufl2#child"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#workflow")
	void setScufl2Workflows(Set<? extends Workflow> scufl2Workflows);

}
