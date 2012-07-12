package no.s11.w3.prov.taverna.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import net.sf.taverna.raven.appconfig.ApplicationConfig;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.lineageservice.URIGenerator;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataflowInvocation;
import net.sf.taverna.t2.provenance.lineageservice.utils.Port;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcessorEnactment;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.reference.T2Reference;

import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.w3.provo.Activity;
import org.w3.provo.Agent;
import org.w3.provo.Entity;
import org.w3.provo.Generation;
import org.w3.provo.ProvenanceContainer;
import org.w3.provo.QualifiedInvolvement;
import org.w3.provo.Recipe;
import org.w3.provo.Role;
import org.w3.provo.Usage;
import org.w3.time.Instant;

public class W3ProvenanceExport {

	private static final URIImpl SAMEAS = new URIImpl("http://www.w3.org/2002/07/owl#sameAs");

	private ProvenanceAccess provenanceAccess;

	private DatatypeFactory datatypeFactory;
	
	private ProvenanceURIGenerator uriGenerator = new ProvenanceURIGenerator() ;

	private String workflowRunId;

	private Map<File, T2Reference> fileToT2Reference = Collections.emptyMap();

	public Map<File, T2Reference> getFileToT2Reference() {
		return fileToT2Reference;
	}

	public SesameManager makeElmoManager() {
		ElmoModule module = new ElmoModule(getClass().getClassLoader());
		SesameManagerFactory factory = new SesameManagerFactory(module);
		factory.setInferencingEnabled(true);
		return factory.createElmoManager();
	}

	public W3ProvenanceExport(ProvenanceAccess provenanceAccess, String workflowRunId) {
		this.setWorkflowRunId(workflowRunId);
		this.setProvenanceAccess(provenanceAccess);
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Can't find a DatatypeFactory implementation", e);
		}
	}

	private final class ProvenanceURIGenerator extends URIGenerator {
		
		// Make URIs match with Scufl2
		@Override
		public String makeWorkflowURI(String workflowID) {
			return makeWorkflowBundleURI(workflowRunId) + "workflow/" + 
				provenanceAccess.getWorkflowNameByWorkflowID(workflowID) + "/";
		}
		
		public String makeWorkflowBundleURI(String workflowRunId) {
			
			
			return "http://ns.taverna.org.uk/2010/workflowBundle/" + provenanceAccess.getTopLevelWorkflowID(workflowRunId) + "/";
		}

		public String makePortURI(String wfId, String pName, String vName,
				boolean inputPort) {		
			String base;
			if (pName == null) {
				base = makeWorkflowURI(wfId);
			} else {
				base = makeProcessorURI(pName, wfId) ;
			}
			return base + (inputPort ? "in/" : "out/") + escape(vName);
		}

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

	public void exportAsW3Prov(BufferedOutputStream outStream)
			throws RepositoryException, RDFHandlerException {

		SesameManager elmoManager = makeElmoManager();
		String runURI = uriGenerator.makeWFInstanceURI(getWorkflowRunId());
		// FIXME: Should this be "" to indicate the current file?
		// FIXME: Should this not be an Account instead?
		ProvenanceContainer provContainer = elmoManager.create(
				new QName(runURI, "provenanceContainer"), ProvenanceContainer.class, Entity.class);
		// TODO: Link provContainer to anything?
		//elmoManager.persist(provContainer);
		
		// Mini-provenance about this provenance trace
 		
 		
 		
		Agent tavernaAgent = elmoManager.create(Agent.class, Activity.class);

		Activity storeProvenance = elmoManager.create(Activity.class);
		storeProvenance.getProvWasControlledBy().add(tavernaAgent);		
		// The agent is an execution of the Taverna software (e.g. also an Activity)
		String versionName = ApplicationConfig.getInstance().getName();
		((Activity)tavernaAgent).getProvHadRecipe().add(elmoManager.create(
				new QName("http://ns.taverna.org.uk/2011/software/", versionName), Recipe.class));

		((Entity)provContainer).setProvWasGeneratedBy(storeProvenance);
		// The store-provenance-process used the workflow run as input
		storeProvenance.getProvUsed().add(elmoManager.create(new QName(runURI), Entity.class, Activity.class));

	
		
		DataflowInvocation dataflowInvocation = provenanceAccess.getDataflowInvocation(getWorkflowRunId());
		//String dataflowURI = uriGenerator.makeDataflowInvocationURI(workflowRunId, dataflowInvocation.getDataflowInvocationId());
		Activity wfProcess = elmoManager.create(new QName(runURI), Activity.class, Agent.class);
		wfProcess.getProvWasControlledBy().add(tavernaAgent);				
		// Recipe
		String wfUri = uriGenerator.makeWorkflowURI(dataflowInvocation.getWorkflowId());
		// TODO: Also make the recipe a Scufl2 Workflow
		Recipe recipe = elmoManager.create(new QName(wfUri), Recipe.class);
		wfProcess.getProvHadRecipe().add(recipe);
		// TODO: start, stop?		
		

		// Workflow inputs and outputs
		storeEntitities(dataflowInvocation.getInputsDataBindingId(), wfProcess,
				Direction.INPUTS, elmoManager);
		// FIXME: These entities come out as "generated" by multiple processes
		storeEntitities(dataflowInvocation.getOutputsDataBindingId(), wfProcess,
				Direction.OUTPUTS, elmoManager);
//		elmoManager.persist(wfProcess);
		
		
		List<ProcessorEnactment> processorEnactments = provenanceAccess
				.getProcessorEnactments(getWorkflowRunId());
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
			Activity process = elmoManager.create(
					new QName(processURI), Activity.class);
			Agent parentProcess = elmoManager.designate(new QName(parentURI), Agent.class, Activity.class);
			process.getProvWasControlledBy().add(parentProcess);
			
			// start/stop			
			setStartedEnded(process, pe.getEnactmentStarted(), pe.getEnactmentEnded(), elmoManager);
			


			// TODO: work out preceeding and controlling from workflow definitions

			
			
			// TODO: Linking to the processor in the workflow definition?			
			ProvenanceProcessor provenanceProcessor = provenanceAccess.getProvenanceProcessor(pe.getProcessorId());			
			String processorURI = uriGenerator.makeProcessorURI(provenanceProcessor.getProcessorName(), provenanceProcessor.getWorkflowId());
			// TODO: Also make the recipe a Scufl2 Processor
			recipe = elmoManager.create(new QName(processorURI), Recipe.class);
			process.getProvHadRecipe().add(recipe);
			

			
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
				.setNamespace("prov", "http://www.w3.org/ns/prov-o/");
		connection.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
//		connection.export(new OrganizedRDFWriter(new RDFXMLPrettyWriter(outStream)));
		connection.export(new OrganizedRDFWriter(new TurtleWriter(outStream)));

	}

	private void setStartedEnded(Activity activity, Timestamp enactmentStarted,
			Timestamp enactmentEnded, SesameManager elmoManager) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(enactmentStarted);			
		XMLGregorianCalendar started = datatypeFactory.newXMLGregorianCalendar(cal);
		cal.setTime(enactmentEnded);
		XMLGregorianCalendar ended = datatypeFactory.newXMLGregorianCalendar(cal);
		activity.setProvStartedAt(elmoManager.create(Instant.class));
		activity.setProvEndedAt(elmoManager.create(Instant.class));
		activity.getProvStartedAt().getProvInXSDDateTime().add(started);
		activity.getProvEndedAt().getProvInXSDDateTime().add(ended);
		
	}

	private void storeEntitities(String dataBindingId,
			Activity activity, Direction direction,
			SesameManager elmoManager) {

		Map<Port, T2Reference> inputs = provenanceAccess
				.getDataBindings(dataBindingId);

		for (Entry<File, T2Reference> entry : getFileToT2Reference().entrySet()) {
			File file = entry.getKey();
			T2Reference t2Ref = entry.getValue();
			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());
			try {
				elmoManager.getConnection().add(new URIImpl(dataURI), SAMEAS,
						new URIImpl(file.toURI().toASCIIString()));
			} catch (RepositoryException e) {
				// FIXME: Fail properly
				throw new RuntimeException("Can't store reference for " + file, e);
			}
		}

		
		for (Entry<Port, T2Reference> inputEntry : inputs.entrySet()) {
			Port port = inputEntry.getKey();
			T2Reference t2Ref = inputEntry.getValue();

			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());

			Entity entity = elmoManager
					.create(new QName(dataURI), Entity.class);


			
			if (direction == Direction.INPUTS) {
				activity.getProvUsed().add(entity);
			} else {
				if (entity.getProvWasGeneratedBy() != null) {
					// Double-output, alias the entity with a fresh one
					// to avoid double-generation
					Entity viewOfEntity = elmoManager.create(Entity.class);
					viewOfEntity.getProvWasComplementOf().add(entity);
					entity = viewOfEntity;
				}
				entity.setProvWasGeneratedBy(activity);
				// No equivalent inverse property in activity
			}
			
			QualifiedInvolvement involvement;
			if (direction == Direction.INPUTS) {
				involvement = elmoManager.create(Usage.class);
				activity.getProvHadQualifiedUsage().add((Usage)involvement);
			} else {
				involvement = elmoManager.create(Generation.class);
				activity.getProvHadQualifiedGeneration().add((Generation)involvement);
			}
			involvement.getProvHadQualifiedEntity().add(entity);

			
			String processerName = null;
			if (port.getProcessorId() != null) {
				// Not a workflow port
				ProvenanceProcessor p = provenanceAccess.getProvenanceProcessor(port.getProcessorId());
				processerName = p.getProcessorName();
			}			
			port.getProcessorId();
			String portURI = uriGenerator.makePortURI(port.getWorkflowId(),
					processerName, port.getPortName(),
					port.isInputPort());
			Role portRole = elmoManager.create(new QName(portURI), Role.class);			
			involvement.getProvHadRole().add(portRole);

//			elmoManager.persist(entity);
		}

	}

	public ProvenanceAccess getProvenanceAccess() {
		return provenanceAccess;
	}

	public void setProvenanceAccess(ProvenanceAccess provenanceAccess) {
		this.provenanceAccess = provenanceAccess;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	public void setFileToT2Reference(Map<File, T2Reference> fileToT2Reference) {
		this.fileToT2Reference = fileToT2Reference;		
	}

}
