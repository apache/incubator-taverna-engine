package no.s11.w3.prov.taverna.ui;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import net.sf.taverna.raven.appconfig.ApplicationConfig;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.lineageservice.URIGenerator;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataflowInvocation;
import net.sf.taverna.t2.provenance.lineageservice.utils.Port;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcessorEnactment;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.reference.T2Reference;
import no.s11.w3.prov.elmo.Agent;
import no.s11.w3.prov.elmo.Entity;
import no.s11.w3.prov.elmo.ProcessExecution;
import no.s11.w3.prov.elmo.ProvenanceContainer;

import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

public class W3ProvenanceExport {

	private ProvenanceAccess provenanceAccess;

	public W3ProvenanceExport() {
	}

	private static ProvenanceURIGenerator uriGenerator = new ProvenanceURIGenerator();

	public SesameManager makeElmoManager() {
		ElmoModule module = new ElmoModule(getClass().getClassLoader());
		SesameManagerFactory factory = new SesameManagerFactory(module);
		factory.setInferencingEnabled(true);
		return factory.createElmoManager();
	}

	public W3ProvenanceExport(ProvenanceAccess provenanceAccess) {
		this.setProvenanceAccess(provenanceAccess);
	}

	private static final class ProvenanceURIGenerator extends URIGenerator {


		public String makeDataflowInvocationURI(String workflowRunId,
				String dataflowInvocationId) {
			return makeWFInstanceURI(workflowRunId) + "workflow/" + dataflowInvocationId + "/";
		}

		public String makeProcessExecution(String workflowRunId,
				String processEnactmentId) {
			return makeWFInstanceURI(workflowRunId) + "process/" + processEnactmentId + "/";
		}
	}

	enum Direction {
		INPUTS, OUTPUTS;
	}

	public void exportAsW3Prov(String workflowRunId, BufferedOutputStream outStream)
			throws RepositoryException, RDFHandlerException {

		SesameManager elmoManager = makeElmoManager();
		String runURI = uriGenerator.makeWFInstanceURI(workflowRunId);
		// FIXME: Should this be "" to indicate the current file?
		ProvenanceContainer provContainer = elmoManager.create(
				new QName(runURI, "provenanceContainer"), ProvenanceContainer.class);
		// TODO: Link provContainer to anything?
		//elmoManager.persist(provContainer);
		
		// Mini-provenance about this provenance trace
		String versionName = ApplicationConfig.getInstance().getName();
		Agent tavernaAgent = elmoManager.create(
				new QName("http://ns.taverna.org.uk/2011/software/", versionName), Agent.class);
		ProcessExecution storeProvenance = elmoManager.create(ProcessExecution.class);
		storeProvenance.getProvIsControlledBy().add(tavernaAgent);
		tavernaAgent.getProvIsParticipantIn().add(storeProvenance);
		provContainer.getProvIsGeneratedBy().add(storeProvenance);
		// The store-provenance-process used the workflow run as input
		storeProvenance.getProvUsed().add(elmoManager.create(new QName(runURI), Entity.class, ProcessExecution.class));
	//	elmoManager.persist(provContainer);
		//elmoManager.persist(storeProvenance);
	
		
		DataflowInvocation dataflowInvocation = provenanceAccess.getDataflowInvocation(workflowRunId);
		//String dataflowURI = uriGenerator.makeDataflowInvocationURI(workflowRunId, dataflowInvocation.getDataflowInvocationId());
		ProcessExecution wfProcess = elmoManager.create(new QName(runURI), ProcessExecution.class, Agent.class);
		wfProcess.getProvIsControlledBy().add(tavernaAgent);
		// inverse property
		tavernaAgent.getProvIsParticipantIn().add(wfProcess);
		// TODO: start, stop?

		// Workflow inputs and outputs
		storeEntitities(dataflowInvocation.getInputsDataBindingId(), wfProcess,
				Direction.INPUTS, elmoManager);
		// FIXME: These entities come out as "generated" by multiple processes
		storeEntitities(dataflowInvocation.getOutputsDataBindingId(), wfProcess,
				Direction.OUTPUTS, elmoManager);
//		elmoManager.persist(wfProcess);
		
		
		List<ProcessorEnactment> processorEnactments = provenanceAccess
				.getProcessorEnactments(workflowRunId);
		// This will also include processor enactments in nested workflows
		for (ProcessorEnactment pe : processorEnactments) {
			String parentURI = pe.getParentProcessorEnactmentId();
			if (parentURI == null) {
				// Top-level workflow
				parentURI = runURI;
			} else {
				// inside nested wf - this will be parent processenactment
				parentURI = uriGenerator.makeProcessExecution(
						pe.getWorkflowRunId(), pe.getProcessEnactmentId());
			}
			String processURI = uriGenerator.makeProcessExecution(
					pe.getWorkflowRunId(), pe.getProcessEnactmentId());
			ProcessExecution process = elmoManager.create(
					new QName(processURI), ProcessExecution.class);
			Agent parentProcess = elmoManager.designate(new QName(parentURI), Agent.class, ProcessExecution.class);
			process.getProvIsControlledBy().add(parentProcess);
			// inverse property
			parentProcess.getProvIsParticipantIn().add(process);
			
			// TODO: start, stop?

			// TODO: work out preceeding and controlling from workflow definitions

			// TODO: Linking this to the processor in the workflow definition?

			// TODO: How to link together iterations on a single processor and the collections
			// they are iterating over and creating? 
			// Need 'virtual' ProcessExecution for iteration.
			
			// TODO: Activity/service details from definition?
			
			// Inputs and outputs
			storeEntitities(pe.getInitialInputsDataBindingId(), process,
					Direction.INPUTS, elmoManager);
			storeEntitities(pe.getFinalOutputsDataBindingId(), process,
					Direction.OUTPUTS, elmoManager);

//			elmoManager.persist(process);
		}

		// Save the whole thing
		ContextAwareConnection connection = elmoManager.getConnection();
		connection.setNamespace("scufl2",
				"http://ns.taverna.org.uk/2010/scufl2#");
		connection
				.setNamespace("prov", "http://w3.org/Prov.owl#");
		connection.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
		connection.export(new RDFXMLPrettyWriter(outStream));
		//connection.export(new OrganizedRDFWriter(new N3Writer(outStream)));

	}

	private String getProcessorName(String processorId) {		
		ProvenanceProcessor processor = provenanceAccess.getProvenanceProcessor(processorId);
		// TODO: Cache same processorId?
		return processor.getProcessorName();
	}

	private void storeEntitities(String dataBindingId,
			ProcessExecution process, Direction direction,
			SesameManager elmoManager) {

		Map<Port, T2Reference> inputs = provenanceAccess
				.getDataBindings(dataBindingId);
		for (Entry<Port, T2Reference> inputEntry : inputs.entrySet()) {
			Port port = inputEntry.getKey();
			T2Reference t2Ref = inputEntry.getValue();

			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());

			Entity entity = elmoManager
					.create(new QName(dataURI), Entity.class);

			if (direction == Direction.INPUTS) {
				process.getProvUsed().add(entity);
				// This is the inverse super-property, we should not need to do
				// this
				// if inferencing is turned on
				entity.getProvIsParticipantIn().add(process);
			} else {
				entity.getProvIsGeneratedBy().add(process);
				// No equivalent inverse property in process!
			}
			String portURI = uriGenerator.makePortURI(port.getWorkflowId(),
					port.getProcessorName(), port.getPortName(),
					port.isInputPort());
			// when was it generated/used?
			// in which role? (ie. port)

//			elmoManager.persist(entity);
		}

	}

	public ProvenanceAccess getProvenanceAccess() {
		return provenanceAccess;
	}

	public void setProvenanceAccess(ProvenanceAccess provenanceAccess) {
		this.provenanceAccess = provenanceAccess;
	}

}
