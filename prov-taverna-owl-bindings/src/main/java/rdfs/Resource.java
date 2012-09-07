package rdfs;

import dcam.VocabularyEncodingScheme;
import dct.Agent;
import dct.AgentClass;
import dct.LicenseDocument;
import dct.LinguisticSystem;
import dct.Location;
import dct.LocationPeriodOrJurisdiction;
import dct.MediaTypeOrExtent;
import dct.MethodOfInstruction;
import dct.PeriodOfTime;
import dct.ProvenanceStatement;
import dct.RightsStatement;
import dct.SizeOrDuration;
import dct.Standard;
import dct.description;
import java.lang.Object;
import java.util.Set;
import org.openrdf.annotations.Iri;
import prov.category;
import prov.component;
import prov.inverse;
import prov.sharesDefinitionWith;
import skos.ConceptScheme;
import skos.definition;
import skos.example;
import skos.note;
import skos.scopeNote;
import vs.term_status;

/** The class resource, everything. */
@label({"Resource"})
@comment({"The class resource, everything."})
@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
@Iri("http://www.w3.org/2000/01/rdf-schema#Resource")
public interface Resource {
	/** 
	 * The resources are the same (like in owl:sameAs), and
	 *       so are the sub-resources having the same base URI as a parent.  For instance if 
	 *         <file:///tmp/example/> scufl2:globalBaseURI 
	 *             <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/> .
	 *       then also 
	 *         <file:///tmp/example/workflow/HelloWorld/> scufl2:globalBaseURI 
	 *             <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/> 
	 * 
	 *       Note that for URIs under
	 *       <http://ns.taverna.org.uk/2010/workflowBundle/> special URI template rules
	 *       link the URI with the scufl2:parent, rdf:type, scufl2:name of the
	 *       resource. 
	 *       
	 *       For instance given 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting> 
	 *       it must be so that:
	 * 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>
	 *             a scufl2:OutputProcessorPort;
	 *             scufl2:name "greeting";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.
	 * 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>
	 *             a scufl2:Processor;
	 *             scufl2:name "Hello";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;
	 *             scufl2:outputProcessorPort <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>.
	 *         
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>    
	 *             a scufl2:Workflow;
	 *             scufl2:name "HelloWorld";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>;
	 *             sculf2:processor <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.
	 *         
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>    
	 *             a scufl2:WorkflowBundle;
	 *             scufl2:workflow <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;
	 *             scufl2:workflowBundleId "28f7c554-4f35-401f-b34b-516e9a0ef731";
	 * 
	 *         Where 28f7c554-4f35-401f-b34b-516e9a0ef731 must be a randomly
	 *         generated UUID.
	 */
	@comment({"The resources are the same (like in owl:sameAs), and\n" + 
		"      so are the sub-resources having the same base URI as a parent.  For instance if \n" + 
		"        <file:///tmp/example/> scufl2:globalBaseURI \n" + 
		"            <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/> .\n" + 
		"      then also \n" + 
		"        <file:///tmp/example/workflow/HelloWorld/> scufl2:globalBaseURI \n" + 
		"            <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/> \n" + 
		"\n" + 
		"      Note that for URIs under\n" + 
		"      <http://ns.taverna.org.uk/2010/workflowBundle/> special URI template rules\n" + 
		"      link the URI with the scufl2:parent, rdf:type, scufl2:name of the\n" + 
		"      resource. \n" + 
		"      \n" + 
		"      For instance given \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting> \n" + 
		"      it must be so that:\n" + 
		"\n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>\n" + 
		"            a scufl2:OutputProcessorPort;\n" + 
		"            scufl2:name \"greeting\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.\n" + 
		"\n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>\n" + 
		"            a scufl2:Processor;\n" + 
		"            scufl2:name \"Hello\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;\n" + 
		"            scufl2:outputProcessorPort <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>.\n" + 
		"        \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>    \n" + 
		"            a scufl2:Workflow;\n" + 
		"            scufl2:name \"HelloWorld\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>;\n" + 
		"            sculf2:processor <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.\n" + 
		"        \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>    \n" + 
		"            a scufl2:WorkflowBundle;\n" + 
		"            scufl2:workflow <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;\n" + 
		"            scufl2:workflowBundleId \"28f7c554-4f35-401f-b34b-516e9a0ef731\";\n" + 
		"\n" + 
		"        Where 28f7c554-4f35-401f-b34b-516e9a0ef731 must be a randomly\n" + 
		"        generated UUID."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#sameAs"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#globalBaseURI")
	Set<Object> getScufl2GlobalBaseURI();
	/** 
	 * The resources are the same (like in owl:sameAs), and
	 *       so are the sub-resources having the same base URI as a parent.  For instance if 
	 *         <file:///tmp/example/> scufl2:globalBaseURI 
	 *             <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/> .
	 *       then also 
	 *         <file:///tmp/example/workflow/HelloWorld/> scufl2:globalBaseURI 
	 *             <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/> 
	 * 
	 *       Note that for URIs under
	 *       <http://ns.taverna.org.uk/2010/workflowBundle/> special URI template rules
	 *       link the URI with the scufl2:parent, rdf:type, scufl2:name of the
	 *       resource. 
	 *       
	 *       For instance given 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting> 
	 *       it must be so that:
	 * 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>
	 *             a scufl2:OutputProcessorPort;
	 *             scufl2:name "greeting";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.
	 * 
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>
	 *             a scufl2:Processor;
	 *             scufl2:name "Hello";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;
	 *             scufl2:outputProcessorPort <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>.
	 *         
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>    
	 *             a scufl2:Workflow;
	 *             scufl2:name "HelloWorld";
	 *             scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>;
	 *             sculf2:processor <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.
	 *         
	 *         <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>    
	 *             a scufl2:WorkflowBundle;
	 *             scufl2:workflow <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;
	 *             scufl2:workflowBundleId "28f7c554-4f35-401f-b34b-516e9a0ef731";
	 * 
	 *         Where 28f7c554-4f35-401f-b34b-516e9a0ef731 must be a randomly
	 *         generated UUID.
	 */
	@comment({"The resources are the same (like in owl:sameAs), and\n" + 
		"      so are the sub-resources having the same base URI as a parent.  For instance if \n" + 
		"        <file:///tmp/example/> scufl2:globalBaseURI \n" + 
		"            <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/> .\n" + 
		"      then also \n" + 
		"        <file:///tmp/example/workflow/HelloWorld/> scufl2:globalBaseURI \n" + 
		"            <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/> \n" + 
		"\n" + 
		"      Note that for URIs under\n" + 
		"      <http://ns.taverna.org.uk/2010/workflowBundle/> special URI template rules\n" + 
		"      link the URI with the scufl2:parent, rdf:type, scufl2:name of the\n" + 
		"      resource. \n" + 
		"      \n" + 
		"      For instance given \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting> \n" + 
		"      it must be so that:\n" + 
		"\n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>\n" + 
		"            a scufl2:OutputProcessorPort;\n" + 
		"            scufl2:name \"greeting\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.\n" + 
		"\n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>\n" + 
		"            a scufl2:Processor;\n" + 
		"            scufl2:name \"Hello\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;\n" + 
		"            scufl2:outputProcessorPort <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello/out/greeting>.\n" + 
		"        \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>    \n" + 
		"            a scufl2:Workflow;\n" + 
		"            scufl2:name \"HelloWorld\";\n" + 
		"            scufl2:parent <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>;\n" + 
		"            sculf2:processor <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/processor/Hello>.\n" + 
		"        \n" + 
		"        <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/>    \n" + 
		"            a scufl2:WorkflowBundle;\n" + 
		"            scufl2:workflow <http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/workflow/HelloWorld/>;\n" + 
		"            scufl2:workflowBundleId \"28f7c554-4f35-401f-b34b-516e9a0ef731\";\n" + 
		"\n" + 
		"        Where 28f7c554-4f35-401f-b34b-516e9a0ef731 must be a randomly\n" + 
		"        generated UUID."})
	@subPropertyOf({"http://www.w3.org/2002/07/owl#sameAs"})
	@Iri("http://ns.taverna.org.uk/2010/scufl2#globalBaseURI")
	void setScufl2GlobalBaseURI(Set<?> scufl2GlobalBaseURI);

	@label({"Member Of"})
	@definition({"A relationship between a resource and a vocabulary encoding scheme which indicates that the resource is a member of a set."})
	@isDefinedBy({"http://dublincore.org/documents/2007/06/04/abstract-model/"})
	@Iri("http://purl.org/dc/dcam/memberOf")
	Set<VocabularyEncodingScheme> getDcamMemberOf();
	@label({"Member Of"})
	@definition({"A relationship between a resource and a vocabulary encoding scheme which indicates that the resource is a member of a set."})
	@isDefinedBy({"http://dublincore.org/documents/2007/06/04/abstract-model/"})
	@Iri("http://purl.org/dc/dcam/memberOf")
	void setDcamMemberOf(Set<? extends VocabularyEncodingScheme> dcamMemberOf);

	@Iri("http://purl.org/dc/elements/1.1/contributor")
	Set<Object> getDcContributors();
	@Iri("http://purl.org/dc/elements/1.1/contributor")
	void setDcContributors(Set<?> dcContributors);

	@Iri("http://purl.org/dc/elements/1.1/coverage")
	Set<Object> getDcCoverages();
	@Iri("http://purl.org/dc/elements/1.1/coverage")
	void setDcCoverages(Set<?> dcCoverages);

	@Iri("http://purl.org/dc/elements/1.1/creator")
	Set<Object> getDcCreators();
	@Iri("http://purl.org/dc/elements/1.1/creator")
	void setDcCreators(Set<?> dcCreators);

	@Iri("http://purl.org/dc/elements/1.1/date")
	Set<Object> getDcDates();
	@Iri("http://purl.org/dc/elements/1.1/date")
	void setDcDates(Set<?> dcDates);

	@Iri("http://purl.org/dc/elements/1.1/description")
	Set<Object> getDcDescriptions();
	@Iri("http://purl.org/dc/elements/1.1/description")
	void setDcDescriptions(Set<?> dcDescriptions);

	@Iri("http://purl.org/dc/elements/1.1/format")
	Set<Object> getDcFormats();
	@Iri("http://purl.org/dc/elements/1.1/format")
	void setDcFormats(Set<?> dcFormats);

	@Iri("http://purl.org/dc/elements/1.1/identifier")
	Set<Object> getDcIdentifiers();
	@Iri("http://purl.org/dc/elements/1.1/identifier")
	void setDcIdentifiers(Set<?> dcIdentifiers);

	@Iri("http://purl.org/dc/elements/1.1/language")
	Set<Object> getDcLanguages();
	@Iri("http://purl.org/dc/elements/1.1/language")
	void setDcLanguages(Set<?> dcLanguages);

	@Iri("http://purl.org/dc/elements/1.1/publisher")
	Set<Object> getDcPublishers();
	@Iri("http://purl.org/dc/elements/1.1/publisher")
	void setDcPublishers(Set<?> dcPublishers);

	@Iri("http://purl.org/dc/elements/1.1/relation")
	Set<Object> getDcRelations();
	@Iri("http://purl.org/dc/elements/1.1/relation")
	void setDcRelations(Set<?> dcRelations);

	@Iri("http://purl.org/dc/elements/1.1/rights")
	Set<Object> getDcRights();
	@Iri("http://purl.org/dc/elements/1.1/rights")
	void setDcRights(Set<?> dcRights);

	@Iri("http://purl.org/dc/elements/1.1/source")
	Set<Object> getDcSources();
	@Iri("http://purl.org/dc/elements/1.1/source")
	void setDcSources(Set<?> dcSources);

	@Iri("http://purl.org/dc/elements/1.1/subject")
	Set<Object> getDcSubjects();
	@Iri("http://purl.org/dc/elements/1.1/subject")
	void setDcSubjects(Set<?> dcSubjects);

	@Iri("http://purl.org/dc/elements/1.1/title")
	Set<Object> getDcTitles();
	@Iri("http://purl.org/dc/elements/1.1/title")
	void setDcTitles(Set<?> dcTitles);

	@Iri("http://purl.org/dc/elements/1.1/type")
	Set<Object> getDcTypes();
	@Iri("http://purl.org/dc/elements/1.1/type")
	void setDcTypes(Set<?> dcTypes);

	/** A summary of the resource. */
	@label({"Abstract", "Abstract"})
	@definition({"A summary of the resource."})
	@comment({"A summary of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/abstract")
	Set<Object> getDctermsAbstracts();
	/** A summary of the resource. */
	@label({"Abstract", "Abstract"})
	@definition({"A summary of the resource."})
	@comment({"A summary of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/abstract")
	void setDctermsAbstracts(Set<?> dctermsAbstracts);

	/** 
	 * Information about who can access the resource or an indication of its security status.
	 * Access Rights may include information regarding access or restrictions based on privacy, security, or other policies.
	 */
	@label({"Access Rights", "Access Rights"})
	@description({"Access Rights may include information regarding access or restrictions based on privacy, security, or other policies."})
	@definition({"Information about who can access the resource or an indication of its security status."})
	@comment({"Information about who can access the resource or an indication of its security status.", "Access Rights may include information regarding access or restrictions based on privacy, security, or other policies."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights", "http://purl.org/dc/terms/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accessRights")
	Set<RightsStatement> getDctermsAccessRights();
	/** 
	 * Information about who can access the resource or an indication of its security status.
	 * Access Rights may include information regarding access or restrictions based on privacy, security, or other policies.
	 */
	@label({"Access Rights", "Access Rights"})
	@description({"Access Rights may include information regarding access or restrictions based on privacy, security, or other policies."})
	@definition({"Information about who can access the resource or an indication of its security status."})
	@comment({"Information about who can access the resource or an indication of its security status.", "Access Rights may include information regarding access or restrictions based on privacy, security, or other policies."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights", "http://purl.org/dc/terms/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/accessRights")
	void setDctermsAccessRights(Set<? extends RightsStatement> dctermsAccessRights);

	/** 
	 * An alternative name for the resource.
	 * The distinction between titles and alternative titles is application-specific.
	 */
	@description({"The distinction between titles and alternative titles is application-specific."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/title", "http://purl.org/dc/terms/title"})
	@label({"Alternative Title", "Alternative Title"})
	@definition({"An alternative name for the resource."})
	@note({"In current practice, this term is used primarily with literal values; however, there are important uses with non-literal values as well. As of December 2007, the DCMI Usage Board is leaving this range unspecified pending an investigation of options."})
	@comment({"An alternative name for the resource.", "The distinction between titles and alternative titles is application-specific."})
	@Iri("http://purl.org/dc/terms/alternative")
	Set<Object> getDctermsAlternatives();
	/** 
	 * An alternative name for the resource.
	 * The distinction between titles and alternative titles is application-specific.
	 */
	@description({"The distinction between titles and alternative titles is application-specific."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/title", "http://purl.org/dc/terms/title"})
	@label({"Alternative Title", "Alternative Title"})
	@definition({"An alternative name for the resource."})
	@note({"In current practice, this term is used primarily with literal values; however, there are important uses with non-literal values as well. As of December 2007, the DCMI Usage Board is leaving this range unspecified pending an investigation of options."})
	@comment({"An alternative name for the resource.", "The distinction between titles and alternative titles is application-specific."})
	@Iri("http://purl.org/dc/terms/alternative")
	void setDctermsAlternatives(Set<?> dctermsAlternatives);

	/** A class of entity for whom the resource is intended or useful. */
	@label({"Audience", "Audience"})
	@definition({"A class of entity for whom the resource is intended or useful."})
	@comment({"A class of entity for whom the resource is intended or useful."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/audience")
	Set<AgentClass> getDctermsAudiences();
	/** A class of entity for whom the resource is intended or useful. */
	@label({"Audience", "Audience"})
	@definition({"A class of entity for whom the resource is intended or useful."})
	@comment({"A class of entity for whom the resource is intended or useful."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/audience")
	void setDctermsAudiences(Set<? extends AgentClass> dctermsAudiences);

	/** Date (often a range) that the resource became or will become available. */
	@label({"Date Available", "Date Available"})
	@definition({"Date (often a range) that the resource became or will become available."})
	@comment({"Date (often a range) that the resource became or will become available."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/available")
	Set<Object> getDctermsAvailables();
	/** Date (often a range) that the resource became or will become available. */
	@label({"Date Available", "Date Available"})
	@definition({"Date (often a range) that the resource became or will become available."})
	@comment({"Date (often a range) that the resource became or will become available."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/available")
	void setDctermsAvailables(Set<?> dctermsAvailables);

	/** An established standard to which the described resource conforms. */
	@label({"Conforms To", "Conforms To"})
	@definition({"An established standard to which the described resource conforms."})
	@comment({"An established standard to which the described resource conforms."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/conformsTo")
	Set<Standard> getDctermsConformsTo();
	/** An established standard to which the described resource conforms. */
	@label({"Conforms To", "Conforms To"})
	@definition({"An established standard to which the described resource conforms."})
	@comment({"An established standard to which the described resource conforms."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/conformsTo")
	void setDctermsConformsTo(Set<? extends Standard> dctermsConformsTo);

	/** 
	 * An entity responsible for making contributions to the resource.
	 * Examples of a Contributor include a person, an organization, or a service. Typically, the name of a Contributor should be used to indicate the entity.
	 */
	@label({"Contributor", "Contributor"})
	@description({"Examples of a Contributor include a person, an organization, or a service."})
	@definition({"An entity responsible for making contributions to the resource."})
	@comment({"An entity responsible for making contributions to the resource.", "Examples of a Contributor include a person, an organization, or a service. Typically, the name of a Contributor should be used to indicate the entity."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/contributor"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/contributor")
	Set<Agent> getDctermsContributors();
	/** 
	 * An entity responsible for making contributions to the resource.
	 * Examples of a Contributor include a person, an organization, or a service. Typically, the name of a Contributor should be used to indicate the entity.
	 */
	@label({"Contributor", "Contributor"})
	@description({"Examples of a Contributor include a person, an organization, or a service."})
	@definition({"An entity responsible for making contributions to the resource."})
	@comment({"An entity responsible for making contributions to the resource.", "Examples of a Contributor include a person, an organization, or a service. Typically, the name of a Contributor should be used to indicate the entity."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/contributor"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/contributor")
	void setDctermsContributors(Set<? extends Agent> dctermsContributors);

	/** 
	 * The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.
	 * Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges.
	 * @see http://www.getty.edu/research/tools/vocabulary/tgn/index.html
	 */
	@description({"Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges."})
	@seeAlso({"http://www.getty.edu/research/tools/vocabulary/tgn/index.html"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage"})
	@label({"Coverage", "Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant."})
	@comment({"The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.", "Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges."})
	@Iri("http://purl.org/dc/terms/coverage")
	Set<LocationPeriodOrJurisdiction> getDctermsCoverages();
	/** 
	 * The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.
	 * Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges.
	 * @see http://www.getty.edu/research/tools/vocabulary/tgn/index.html
	 */
	@description({"Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges."})
	@seeAlso({"http://www.getty.edu/research/tools/vocabulary/tgn/index.html"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage"})
	@label({"Coverage", "Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant."})
	@comment({"The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.", "Spatial topic and spatial applicability may be a named place or a location specified by its geographic coordinates. Temporal topic may be a named period, date, or date range. A jurisdiction may be a named administrative entity or a geographic place to which the resource applies. Recommended best practice is to use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. Where appropriate, named places or time periods can be used in preference to numeric identifiers such as sets of coordinates or date ranges."})
	@Iri("http://purl.org/dc/terms/coverage")
	void setDctermsCoverages(Set<? extends LocationPeriodOrJurisdiction> dctermsCoverages);

	/** Date of creation of the resource. */
	@label({"Date Created", "Date Created"})
	@definition({"Date of creation of the resource."})
	@comment({"Date of creation of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/created")
	Set<Object> getDctermsCreated();
	/** Date of creation of the resource. */
	@label({"Date Created", "Date Created"})
	@definition({"Date of creation of the resource."})
	@comment({"Date of creation of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/created")
	void setDctermsCreated(Set<?> dctermsCreated);

	/** 
	 * An entity primarily responsible for making the resource.
	 * Examples of a Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity.
	 */
	@description({"Examples of a Creator include a person, an organization, or a service."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/creator", "http://purl.org/dc/terms/contributor"})
	@label({"Creator", "Creator"})
	@definition({"An entity primarily responsible for making the resource."})
	@comment({"An entity primarily responsible for making the resource.", "Examples of a Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity."})
	@Iri("http://purl.org/dc/terms/creator")
	Set<Agent> getDctermsCreators();
	/** 
	 * An entity primarily responsible for making the resource.
	 * Examples of a Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity.
	 */
	@description({"Examples of a Creator include a person, an organization, or a service."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/creator", "http://purl.org/dc/terms/contributor"})
	@label({"Creator", "Creator"})
	@definition({"An entity primarily responsible for making the resource."})
	@comment({"An entity primarily responsible for making the resource.", "Examples of a Creator include a person, an organization, or a service. Typically, the name of a Creator should be used to indicate the entity."})
	@Iri("http://purl.org/dc/terms/creator")
	void setDctermsCreators(Set<? extends Agent> dctermsCreators);

	/** 
	 * A point or period of time associated with an event in the lifecycle of the resource.
	 * Date may be used to express temporal information at any level of granularity. Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF].
	 * @see http://www.w3.org/TR/NOTE-datetime
	 */
	@description({"Date may be used to express temporal information at any level of granularity.  Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF]."})
	@seeAlso({"http://www.w3.org/TR/NOTE-datetime"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date"})
	@label({"Date", "Date"})
	@definition({"A point or period of time associated with an event in the lifecycle of the resource."})
	@comment({"A point or period of time associated with an event in the lifecycle of the resource.", "Date may be used to express temporal information at any level of granularity. Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF]."})
	@Iri("http://purl.org/dc/terms/date")
	Set<Object> getDctermsDates();
	/** 
	 * A point or period of time associated with an event in the lifecycle of the resource.
	 * Date may be used to express temporal information at any level of granularity. Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF].
	 * @see http://www.w3.org/TR/NOTE-datetime
	 */
	@description({"Date may be used to express temporal information at any level of granularity.  Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF]."})
	@seeAlso({"http://www.w3.org/TR/NOTE-datetime"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date"})
	@label({"Date", "Date"})
	@definition({"A point or period of time associated with an event in the lifecycle of the resource."})
	@comment({"A point or period of time associated with an event in the lifecycle of the resource.", "Date may be used to express temporal information at any level of granularity. Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF]."})
	@Iri("http://purl.org/dc/terms/date")
	void setDctermsDates(Set<?> dctermsDates);

	/** 
	 * Date of acceptance of the resource.
	 * Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal).
	 */
	@label({"Date Accepted", "Date Accepted"})
	@description({"Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal)."})
	@definition({"Date of acceptance of the resource."})
	@comment({"Date of acceptance of the resource.", "Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal)."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateAccepted")
	Set<Object> getDctermsDateAccepted();
	/** 
	 * Date of acceptance of the resource.
	 * Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal).
	 */
	@label({"Date Accepted", "Date Accepted"})
	@description({"Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal)."})
	@definition({"Date of acceptance of the resource."})
	@comment({"Date of acceptance of the resource.", "Examples of resources to which a Date Accepted may be relevant are a thesis (accepted by a university department) or an article (accepted by a journal)."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateAccepted")
	void setDctermsDateAccepted(Set<?> dctermsDateAccepted);

	/** Date of copyright. */
	@label({"Date Copyrighted", "Date Copyrighted"})
	@definition({"Date of copyright."})
	@comment({"Date of copyright."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateCopyrighted")
	Set<Object> getDctermsDateCopyrighted();
	/** Date of copyright. */
	@label({"Date Copyrighted", "Date Copyrighted"})
	@definition({"Date of copyright."})
	@comment({"Date of copyright."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateCopyrighted")
	void setDctermsDateCopyrighted(Set<?> dctermsDateCopyrighted);

	/** 
	 * Date of submission of the resource.
	 * Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal).
	 */
	@label({"Date Submitted", "Date Submitted"})
	@description({"Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal)."})
	@definition({"Date of submission of the resource."})
	@comment({"Date of submission of the resource.", "Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal)."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateSubmitted")
	Set<Object> getDctermsDateSubmitted();
	/** 
	 * Date of submission of the resource.
	 * Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal).
	 */
	@label({"Date Submitted", "Date Submitted"})
	@description({"Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal)."})
	@definition({"Date of submission of the resource."})
	@comment({"Date of submission of the resource.", "Examples of resources to which a Date Submitted may be relevant are a thesis (submitted to a university department) or an article (submitted to a journal)."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/dateSubmitted")
	void setDctermsDateSubmitted(Set<?> dctermsDateSubmitted);

	/** A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended. */
	@label({"Audience Education Level", "Audience Education Level"})
	@definition({"A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended."})
	@comment({"A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended."})
	@subPropertyOf({"http://purl.org/dc/terms/audience"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/educationLevel")
	Set<AgentClass> getDctermsEducationLevel();
	/** A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended. */
	@label({"Audience Education Level", "Audience Education Level"})
	@definition({"A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended."})
	@comment({"A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended."})
	@subPropertyOf({"http://purl.org/dc/terms/audience"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/educationLevel")
	void setDctermsEducationLevel(Set<? extends AgentClass> dctermsEducationLevel);

	/** The size or duration of the resource. */
	@label({"Extent", "Extent"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The size or duration of the resource."})
	@comment({"The size or duration of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format", "http://purl.org/dc/terms/format"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/extent")
	Set<SizeOrDuration> getDctermsExtents();
	/** The size or duration of the resource. */
	@label({"Extent", "Extent"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The size or duration of the resource."})
	@comment({"The size or duration of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format", "http://purl.org/dc/terms/format"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/extent")
	void setDctermsExtents(Set<? extends SizeOrDuration> dctermsExtents);

	/** 
	 * The file format, physical medium, or dimensions of the resource.
	 * Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME].
	 * @see http://www.iana.org/assignments/media-types/
	 */
	@description({"Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME]."})
	@seeAlso({"http://www.iana.org/assignments/media-types/"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format"})
	@label({"Format", "Format"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The file format, physical medium, or dimensions of the resource."})
	@comment({"The file format, physical medium, or dimensions of the resource.", "Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME]."})
	@Iri("http://purl.org/dc/terms/format")
	Set<MediaTypeOrExtent> getDctermsFormats();
	/** 
	 * The file format, physical medium, or dimensions of the resource.
	 * Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME].
	 * @see http://www.iana.org/assignments/media-types/
	 */
	@description({"Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME]."})
	@seeAlso({"http://www.iana.org/assignments/media-types/"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/format"})
	@label({"Format", "Format"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"The file format, physical medium, or dimensions of the resource."})
	@comment({"The file format, physical medium, or dimensions of the resource.", "Examples of dimensions include size and duration. Recommended best practice is to use a controlled vocabulary such as the list of Internet Media Types [MIME]."})
	@Iri("http://purl.org/dc/terms/format")
	void setDctermsFormats(Set<? extends MediaTypeOrExtent> dctermsFormats);

	/** A related resource that is substantially the same as the pre-existing described resource, but in another format. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Format", "Has Format"})
	@definition({"A related resource that is substantially the same as the pre-existing described resource, but in another format."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is substantially the same as the pre-existing described resource, but in another format."})
	@Iri("http://purl.org/dc/terms/hasFormat")
	Set<Object> getDctermsHasFormats();
	/** A related resource that is substantially the same as the pre-existing described resource, but in another format. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Format", "Has Format"})
	@definition({"A related resource that is substantially the same as the pre-existing described resource, but in another format."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is substantially the same as the pre-existing described resource, but in another format."})
	@Iri("http://purl.org/dc/terms/hasFormat")
	void setDctermsHasFormats(Set<?> dctermsHasFormats);

	/** A related resource that is included either physically or logically in the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Part", "Has Part"})
	@definition({"A related resource that is included either physically or logically in the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is included either physically or logically in the described resource."})
	@Iri("http://purl.org/dc/terms/hasPart")
	Set<Object> getDctermsHasPart();
	/** A related resource that is included either physically or logically in the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Part", "Has Part"})
	@definition({"A related resource that is included either physically or logically in the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is included either physically or logically in the described resource."})
	@Iri("http://purl.org/dc/terms/hasPart")
	void setDctermsHasPart(Set<?> dctermsHasPart);

	/** A related resource that is a version, edition, or adaptation of the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Version", "Has Version"})
	@definition({"A related resource that is a version, edition, or adaptation of the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is a version, edition, or adaptation of the described resource."})
	@Iri("http://purl.org/dc/terms/hasVersion")
	Set<Object> getDctermsHasVersions();
	/** A related resource that is a version, edition, or adaptation of the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Has Version", "Has Version"})
	@definition({"A related resource that is a version, edition, or adaptation of the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is a version, edition, or adaptation of the described resource."})
	@Iri("http://purl.org/dc/terms/hasVersion")
	void setDctermsHasVersions(Set<?> dctermsHasVersions);

	/** 
	 * An unambiguous reference to the resource within a given context.
	 * Recommended best practice is to identify the resource by means of a string conforming to a formal identification system.
	 */
	@label({"Identifier", "Identifier"})
	@description({"Recommended best practice is to identify the resource by means of a string conforming to a formal identification system. "})
	@definition({"An unambiguous reference to the resource within a given context."})
	@comment({"An unambiguous reference to the resource within a given context.", "Recommended best practice is to identify the resource by means of a string conforming to a formal identification system."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/identifier"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/identifier")
	Set<Object> getDctermsIdentifiers();
	/** 
	 * An unambiguous reference to the resource within a given context.
	 * Recommended best practice is to identify the resource by means of a string conforming to a formal identification system.
	 */
	@label({"Identifier", "Identifier"})
	@description({"Recommended best practice is to identify the resource by means of a string conforming to a formal identification system. "})
	@definition({"An unambiguous reference to the resource within a given context."})
	@comment({"An unambiguous reference to the resource within a given context.", "Recommended best practice is to identify the resource by means of a string conforming to a formal identification system."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/identifier"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/identifier")
	void setDctermsIdentifiers(Set<?> dctermsIdentifiers);

	/** 
	 * A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support.
	 * Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured. Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback.
	 */
	@label({"Instructional Method", "Instructional Method"})
	@description({"Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured.  Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback."})
	@definition({"A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support."})
	@comment({"A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support.", "Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured. Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/instructionalMethod")
	Set<MethodOfInstruction> getDctermsInstructionalMethod();
	/** 
	 * A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support.
	 * Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured. Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback.
	 */
	@label({"Instructional Method", "Instructional Method"})
	@description({"Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured.  Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback."})
	@definition({"A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support."})
	@comment({"A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support.", "Instructional Method will typically include ways of presenting instructional materials or conducting instructional activities, patterns of learner-to-learner and learner-to-instructor interactions, and mechanisms by which group and individual levels of learning are measured. Instructional methods include all aspects of the instruction and learning processes from planning and implementation through evaluation and feedback."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/instructionalMethod")
	void setDctermsInstructionalMethod(Set<? extends MethodOfInstruction> dctermsInstructionalMethod);

	/** A related resource that is substantially the same as the described resource, but in another format. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Format Of", "Is Format Of"})
	@definition({"A related resource that is substantially the same as the described resource, but in another format."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is substantially the same as the described resource, but in another format."})
	@Iri("http://purl.org/dc/terms/isFormatOf")
	Set<Object> getDctermsIsFormatOf();
	/** A related resource that is substantially the same as the described resource, but in another format. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Format Of", "Is Format Of"})
	@definition({"A related resource that is substantially the same as the described resource, but in another format."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is substantially the same as the described resource, but in another format."})
	@Iri("http://purl.org/dc/terms/isFormatOf")
	void setDctermsIsFormatOf(Set<?> dctermsIsFormatOf);

	/** A related resource in which the described resource is physically or logically included. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Part Of", "Is Part Of"})
	@definition({"A related resource in which the described resource is physically or logically included."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource in which the described resource is physically or logically included."})
	@Iri("http://purl.org/dc/terms/isPartOf")
	Set<Object> getDctermsIsPartOf();
	/** A related resource in which the described resource is physically or logically included. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Part Of", "Is Part Of"})
	@definition({"A related resource in which the described resource is physically or logically included."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource in which the described resource is physically or logically included."})
	@Iri("http://purl.org/dc/terms/isPartOf")
	void setDctermsIsPartOf(Set<?> dctermsIsPartOf);

	/** A related resource that references, cites, or otherwise points to the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Referenced By", "Is Referenced By"})
	@definition({"A related resource that references, cites, or otherwise points to the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that references, cites, or otherwise points to the described resource."})
	@Iri("http://purl.org/dc/terms/isReferencedBy")
	Set<Object> getDctermsIsReferencedBy();
	/** A related resource that references, cites, or otherwise points to the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Referenced By", "Is Referenced By"})
	@definition({"A related resource that references, cites, or otherwise points to the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that references, cites, or otherwise points to the described resource."})
	@Iri("http://purl.org/dc/terms/isReferencedBy")
	void setDctermsIsReferencedBy(Set<?> dctermsIsReferencedBy);

	/** A related resource that supplants, displaces, or supersedes the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Replaced By", "Is Replaced By"})
	@definition({"A related resource that supplants, displaces, or supersedes the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that supplants, displaces, or supersedes the described resource."})
	@Iri("http://purl.org/dc/terms/isReplacedBy")
	Set<Object> getDctermsIsReplacedBy();
	/** A related resource that supplants, displaces, or supersedes the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Replaced By", "Is Replaced By"})
	@definition({"A related resource that supplants, displaces, or supersedes the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that supplants, displaces, or supersedes the described resource."})
	@Iri("http://purl.org/dc/terms/isReplacedBy")
	void setDctermsIsReplacedBy(Set<?> dctermsIsReplacedBy);

	/** A related resource that requires the described resource to support its function, delivery, or coherence. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Required By", "Is Required By"})
	@definition({"A related resource that requires the described resource to support its function, delivery, or coherence."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that requires the described resource to support its function, delivery, or coherence."})
	@Iri("http://purl.org/dc/terms/isRequiredBy")
	Set<Object> getDctermsIsRequiredBy();
	/** A related resource that requires the described resource to support its function, delivery, or coherence. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Required By", "Is Required By"})
	@definition({"A related resource that requires the described resource to support its function, delivery, or coherence."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that requires the described resource to support its function, delivery, or coherence."})
	@Iri("http://purl.org/dc/terms/isRequiredBy")
	void setDctermsIsRequiredBy(Set<?> dctermsIsRequiredBy);

	/** 
	 * A related resource of which the described resource is a version, edition, or adaptation.
	 * Changes in version imply substantive changes in content rather than differences in format.
	 */
	@description({"Changes in version imply substantive changes in content rather than differences in format."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Version Of", "Is Version Of"})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@definition({"A related resource of which the described resource is a version, edition, or adaptation."})
	@comment({"A related resource of which the described resource is a version, edition, or adaptation.", "Changes in version imply substantive changes in content rather than differences in format."})
	@Iri("http://purl.org/dc/terms/isVersionOf")
	Set<Object> getDctermsIsVersionOf();
	/** 
	 * A related resource of which the described resource is a version, edition, or adaptation.
	 * Changes in version imply substantive changes in content rather than differences in format.
	 */
	@description({"Changes in version imply substantive changes in content rather than differences in format."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Is Version Of", "Is Version Of"})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@definition({"A related resource of which the described resource is a version, edition, or adaptation."})
	@comment({"A related resource of which the described resource is a version, edition, or adaptation.", "Changes in version imply substantive changes in content rather than differences in format."})
	@Iri("http://purl.org/dc/terms/isVersionOf")
	void setDctermsIsVersionOf(Set<?> dctermsIsVersionOf);

	/** Date of formal issuance (e.g., publication) of the resource. */
	@label({"Date Issued", "Date Issued"})
	@definition({"Date of formal issuance (e.g., publication) of the resource."})
	@comment({"Date of formal issuance (e.g., publication) of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/issued")
	Set<Object> getDctermsIssued();
	/** Date of formal issuance (e.g., publication) of the resource. */
	@label({"Date Issued", "Date Issued"})
	@definition({"Date of formal issuance (e.g., publication) of the resource."})
	@comment({"Date of formal issuance (e.g., publication) of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/issued")
	void setDctermsIssued(Set<?> dctermsIssued);

	/** 
	 * A language of the resource.
	 * Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646].
	 * @see http://www.ietf.org/rfc/rfc4646.txt
	 */
	@description({"Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646]."})
	@seeAlso({"http://www.ietf.org/rfc/rfc4646.txt"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/language"})
	@label({"Language", "Language"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. as a language code or tag) but only as a non-literal value."})
	@definition({"A language of the resource."})
	@comment({"A language of the resource.", "Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646]."})
	@Iri("http://purl.org/dc/terms/language")
	Set<LinguisticSystem> getDctermsLanguages();
	/** 
	 * A language of the resource.
	 * Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646].
	 * @see http://www.ietf.org/rfc/rfc4646.txt
	 */
	@description({"Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646]."})
	@seeAlso({"http://www.ietf.org/rfc/rfc4646.txt"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/language"})
	@label({"Language", "Language"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. as a language code or tag) but only as a non-literal value."})
	@definition({"A language of the resource."})
	@comment({"A language of the resource.", "Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646]."})
	@Iri("http://purl.org/dc/terms/language")
	void setDctermsLanguages(Set<? extends LinguisticSystem> dctermsLanguages);

	/** A legal document giving official permission to do something with the resource. */
	@label({"License", "License"})
	@definition({"A legal document giving official permission to do something with the resource."})
	@comment({"A legal document giving official permission to do something with the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights", "http://purl.org/dc/terms/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/license")
	Set<LicenseDocument> getDctermsLicenses();
	/** A legal document giving official permission to do something with the resource. */
	@label({"License", "License"})
	@definition({"A legal document giving official permission to do something with the resource."})
	@comment({"A legal document giving official permission to do something with the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights", "http://purl.org/dc/terms/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/license")
	void setDctermsLicenses(Set<? extends LicenseDocument> dctermsLicenses);

	/** 
	 * An entity that mediates access to the resource and for whom the resource is intended or useful.
	 * In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver.
	 */
	@label({"Mediator", "Mediator"})
	@description({"In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver."})
	@definition({"An entity that mediates access to the resource and for whom the resource is intended or useful."})
	@comment({"An entity that mediates access to the resource and for whom the resource is intended or useful.", "In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver."})
	@subPropertyOf({"http://purl.org/dc/terms/audience"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/mediator")
	Set<AgentClass> getDctermsMediators();
	/** 
	 * An entity that mediates access to the resource and for whom the resource is intended or useful.
	 * In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver.
	 */
	@label({"Mediator", "Mediator"})
	@description({"In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver."})
	@definition({"An entity that mediates access to the resource and for whom the resource is intended or useful."})
	@comment({"An entity that mediates access to the resource and for whom the resource is intended or useful.", "In an educational context, a mediator might be a parent, teacher, teaching assistant, or care-giver."})
	@subPropertyOf({"http://purl.org/dc/terms/audience"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/mediator")
	void setDctermsMediators(Set<? extends AgentClass> dctermsMediators);

	/** Date on which the resource was changed. */
	@label({"Date Modified", "Date Modified"})
	@definition({"Date on which the resource was changed."})
	@comment({"Date on which the resource was changed."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/modified")
	Set<Object> getDctermsModified();
	/** Date on which the resource was changed. */
	@label({"Date Modified", "Date Modified"})
	@definition({"Date on which the resource was changed."})
	@comment({"Date on which the resource was changed."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/modified")
	void setDctermsModified(Set<?> dctermsModified);

	/** 
	 * A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation.
	 * The statement may include a description of any changes successive custodians made to the resource.
	 */
	@label({"Provenance", "Provenance"})
	@description({"The statement may include a description of any changes successive custodians made to the resource."})
	@definition({"A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation."})
	@comment({"A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation.", "The statement may include a description of any changes successive custodians made to the resource."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/provenance")
	Set<ProvenanceStatement> getDctermsProvenances();
	/** 
	 * A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation.
	 * The statement may include a description of any changes successive custodians made to the resource.
	 */
	@label({"Provenance", "Provenance"})
	@description({"The statement may include a description of any changes successive custodians made to the resource."})
	@definition({"A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation."})
	@comment({"A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation.", "The statement may include a description of any changes successive custodians made to the resource."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/provenance")
	void setDctermsProvenances(Set<? extends ProvenanceStatement> dctermsProvenances);

	/** 
	 * An entity responsible for making the resource available.
	 * Examples of a Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity.
	 */
	@label({"Publisher", "Publisher"})
	@description({"Examples of a Publisher include a person, an organization, or a service."})
	@definition({"An entity responsible for making the resource available."})
	@comment({"An entity responsible for making the resource available.", "Examples of a Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/publisher"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/publisher")
	Set<Agent> getDctermsPublishers();
	/** 
	 * An entity responsible for making the resource available.
	 * Examples of a Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity.
	 */
	@label({"Publisher", "Publisher"})
	@description({"Examples of a Publisher include a person, an organization, or a service."})
	@definition({"An entity responsible for making the resource available."})
	@comment({"An entity responsible for making the resource available.", "Examples of a Publisher include a person, an organization, or a service. Typically, the name of a Publisher should be used to indicate the entity."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/publisher"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/publisher")
	void setDctermsPublishers(Set<? extends Agent> dctermsPublishers);

	/** A related resource that is referenced, cited, or otherwise pointed to by the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"References", "References"})
	@definition({"A related resource that is referenced, cited, or otherwise pointed to by the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is referenced, cited, or otherwise pointed to by the described resource."})
	@Iri("http://purl.org/dc/terms/references")
	Set<Object> getDctermsReferences();
	/** A related resource that is referenced, cited, or otherwise pointed to by the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"References", "References"})
	@definition({"A related resource that is referenced, cited, or otherwise pointed to by the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is referenced, cited, or otherwise pointed to by the described resource."})
	@Iri("http://purl.org/dc/terms/references")
	void setDctermsReferences(Set<?> dctermsReferences);

	/** 
	 * A related resource.
	 * Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
	 */
	@description({"Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system. "})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation"})
	@label({"Relation", "Relation"})
	@definition({"A related resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource.", "Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@Iri("http://purl.org/dc/terms/relation")
	Set<Object> getDctermsRelations();
	/** 
	 * A related resource.
	 * Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
	 */
	@description({"Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system. "})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation"})
	@label({"Relation", "Relation"})
	@definition({"A related resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource.", "Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@Iri("http://purl.org/dc/terms/relation")
	void setDctermsRelations(Set<?> dctermsRelations);

	/** A related resource that is supplanted, displaced, or superseded by the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Replaces", "Replaces"})
	@definition({"A related resource that is supplanted, displaced, or superseded by the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is supplanted, displaced, or superseded by the described resource."})
	@Iri("http://purl.org/dc/terms/replaces")
	Set<Object> getDctermsReplaces();
	/** A related resource that is supplanted, displaced, or superseded by the described resource. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Replaces", "Replaces"})
	@definition({"A related resource that is supplanted, displaced, or superseded by the described resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is supplanted, displaced, or superseded by the described resource."})
	@Iri("http://purl.org/dc/terms/replaces")
	void setDctermsReplaces(Set<?> dctermsReplaces);

	/** A related resource that is required by the described resource to support its function, delivery, or coherence. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Requires", "Requires"})
	@definition({"A related resource that is required by the described resource to support its function, delivery, or coherence."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is required by the described resource to support its function, delivery, or coherence."})
	@Iri("http://purl.org/dc/terms/requires")
	Set<Object> getDctermsRequires();
	/** A related resource that is required by the described resource to support its function, delivery, or coherence. */
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/relation", "http://purl.org/dc/terms/relation"})
	@label({"Requires", "Requires"})
	@definition({"A related resource that is required by the described resource to support its function, delivery, or coherence."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource that is required by the described resource to support its function, delivery, or coherence."})
	@Iri("http://purl.org/dc/terms/requires")
	void setDctermsRequires(Set<?> dctermsRequires);

	/** 
	 * Information about rights held in and over the resource.
	 * Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights.
	 */
	@label({"Rights", "Rights"})
	@description({"Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights."})
	@definition({"Information about rights held in and over the resource."})
	@comment({"Information about rights held in and over the resource.", "Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/rights")
	Set<RightsStatement> getDctermsRights();
	/** 
	 * Information about rights held in and over the resource.
	 * Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights.
	 */
	@label({"Rights", "Rights"})
	@description({"Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights."})
	@definition({"Information about rights held in and over the resource."})
	@comment({"Information about rights held in and over the resource.", "Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/rights"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/rights")
	void setDctermsRights(Set<? extends RightsStatement> dctermsRights);

	/** A person or organization owning or managing rights over the resource. */
	@label({"Rights Holder", "Rights Holder"})
	@definition({"A person or organization owning or managing rights over the resource."})
	@comment({"A person or organization owning or managing rights over the resource."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/rightsHolder")
	Set<Agent> getDctermsRightsHolder();
	/** A person or organization owning or managing rights over the resource. */
	@label({"Rights Holder", "Rights Holder"})
	@definition({"A person or organization owning or managing rights over the resource."})
	@comment({"A person or organization owning or managing rights over the resource."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/rightsHolder")
	void setDctermsRightsHolder(Set<? extends Agent> dctermsRightsHolder);

	/** 
	 * A related resource from which the described resource is derived.
	 * The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
	 */
	@description({"The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/source", "http://purl.org/dc/terms/relation"})
	@label({"Source", "Source"})
	@definition({"A related resource from which the described resource is derived."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource from which the described resource is derived.", "The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@Iri("http://purl.org/dc/terms/source")
	Set<Object> getDctermsSources();
	/** 
	 * A related resource from which the described resource is derived.
	 * The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system.
	 */
	@description({"The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/source", "http://purl.org/dc/terms/relation"})
	@label({"Source", "Source"})
	@definition({"A related resource from which the described resource is derived."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"A related resource from which the described resource is derived.", "The described resource may be derived from the related resource in whole or in part. Recommended best practice is to identify the related resource by means of a string conforming to a formal identification system."})
	@Iri("http://purl.org/dc/terms/source")
	void setDctermsSources(Set<?> dctermsSources);

	/** Spatial characteristics of the resource. */
	@label({"Spatial Coverage", "Spatial Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"Spatial characteristics of the resource."})
	@comment({"Spatial characteristics of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage", "http://purl.org/dc/terms/coverage"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/spatial")
	Set<Location> getDctermsSpatials();
	/** Spatial characteristics of the resource. */
	@label({"Spatial Coverage", "Spatial Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"Spatial characteristics of the resource."})
	@comment({"Spatial characteristics of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage", "http://purl.org/dc/terms/coverage"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/spatial")
	void setDctermsSpatials(Set<? extends Location> dctermsSpatials);

	/** 
	 * The topic of the resource.
	 * Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary. To describe the spatial or temporal topic of the resource, use the Coverage element.
	 */
	@description({"Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/subject"})
	@label({"Subject", "Subject"})
	@definition({"The topic of the resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"The topic of the resource.", "Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary. To describe the spatial or temporal topic of the resource, use the Coverage element."})
	@Iri("http://purl.org/dc/terms/subject")
	Set<Object> getDctermsSubjects();
	/** 
	 * The topic of the resource.
	 * Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary. To describe the spatial or temporal topic of the resource, use the Coverage element.
	 */
	@description({"Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary."})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/subject"})
	@label({"Subject", "Subject"})
	@definition({"The topic of the resource."})
	@note({"This term is intended to be used with non-literal values as defined in the DCMI Abstract Model (http://dublincore.org/documents/abstract-model/).  As of December 2007, the DCMI Usage Board is seeking a way to express this intention with a formal range declaration."})
	@comment({"The topic of the resource.", "Typically, the subject will be represented using keywords, key phrases, or classification codes. Recommended best practice is to use a controlled vocabulary. To describe the spatial or temporal topic of the resource, use the Coverage element."})
	@Iri("http://purl.org/dc/terms/subject")
	void setDctermsSubjects(Set<?> dctermsSubjects);

	/** A list of subunits of the resource. */
	@label({"Table Of Contents", "Table Of Contents"})
	@definition({"A list of subunits of the resource."})
	@comment({"A list of subunits of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/tableOfContents")
	Set<Object> getDctermsTableOfContents();
	/** A list of subunits of the resource. */
	@label({"Table Of Contents", "Table Of Contents"})
	@definition({"A list of subunits of the resource."})
	@comment({"A list of subunits of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/description", "http://purl.org/dc/terms/description"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/tableOfContents")
	void setDctermsTableOfContents(Set<?> dctermsTableOfContents);

	/** Temporal characteristics of the resource. */
	@label({"Temporal Coverage", "Temporal Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"Temporal characteristics of the resource."})
	@comment({"Temporal characteristics of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage", "http://purl.org/dc/terms/coverage"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/temporal")
	Set<PeriodOfTime> getDctermsTemporals();
	/** Temporal characteristics of the resource. */
	@label({"Temporal Coverage", "Temporal Coverage"})
	@note({"In this ontology this property has been defined as an object property, meaning when using this ontology you cannot encode its value as a literal (e.g. by using DCMI's DCSV) but only as a related description."})
	@definition({"Temporal characteristics of the resource."})
	@comment({"Temporal characteristics of the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/coverage", "http://purl.org/dc/terms/coverage"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/temporal")
	void setDctermsTemporals(Set<? extends PeriodOfTime> dctermsTemporals);

	/** A name given to the resource. */
	@label({"Title", "Title"})
	@note({"In current practice, this term is used primarily with literal values; however, there are important uses with non-literal values as well. As of December 2007, the DCMI Usage Board is leaving this range unspecified pending an investigation of options."})
	@definition({"A name given to the resource."})
	@comment({"A name given to the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/title"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/title")
	Set<Object> getDctermsTitles();
	/** A name given to the resource. */
	@label({"Title", "Title"})
	@note({"In current practice, this term is used primarily with literal values; however, there are important uses with non-literal values as well. As of December 2007, the DCMI Usage Board is leaving this range unspecified pending an investigation of options."})
	@definition({"A name given to the resource."})
	@comment({"A name given to the resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/title"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/title")
	void setDctermsTitles(Set<?> dctermsTitles);

	/** 
	 * The nature or genre of the resource.
	 * Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element.
	 * @see http://dublincore.org/documents/dcmi-type-vocabulary/
	 */
	@description({"Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element."})
	@seeAlso({"http://dublincore.org/documents/dcmi-type-vocabulary/"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/type"})
	@label({"Type", "Type"})
	@definition({"The nature or genre of the resource."})
	@comment({"The nature or genre of the resource.", "Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element."})
	@Iri("http://purl.org/dc/terms/type")
	Set<Class> getDctermsTypes();
	/** 
	 * The nature or genre of the resource.
	 * Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element.
	 * @see http://dublincore.org/documents/dcmi-type-vocabulary/
	 */
	@description({"Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element."})
	@seeAlso({"http://dublincore.org/documents/dcmi-type-vocabulary/"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/type"})
	@label({"Type", "Type"})
	@definition({"The nature or genre of the resource."})
	@comment({"The nature or genre of the resource.", "Recommended best practice is to use a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, physical medium, or dimensions of the resource, use the Format element."})
	@Iri("http://purl.org/dc/terms/type")
	void setDctermsTypes(Set<? extends Class> dctermsTypes);

	/** Date (often a range) of validity of a resource. */
	@label({"Date Valid", "Date Valid"})
	@definition({"Date (often a range) of validity of a resource."})
	@comment({"Date (often a range) of validity of a resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/valid")
	Set<Object> getDctermsValids();
	/** Date (often a range) of validity of a resource. */
	@label({"Date Valid", "Date Valid"})
	@definition({"Date (often a range) of validity of a resource."})
	@comment({"Date (often a range) of validity of a resource."})
	@subPropertyOf({"http://purl.org/dc/elements/1.1/date", "http://purl.org/dc/terms/date"})
	@isDefinedBy({"http://purl.org/dc/terms/"})
	@Iri("http://purl.org/dc/terms/valid")
	void setDctermsValids(Set<?> dctermsValids);

	/** A description of the subject resource. */
	@label({"comment"})
	@comment({"A description of the subject resource.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#comment")
	Set<Object> getRdfsComments();
	/** A description of the subject resource. */
	@label({"comment"})
	@comment({"A description of the subject resource.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#comment")
	void setRdfsComments(Set<?> rdfsComments);

	/** The defininition of the subject resource. */
	@label({"isDefinedBy"})
	@comment({"The defininition of the subject resource."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#isDefinedBy")
	Set<Object> getRdfsIsDefinedBy();
	/** The defininition of the subject resource. */
	@label({"isDefinedBy"})
	@comment({"The defininition of the subject resource."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#isDefinedBy")
	void setRdfsIsDefinedBy(Set<?> rdfsIsDefinedBy);

	/** A human-readable name for the subject. */
	@label({"label"})
	@comment({"A human-readable name for the subject.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#label")
	Set<Object> getRdfsLabels();
	/** A human-readable name for the subject. */
	@label({"label"})
	@comment({"A human-readable name for the subject.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#label")
	void setRdfsLabels(Set<?> rdfsLabels);

	/** A member of the subject resource. */
	@label({"member"})
	@comment({"A member of the subject resource."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#member")
	Set<Object> getRdfsMembers();
	/** A member of the subject resource. */
	@label({"member"})
	@comment({"A member of the subject resource."})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#member")
	void setRdfsMembers(Set<?> rdfsMembers);

	/** Further information about the subject resource. */
	@label({"seeAlso"})
	@comment({"Further information about the subject resource.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#seeAlso")
	Set<Object> getRdfsSeeAlso();
	/** Further information about the subject resource. */
	@label({"seeAlso"})
	@comment({"Further information about the subject resource.", ""})
	@isDefinedBy({"http://www.w3.org/2000/01/rdf-schema#"})
	@Iri("http://www.w3.org/2000/01/rdf-schema#seeAlso")
	void setRdfsSeeAlso(Set<?> rdfsSeeAlso);

	@Iri("http://www.w3.org/2002/07/owl#sameAs")
	Set<Object> getOwlSameAs();
	@Iri("http://www.w3.org/2002/07/owl#sameAs")
	void setOwlSameAs(Set<?> owlSameAs);

	@Iri("http://www.w3.org/2002/07/owl#topObjectProperty")
	Set<Object> getOwlTopObjectProperties();
	@Iri("http://www.w3.org/2002/07/owl#topObjectProperty")
	void setOwlTopObjectProperties(Set<?> owlTopObjectProperties);

	@Iri("http://www.w3.org/2002/07/owl#versionInfo")
	Set<Object> getOwlVersionInfo();
	@Iri("http://www.w3.org/2002/07/owl#versionInfo")
	void setOwlVersionInfo(Set<?> owlVersionInfo);

	@Iri("http://www.w3.org/2003/06/sw-vocab-status/ns#term_status")
	Set<Object> getVsTerm_status();
	@Iri("http://www.w3.org/2003/06/sw-vocab-status/ns#term_status")
	void setVsTerm_status(Set<?> vsTerm_status);

	/** 
	 * The range of skos:altLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
	 */
	@label({"alternative label"})
	@definition({"An alternative lexical label for a resource."})
	@comment({"The range of skos:altLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@example({"Acronyms, abbreviations, spelling variants, and irregular plural/singular forms may be included among the alternative labels for a concept. Mis-spelled terms are normally included as hidden labels (see skos:hiddenLabel)."})
	@Iri("http://www.w3.org/2004/02/skos/core#altLabel")
	Set<Object> getSkosAltLabels();
	/** 
	 * The range of skos:altLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
	 */
	@label({"alternative label"})
	@definition({"An alternative lexical label for a resource."})
	@comment({"The range of skos:altLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@example({"Acronyms, abbreviations, spelling variants, and irregular plural/singular forms may be included among the alternative labels for a concept. Mis-spelled terms are normally included as hidden labels (see skos:hiddenLabel)."})
	@Iri("http://www.w3.org/2004/02/skos/core#altLabel")
	void setSkosAltLabels(Set<?> skosAltLabels);

	@label({"has broader match"})
	@definition({"skos:broadMatch is used to state a hierarchical mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#broader", "http://www.w3.org/2004/02/skos/core#mappingRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#broadMatch")
	Set<Object> getSkosBroadMatch();
	@label({"has broader match"})
	@definition({"skos:broadMatch is used to state a hierarchical mapping link between two conceptual resources in different concept schemes."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#broader", "http://www.w3.org/2004/02/skos/core#mappingRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#broadMatch")
	void setSkosBroadMatch(Set<?> skosBroadMatch);

	/** Broader concepts are typically rendered as parents in a concept hierarchy (tree). */
	@label({"has broader"})
	@definition({"Relates a concept to a concept that is more general in meaning."})
	@comment({"Broader concepts are typically rendered as parents in a concept hierarchy (tree)."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#broaderTransitive"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broader is only used to assert an immediate (i.e. direct) hierarchical link between two conceptual resources."})
	@Iri("http://www.w3.org/2004/02/skos/core#broader")
	Set<Object> getSkosBroaders();
	/** Broader concepts are typically rendered as parents in a concept hierarchy (tree). */
	@label({"has broader"})
	@definition({"Relates a concept to a concept that is more general in meaning."})
	@comment({"Broader concepts are typically rendered as parents in a concept hierarchy (tree)."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#broaderTransitive"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broader is only used to assert an immediate (i.e. direct) hierarchical link between two conceptual resources."})
	@Iri("http://www.w3.org/2004/02/skos/core#broader")
	void setSkosBroaders(Set<?> skosBroaders);

	@label({"change note"})
	@definition({"A note about a modification to a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#changeNote")
	Set<Object> getSkosChangeNotes();
	@label({"change note"})
	@definition({"A note about a modification to a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#changeNote")
	void setSkosChangeNotes(Set<?> skosChangeNotes);

	@label({"has close match"})
	@definition({"skos:closeMatch is used to link two concepts that are sufficiently similar that they can be used interchangeably in some information retrieval applications. In order to avoid the possibility of \"compound errors\" when combining mappings across more than two concept schemes, skos:closeMatch is not declared to be a transitive property."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#closeMatch")
	Set<Object> getSkosCloseMatch();
	@label({"has close match"})
	@definition({"skos:closeMatch is used to link two concepts that are sufficiently similar that they can be used interchangeably in some information retrieval applications. In order to avoid the possibility of \"compound errors\" when combining mappings across more than two concept schemes, skos:closeMatch is not declared to be a transitive property."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#mappingRelation"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#closeMatch")
	void setSkosCloseMatch(Set<?> skosCloseMatch);

	@label({"definition"})
	@definition({"A statement or formal explanation of the meaning of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#definition")
	Set<Object> getSkosDefinitions();
	@label({"definition"})
	@definition({"A statement or formal explanation of the meaning of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#definition")
	void setSkosDefinitions(Set<?> skosDefinitions);

	@label({"editorial note"})
	@definition({"A note for an editor, translator or maintainer of the vocabulary."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#editorialNote")
	Set<Object> getSkosEditorialNotes();
	@label({"editorial note"})
	@definition({"A note for an editor, translator or maintainer of the vocabulary."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#editorialNote")
	void setSkosEditorialNotes(Set<?> skosEditorialNotes);

	/** skos:exactMatch is disjoint with each of the properties skos:broadMatch and skos:relatedMatch. */
	@label({"has exact match"})
	@definition({"skos:exactMatch is used to link two concepts, indicating a high degree of confidence that the concepts can be used interchangeably across a wide range of information retrieval applications. skos:exactMatch is a transitive property, and is a sub-property of skos:closeMatch."})
	@comment({"skos:exactMatch is disjoint with each of the properties skos:broadMatch and skos:relatedMatch."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#closeMatch"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#exactMatch")
	Set<Object> getSkosExactMatch();
	/** skos:exactMatch is disjoint with each of the properties skos:broadMatch and skos:relatedMatch. */
	@label({"has exact match"})
	@definition({"skos:exactMatch is used to link two concepts, indicating a high degree of confidence that the concepts can be used interchangeably across a wide range of information retrieval applications. skos:exactMatch is a transitive property, and is a sub-property of skos:closeMatch."})
	@comment({"skos:exactMatch is disjoint with each of the properties skos:broadMatch and skos:relatedMatch."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#closeMatch"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#exactMatch")
	void setSkosExactMatch(Set<?> skosExactMatch);

	@label({"example"})
	@definition({"An example of the use of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#example")
	Set<Object> getSkosExamples();
	@label({"example"})
	@definition({"An example of the use of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#example")
	void setSkosExamples(Set<?> skosExamples);

	/** 
	 * The range of skos:hiddenLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
	 */
	@label({"hidden label"})
	@definition({"A lexical label for a resource that should be hidden when generating visual displays of the resource, but should still be accessible to free text search operations."})
	@comment({"The range of skos:hiddenLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#hiddenLabel")
	Set<Object> getSkosHiddenLabels();
	/** 
	 * The range of skos:hiddenLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties.
	 */
	@label({"hidden label"})
	@definition({"A lexical label for a resource that should be hidden when generating visual displays of the resource, but should still be accessible to free text search operations."})
	@comment({"The range of skos:hiddenLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#hiddenLabel")
	void setSkosHiddenLabels(Set<?> skosHiddenLabels);

	@label({"history note"})
	@definition({"A note about the past state/use/meaning of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#historyNote")
	Set<Object> getSkosHistoryNotes();
	@label({"history note"})
	@definition({"A note about the past state/use/meaning of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#historyNote")
	void setSkosHistoryNotes(Set<?> skosHistoryNotes);

	@label({"is in scheme"})
	@definition({"Relates a resource (for example a concept) to a concept scheme in which it is included."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"A concept may be a member of more than one concept scheme."})
	@Iri("http://www.w3.org/2004/02/skos/core#inScheme")
	Set<ConceptScheme> getSkosInScheme();
	@label({"is in scheme"})
	@definition({"Relates a resource (for example a concept) to a concept scheme in which it is included."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"A concept may be a member of more than one concept scheme."})
	@Iri("http://www.w3.org/2004/02/skos/core#inScheme")
	void setSkosInScheme(Set<? extends ConceptScheme> skosInScheme);

	/** Narrower concepts are typically rendered as children in a concept hierarchy (tree). */
	@label({"has narrower"})
	@definition({"Relates a concept to a concept that is more specific in meaning."})
	@comment({"Narrower concepts are typically rendered as children in a concept hierarchy (tree)."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#narrowerTransitive"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broader is only used to assert an immediate (i.e. direct) hierarchical link between two conceptual resources."})
	@Iri("http://www.w3.org/2004/02/skos/core#narrower")
	Set<Object> getSkosNarrowers();
	/** Narrower concepts are typically rendered as children in a concept hierarchy (tree). */
	@label({"has narrower"})
	@definition({"Relates a concept to a concept that is more specific in meaning."})
	@comment({"Narrower concepts are typically rendered as children in a concept hierarchy (tree)."})
	@subPropertyOf({"http://www.w3.org/2004/02/skos/core#narrowerTransitive"})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:broader is only used to assert an immediate (i.e. direct) hierarchical link between two conceptual resources."})
	@Iri("http://www.w3.org/2004/02/skos/core#narrower")
	void setSkosNarrowers(Set<?> skosNarrowers);

	@label({"notation"})
	@definition({"A notation, also known as classification code, is a string of characters such as \"T58.5\" or \"303.4833\" used to uniquely identify a concept within the scope of a given concept scheme."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:notation is used with a typed literal in the object position of the triple."})
	@Iri("http://www.w3.org/2004/02/skos/core#notation")
	Set<Object> getSkosNotations();
	@label({"notation"})
	@definition({"A notation, also known as classification code, is a string of characters such as \"T58.5\" or \"303.4833\" used to uniquely identify a concept within the scope of a given concept scheme."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"By convention, skos:notation is used with a typed literal in the object position of the triple."})
	@Iri("http://www.w3.org/2004/02/skos/core#notation")
	void setSkosNotations(Set<?> skosNotations);

	@label({"note"})
	@definition({"A general note, for any purpose."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"This property may be used directly, or as a super-property for more specific note types."})
	@Iri("http://www.w3.org/2004/02/skos/core#note")
	Set<Object> getSkosNotes();
	@label({"note"})
	@definition({"A general note, for any purpose."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@scopeNote({"This property may be used directly, or as a super-property for more specific note types."})
	@Iri("http://www.w3.org/2004/02/skos/core#note")
	void setSkosNotes(Set<?> skosNotes);

	/** 
	 * A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.
	 * The range of skos:prefLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise
	 *       disjoint properties.
	 */
	@label({"preferred label"})
	@definition({"The preferred lexical label for a resource, in a given language."})
	@comment({"A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.", "The range of skos:prefLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise\n" + 
		"      disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#prefLabel")
	Set<Object> getSkosPrefLabels();
	/** 
	 * A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.
	 * The range of skos:prefLabel is the class of RDF plain literals.
	 * skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise
	 *       disjoint properties.
	 */
	@label({"preferred label"})
	@definition({"The preferred lexical label for a resource, in a given language."})
	@comment({"A resource has no more than one value of skos:prefLabel per language tag, and no more than one value of skos:prefLabel without language tag.", "The range of skos:prefLabel is the class of RDF plain literals.", "skos:prefLabel, skos:altLabel and skos:hiddenLabel are pairwise\n" + 
		"      disjoint properties."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#prefLabel")
	void setSkosPrefLabels(Set<?> skosPrefLabels);

	@label({"scope note"})
	@definition({"A note that helps to clarify the meaning and/or the use of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#scopeNote")
	Set<Object> getSkosScopeNotes();
	@label({"scope note"})
	@definition({"A note that helps to clarify the meaning and/or the use of a concept."})
	@isDefinedBy({"http://www.w3.org/2004/02/skos/core"})
	@Iri("http://www.w3.org/2004/02/skos/core#scopeNote")
	void setSkosScopeNotes(Set<?> skosScopeNotes);

	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#aq")
	Set<Object> getProvAqs();
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#aq")
	void setProvAqs(Set<?> provAqs);

	/** Classify prov-o terms into three categories, including 'starting-point', 'qualifed', and 'extended'. This classification is used by the prov-o html document to gently introduce prov-o terms to its users. */
	@comment({"Classify prov-o terms into three categories, including 'starting-point', 'qualifed', and 'extended'. This classification is used by the prov-o html document to gently introduce prov-o terms to its users. "})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#category")
	Set<Object> getProvCategories();
	/** Classify prov-o terms into three categories, including 'starting-point', 'qualifed', and 'extended'. This classification is used by the prov-o html document to gently introduce prov-o terms to its users. */
	@comment({"Classify prov-o terms into three categories, including 'starting-point', 'qualifed', and 'extended'. This classification is used by the prov-o html document to gently introduce prov-o terms to its users. "})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#category")
	void setProvCategories(Set<?> provCategories);

	/** Classify prov-o terms into six components according to prov-dm, including 'agents-responsibility', 'alternate', 'annotations', 'collections', 'derivations', and 'entities-activities'. This classification is used so that readers of prov-o specification can find its correspondence with the prov-dm specification. */
	@comment({"Classify prov-o terms into six components according to prov-dm, including 'agents-responsibility', 'alternate', 'annotations', 'collections', 'derivations', and 'entities-activities'. This classification is used so that readers of prov-o specification can find its correspondence with the prov-dm specification."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#component")
	Set<Object> getProvComponents();
	/** Classify prov-o terms into six components according to prov-dm, including 'agents-responsibility', 'alternate', 'annotations', 'collections', 'derivations', and 'entities-activities'. This classification is used so that readers of prov-o specification can find its correspondence with the prov-dm specification. */
	@comment({"Classify prov-o terms into six components according to prov-dm, including 'agents-responsibility', 'alternate', 'annotations', 'collections', 'derivations', and 'entities-activities'. This classification is used so that readers of prov-o specification can find its correspondence with the prov-dm specification."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#component")
	void setProvComponents(Set<?> provComponents);

	/** A reference to the principal section of the PROV-CONSTRAINTS document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-CONSTRAINTS document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#constraints")
	Set<Object> getProvConstraints();
	/** A reference to the principal section of the PROV-CONSTRAINTS document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-CONSTRAINTS document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#constraints")
	void setProvConstraints(Set<?> provConstraints);

	/** A definition quoted from PROV-DM or PROV-CONSTRAINTS that describes the concept expressed with this OWL term. */
	@comment({"A definition quoted from PROV-DM or PROV-CONSTRAINTS that describes the concept expressed with this OWL term."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#definition")
	Set<Object> getProvDefinitions();
	/** A definition quoted from PROV-DM or PROV-CONSTRAINTS that describes the concept expressed with this OWL term. */
	@comment({"A definition quoted from PROV-DM or PROV-CONSTRAINTS that describes the concept expressed with this OWL term."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#definition")
	void setProvDefinitions(Set<?> provDefinitions);

	/** A reference to the principal section of the PROV-DM document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-DM document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#dm")
	Set<Object> getProvDms();
	/** A reference to the principal section of the PROV-DM document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-DM document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#dm")
	void setProvDms(Set<?> provDms);

	/** A note by the OWL development team about how this term expresses the PROV-DM concept, or how it should be used in context of semantic web or linked data. */
	@comment({"A note by the OWL development team about how this term expresses the PROV-DM concept, or how it should be used in context of semantic web or linked data."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#editorialNote")
	Set<Object> getProvEditorialNotes();
	/** A note by the OWL development team about how this term expresses the PROV-DM concept, or how it should be used in context of semantic web or linked data. */
	@comment({"A note by the OWL development team about how this term expresses the PROV-DM concept, or how it should be used in context of semantic web or linked data."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#editorialNote")
	void setProvEditorialNotes(Set<?> provEditorialNotes);

	/** When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one. */
	@comment({"When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one."})
	@subPropertyOf({"http://www.w3.org/ns/prov#definition"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#editorsDefinition")
	Set<Object> getProvEditorsDefinitions();
	/** When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one. */
	@comment({"When the prov-o term does not have a definition drawn from prov-dm, and the prov-o editor provides one."})
	@subPropertyOf({"http://www.w3.org/ns/prov#definition"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#editorsDefinition")
	void setProvEditorsDefinitions(Set<?> provEditorsDefinitions);

	@category({"expanded"})
	@label({"influenced"})
	@component({"agents-responsibility"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#influenced")
	Set<Object> getProvInfluenced();
	@category({"expanded"})
	@label({"influenced"})
	@component({"agents-responsibility"})
	@sharesDefinitionWith({"http://www.w3.org/ns/prov#Influence"})
	@inverse({"wasInfluencedBy"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#influenced")
	void setProvInfluenced(Set<?> provInfluenced);

	/** 
	 * PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used.
	 * @see http://www.w3.org/TR/prov-o/#names-of-inverse-properties
	 */
	@seeAlso({"http://www.w3.org/TR/prov-o/#names-of-inverse-properties"})
	@comment({"PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#inverse")
	Set<Object> getProvInverses();
	/** 
	 * PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used.
	 * @see http://www.w3.org/TR/prov-o/#names-of-inverse-properties
	 */
	@seeAlso({"http://www.w3.org/TR/prov-o/#names-of-inverse-properties"})
	@comment({"PROV-O does not define all property inverses. The directionalities defined in PROV-O should be given preference over those not defined. However, if users wish to name the inverse of a PROV-O property, the local name given by prov:inverse should be used."})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#inverse")
	void setProvInverses(Set<?> provInverses);

	/** A reference to the principal section of the PROV-M document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-M document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#n")
	Set<Object> getProvNs();
	/** A reference to the principal section of the PROV-M document that describes this concept. */
	@comment({"A reference to the principal section of the PROV-M document that describes this concept."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#n")
	void setProvNs(Set<?> provNs);

	/** 
	 * This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. 
	 * 
	 * Example annotation:
	 * 
	 *     prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .
	 * 
	 * Then this unqualified assertion:
	 * 
	 *     :entity1 prov:wasGeneratedBy :activity1 .
	 * 
	 * can be qualified by adding:
	 * 
	 *    :entity1 prov:qualifiedGeneration :entity1Gen .
	 *    :entity1Gen 
	 *        a prov:Generation, prov:Influence;
	 *        prov:activity :activity1;
	 *        :customValue 1337 .
	 * 
	 * Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class.
	 */
	@comment({"This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. \n" + 
		"\n" + 
		"Example annotation:\n" + 
		"\n" + 
		"    prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .\n" + 
		"\n" + 
		"Then this unqualified assertion:\n" + 
		"\n" + 
		"    :entity1 prov:wasGeneratedBy :activity1 .\n" + 
		"\n" + 
		"can be qualified by adding:\n" + 
		"\n" + 
		"   :entity1 prov:qualifiedGeneration :entity1Gen .\n" + 
		"   :entity1Gen \n" + 
		"       a prov:Generation, prov:Influence;\n" + 
		"       prov:activity :activity1;\n" + 
		"       :customValue 1337 .\n" + 
		"\n" + 
		"Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#qualifiedForm")
	Set<Object> getProvQualifiedForm();
	/** 
	 * This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. 
	 * 
	 * Example annotation:
	 * 
	 *     prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .
	 * 
	 * Then this unqualified assertion:
	 * 
	 *     :entity1 prov:wasGeneratedBy :activity1 .
	 * 
	 * can be qualified by adding:
	 * 
	 *    :entity1 prov:qualifiedGeneration :entity1Gen .
	 *    :entity1Gen 
	 *        a prov:Generation, prov:Influence;
	 *        prov:activity :activity1;
	 *        :customValue 1337 .
	 * 
	 * Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class.
	 */
	@comment({"This annotation property links a subproperty of prov:wasInfluencedBy with the subclass of prov:Influence and the qualifying property that are used to qualify it. \n" + 
		"\n" + 
		"Example annotation:\n" + 
		"\n" + 
		"    prov:wasGeneratedBy prov:qualifiedForm prov:qualifiedGeneration, prov:Generation .\n" + 
		"\n" + 
		"Then this unqualified assertion:\n" + 
		"\n" + 
		"    :entity1 prov:wasGeneratedBy :activity1 .\n" + 
		"\n" + 
		"can be qualified by adding:\n" + 
		"\n" + 
		"   :entity1 prov:qualifiedGeneration :entity1Gen .\n" + 
		"   :entity1Gen \n" + 
		"       a prov:Generation, prov:Influence;\n" + 
		"       prov:activity :activity1;\n" + 
		"       :customValue 1337 .\n" + 
		"\n" + 
		"Note how the value of the unqualified influence (prov:wasGeneratedBy :activity1) is mirrored as the value of the prov:activity (or prov:entity, or prov:agent) property on the influence class."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#qualifiedForm")
	void setProvQualifiedForm(Set<?> provQualifiedForm);

	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#sharesDefinitionWith")
	Set<Object> getProvSharesDefinitionWith();
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#sharesDefinitionWith")
	void setProvSharesDefinitionWith(Set<?> provSharesDefinitionWith);

	@Iri("http://www.w3.org/ns/prov#todo")
	Set<Object> getProvTodos();
	@Iri("http://www.w3.org/ns/prov#todo")
	void setProvTodos(Set<?> provTodos);

	/** Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation. */
	@comment({"Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#unqualifiedForm")
	Set<Object> getProvUnqualifiedForm();
	/** Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation. */
	@comment({"Classes and properties used to qualify relationships are annotated with prov:unqualifiedForm to indicate the property used to assert an unqualified provenance relation."})
	@subPropertyOf({"http://www.w3.org/2000/01/rdf-schema#seeAlso"})
	@isDefinedBy({"http://www.w3.org/ns/prov#"})
	@Iri("http://www.w3.org/ns/prov#unqualifiedForm")
	void setProvUnqualifiedForm(Set<?> provUnqualifiedForm);

	/** A checksum for the DNA of some thing. Joke. */
	@label({"DNA checksum"})
	@term_status({"archaic"})
	@comment({"A checksum for the DNA of some thing. Joke."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/dnaChecksum")
	Set<Object> getFoafDnaChecksums();
	/** A checksum for the DNA of some thing. Joke. */
	@label({"DNA checksum"})
	@term_status({"archaic"})
	@comment({"A checksum for the DNA of some thing. Joke."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/dnaChecksum")
	void setFoafDnaChecksums(Set<?> foafDnaChecksums);

	/** The given name of some person. */
	@label({"Given name"})
	@term_status({"testing"})
	@comment({"The given name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/givenName")
	Set<Object> getFoafGivenNames();
	/** The given name of some person. */
	@label({"Given name"})
	@term_status({"testing"})
	@comment({"The given name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/givenName")
	void setFoafGivenNames(Set<?> foafGivenNames);

	/** The given name of some person. */
	@label({"Given name"})
	@term_status({"archaic"})
	@comment({"The given name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/givenname")
	Set<Object> getFoafGivennames();
	/** The given name of some person. */
	@label({"Given name"})
	@term_status({"archaic"})
	@comment({"The given name of some person."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/givenname")
	void setFoafGivennames(Set<?> foafGivennames);

	/** Indicates the class of individuals that are a member of a Group */
	@label({"membershipClass"})
	@term_status({"unstable"})
	@comment({"Indicates the class of individuals that are a member of a Group"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/membershipClass")
	Set<Object> getFoafMembershipClasses();
	/** Indicates the class of individuals that are a member of a Group */
	@label({"membershipClass"})
	@term_status({"unstable"})
	@comment({"Indicates the class of individuals that are a member of a Group"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/membershipClass")
	void setFoafMembershipClasses(Set<?> foafMembershipClasses);

	/** A short informal nickname characterising an agent (includes login identifiers, IRC and other chat nicknames). */
	@label({"nickname"})
	@term_status({"testing"})
	@comment({"A short informal nickname characterising an agent (includes login identifiers, IRC and other chat nicknames)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/nick")
	Set<Object> getFoafNicks();
	/** A short informal nickname characterising an agent (includes login identifiers, IRC and other chat nicknames). */
	@label({"nickname"})
	@term_status({"testing"})
	@comment({"A short informal nickname characterising an agent (includes login identifiers, IRC and other chat nicknames)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/nick")
	void setFoafNicks(Set<?> foafNicks);

	/** A phone,  specified using fully qualified tel: URI scheme (refs: http://www.w3.org/Addressing/schemes.html#tel). */
	@label({"phone"})
	@term_status({"testing"})
	@comment({"A phone,  specified using fully qualified tel: URI scheme (refs: http://www.w3.org/Addressing/schemes.html#tel)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/phone")
	Set<Object> getFoafPhones();
	/** A phone,  specified using fully qualified tel: URI scheme (refs: http://www.w3.org/Addressing/schemes.html#tel). */
	@label({"phone"})
	@term_status({"testing"})
	@comment({"A phone,  specified using fully qualified tel: URI scheme (refs: http://www.w3.org/Addressing/schemes.html#tel)."})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/phone")
	void setFoafPhones(Set<?> foafPhones);

	/** Title (Mr, Mrs, Ms, Dr. etc) */
	@label({"title"})
	@term_status({"testing"})
	@comment({"Title (Mr, Mrs, Ms, Dr. etc)"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/title")
	Set<Object> getFoafTitles();
	/** Title (Mr, Mrs, Ms, Dr. etc) */
	@label({"title"})
	@term_status({"testing"})
	@comment({"Title (Mr, Mrs, Ms, Dr. etc)"})
	@isDefinedBy({"http://xmlns.com/foaf/0.1/"})
	@Iri("http://xmlns.com/foaf/0.1/title")
	void setFoafTitles(Set<?> foafTitles);

	@Iri("http://xmlns.com/wot/0.1/assurance")
	Set<Object> getWotAssurances();
	@Iri("http://xmlns.com/wot/0.1/assurance")
	void setWotAssurances(Set<?> wotAssurances);

	@Iri("http://xmlns.com/wot/0.1/src_assurance")
	Set<Object> getWotSrc_assurances();
	@Iri("http://xmlns.com/wot/0.1/src_assurance")
	void setWotSrc_assurances(Set<?> wotSrc_assurances);

}
