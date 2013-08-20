package org.purl.wf4ever.provtaverna.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.sf.taverna.raven.appconfig.ApplicationConfig;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.lineageservice.URIGenerator;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataflowInvocation;
import net.sf.taverna.t2.provenance.lineageservice.utils.Port;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcessorEnactment;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.purl.wf4ever.provtaverna.owl.ProvModel;
import org.purl.wf4ever.provtaverna.owl.TavernaProvModel;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Resource;

import uk.org.taverna.scufl2.api.common.URITools;

public class W3ProvenanceExport {

    private static ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
    
	private URITools uriTools = new URITools();

	private static Logger logger = Logger.getLogger(W3ProvenanceExport.class);

	protected Map<T2Reference, File> seenReferences = new HashMap<T2Reference, File>();

	private static final int NANOSCALE = 9;

	private ProvenanceAccess provenanceAccess;

	private DatatypeFactory datatypeFactory;

	private ProvenanceURIGenerator uriGenerator = new ProvenanceURIGenerator();

	private String workflowRunId;

	private Map<File, T2Reference> fileToT2Reference = Collections.emptyMap();

	private File baseFolder;

	private File intermediatesDirectory;

	private Saver saver;

	private Map<String, Individual> describedEntities = new HashMap<String, Individual>();

    private TavernaProvModel provModel = new TavernaProvModel();;

	public File getIntermediatesDirectory() {
		return intermediatesDirectory;
	}

	public File getBaseFolder() {
		return baseFolder;
	}

	public Map<File, T2Reference> getFileToT2Reference() {
		return fileToT2Reference;
	}

//	protected <I> void repopulateRegistry(ServiceRegistry<?, I> registry,
//			Class<I> spi) {
//		ClassLoader cl = classLoaderForServiceLoader(spi);
//		logger.info("Selected classloader " + cl + " for registry of " + spi);
//		for (I service : ServiceLoader.load(spi, cl)) {
//			registry.add(service);
//		}
//	}

	private ClassLoader classLoaderForServiceLoader(Class<?> mustHave) {
		List<ClassLoader> possibles = Arrays.asList(Thread.currentThread()
				.getContextClassLoader(), getClass().getClassLoader(), mustHave
				.getClassLoader());

		for (ClassLoader cl : possibles) {
			if (cl == null) {
				continue;
			}
			try {
				if (cl.loadClass(mustHave.getCanonicalName()) == mustHave) {
					return cl;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		// Final fall-back, the old..
		return ClassLoader.getSystemClassLoader();
	}

	public W3ProvenanceExport(ProvenanceAccess provenanceAccess,
			String workflowRunId, Saver saver) {
		this.saver = saver;
		this.setWorkflowRunId(workflowRunId);
		this.setProvenanceAccess(provenanceAccess);


		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException(
					"Can't find a DatatypeFactory implementation", e);
		}
	}

	private final class ProvenanceURIGenerator extends URIGenerator {

		// Make URIs match with Scufl2
		@Override
		public String makeWorkflowURI(String workflowID) {
			return makeWorkflowBundleURI(workflowRunId) + "workflow/"
					+ provenanceAccess.getWorkflowNameByWorkflowID(workflowID)
					+ "/";
		}

		public String makeWorkflowBundleURI(String workflowRunId) {

			return "http://ns.taverna.org.uk/2010/workflowBundle/"
					+ provenanceAccess.getTopLevelWorkflowID(workflowRunId)
					+ "/";
		}

		public String makePortURI(String wfId, String pName, String vName,
				boolean inputPort) {
			String base;
			if (pName == null) {
				base = makeWorkflowURI(wfId);
			} else {
				base = makeProcessorURI(pName, wfId);
			}
			return base + (inputPort ? "in/" : "out/") + escape(vName);
		}

		public String makeDataflowInvocationURI(String workflowRunId,
				String dataflowInvocationId) {
			return makeWFInstanceURI(workflowRunId) + "workflow/"
					+ dataflowInvocationId + "/";
		}

		public String makeProcessExecution(String workflowRunId,
				String processEnactmentId) {
			return makeWFInstanceURI(workflowRunId) + "process/"
					+ processEnactmentId + "/";
		}
	}

	enum Direction {
		INPUTS("in"), OUTPUTS("out");
		private final String path;

		Direction(String path) {
			this.path = path;

		}

		public String getPath() {
			return path;
		}
	}

	public void exportAsW3Prov(BufferedOutputStream outStream, URI base)
			throws IOException {

		// TODO: Make this thread safe using contexts?

		GregorianCalendar startedProvExportAt = new GregorianCalendar();

		URI runURI = URI.create(uriGenerator.makeWFInstanceURI(getWorkflowRunId()));
		// FIXME: Should this be "" to indicate the current file?
		// FIXME: Should this not be an Account instead?

		Individual bundle = provModel.createBundle(base); 

		// Mini-provenance about this provenance trace. Unkown URI for
		// agent/activity

		
		Individual storeProvenance = provModel.createActivity(base.resolve("#taverna-prov-export"));
		storeProvenance.setLabel("taverna-prov export of workflow run provenance", "en");

		provModel.setStartedAtTime(storeProvenance, startedProvExportAt);
		
		// The agent is an execution of the Taverna software (e.g. also an
		// Activity)
		Individual tavernaAgent = provModel.createTavernaEngine(base.resolve("#taverna-engine"));
		
        String versionName = applicationConfig.getName();
		Individual plan = provModel.createPlan(URI.create("http://ns.taverna.org.uk/2011/software/"
		        + versionName));
		plan.setLabel(applicationConfig.getTitle(), "en");
		
		Individual association = provModel.setWasAssociatedWith(storeProvenance, tavernaAgent, plan);
		Individual generation = provModel.setWasGeneratedBy(bundle, storeProvenance);

		Individual wfProcess = provModel.createWorkflowRun(runURI);
		
		DataflowInvocation dataflowInvocation = provenanceAccess
				.getDataflowInvocation(getWorkflowRunId());
		String workflowName = provenanceAccess
				.getWorkflowNameByWorkflowID(dataflowInvocation.getWorkflowId());
		label(wfProcess, "Workflow run of " + workflowName);

		provModel.setWasInformedBy(storeProvenance,  wfProcess);
		String wfUri = uriGenerator.makeWorkflowURI(dataflowInvocation
		        .getWorkflowId());
		Individual wfplan = provModel.createWorkflow(URI.create(wfUri));
		Individual wfAssoc = provModel.setWasEnactedBy(wfProcess, tavernaAgent, wfplan);
		
		
		
		association.getProvHadPlans().add(wfplan);
		wfProcess.getWfprovDescribedByWorkflows().add(wfplan);

		wfProcess.getProvStartedAtTime().add(
				timestampToXmlGreg(dataflowInvocation.getInvocationStarted()));
		wfProcess.getProvEndedAtTime().add(
				timestampToXmlGreg(dataflowInvocation.getInvocationEnded()));

		
		// Workflow inputs and outputs
		storeEntitities(dataflowInvocation.getInputsDataBindingId(), wfProcess,
				Direction.INPUTS, getIntermediatesDirectory());
		// FIXME: These entities come out as "generated" by multiple processes
		storeEntitities(dataflowInvocation.getOutputsDataBindingId(),
				wfProcess, Direction.OUTPUTS, getIntermediatesDirectory());
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
			ProcessRun process = objFact.createObject(processURI, ProcessRun.class);
			WorkflowRun parentProcess = objFact.createObject(parentURI,
					WorkflowRun.class);
			process.getWfprovWasPartOfWorkflowRun().add(parentProcess);
			objCon.addDesignation(parentProcess, Resource.class).getDctermsHasPart().add(process);
			
			process.getProvStartedAtTime().add(
					timestampToXmlGreg(pe.getEnactmentStarted()));
			process.getProvEndedAtTime().add(
					timestampToXmlGreg(pe.getEnactmentEnded()));

			ProvenanceProcessor provenanceProcessor = provenanceAccess
					.getProvenanceProcessor(pe.getProcessorId());
			
			String processorURI = uriGenerator.makeProcessorURI(
					provenanceProcessor.getProcessorName(),
					provenanceProcessor.getWorkflowId());
			
			label(process,
					"Processor execution "
							+ provenanceProcessor.getProcessorName() + " ("
							+ pe.getProcessIdentifier() + ")");
			association = createObject(Association.class);
			process.getProvQualifiedAssociations().add(association);
			association.getProvAgents_1().add(tavernaAgent);
			Processor procPlan = objFact.createObject(processorURI, Processor.class);
			
			label(procPlan, "Processor " + provenanceProcessor.getProcessorName());
			association.getProvHadPlans().add(procPlan);
			process.getWfprovDescribedByProcesses_1().add(procPlan);			

			String parentWfUri = uriGenerator.makeWorkflowURI(provenanceProcessor.getWorkflowId());
			Workflow parentWf = objFact.createObject(parentWfUri, Workflow.class);
			parentWf.getWfdescHasSubProcesses().add(procPlan);
			objCon.addDesignation(parentWf, Resource.class).getDctermsHasPart().add(procPlan);
			
			
			// TODO: How to link together iterations on a single processor and
			// the collections
			// they are iterating over and creating?
			// Need 'virtual' ProcessExecution for iteration?

			// TODO: Activity/service details from definition?

			File path = getIntermediatesDirectory();

			// Inputs and outputs
			storeEntitities(pe.getInitialInputsDataBindingId(), process,
					Direction.INPUTS, path);
			storeEntitities(pe.getFinalOutputsDataBindingId(), process,
					Direction.OUTPUTS, path);
		}

		storeFileReferences();

		GregorianCalendar endedProvExportAt = new GregorianCalendar();
		storeProvenance.getProvEndedAtTime().add(
				datatypeFactory.newXMLGregorianCalendar(endedProvExportAt));

		// Save the whole thing
		ContextAwareConnection connection = objCon;
		// Taken from @prefix in
		// prov-taverna-owl-bindings/src/test/storeFileReferencesresources/handmade.ttl
		connection.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
		connection.setNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
		connection
				.setNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		connection.setNamespace("prov", "http://www.w3.org/ns/prov#");
		connection.setNamespace("wfprov", "http://purl.org/wf4ever/wfprov#");
		connection.setNamespace("wfdesc", "http://purl.org/wf4ever/wfdesc#");
		connection.setNamespace("tavernaprov",
				"http://ns.taverna.org.uk/2012/tavernaprov/");
		connection.setNamespace("doap", "http://usefulinc.com/ns/doap#");
		connection.setNamespace("cnt", "http://www.w3.org/2011/content#");
		connection.setNamespace("dcterms", "http://purl.org/dc/terms/");
		connection.setNamespace("scufl2",
				"http://ns.taverna.org.uk/2010/scufl2#");
		//connection.export(new TurtleWriterWithBase(outStream, base));
		connection.export(new ArrangedWriter(new TurtleWriterWithBase(outStream, base)));
	}

	protected void label(Individual obj, String label)  {
	    obj.setLabel(label, "en");
	}

	protected XMLGregorianCalendar timestampToXmlGreg(
			Timestamp invocationStarted) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(invocationStarted);
		XMLGregorianCalendar xmlCal = datatypeFactory
				.newXMLGregorianCalendar(cal);
		// Chop of the trailing 0-s of non-precission
		xmlCal.setFractionalSecond(BigDecimal.valueOf(
				invocationStarted.getNanos() / 1000000, NANOSCALE - 6));
		return xmlCal;
	}

	protected void storeFileReferences() throws RepositoryException {

		for (Entry<File, T2Reference> entry : getFileToT2Reference().entrySet()) {
			File file = entry.getKey();
			T2Reference t2Ref = entry.getValue();
			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());

			Artifact entity = objFact.createObject(dataURI, Artifact.class);
			Content content = objFact.createObject(
					file.toURI().toASCIIString(), Content.class);
			entity.getTavernaprovContents().add(content);

			// Add checksums
			String sha1 = saver.getSha1sums().get(file.getAbsoluteFile());
			if (sha1 != null) {
				content.getTavernaprovSha1s().add(sha1);
			}			
			String sha512 = saver.getSha512sums().get(file.getAbsoluteFile());
			if (sha512 != null) {
				content.getTavernaprovSha512s().add(sha512);
			}			
			
		}
	}

	protected void storeEntitities(String dataBindingId, ProcessRun activity,
			Direction direction, File path) throws IOException,
			RepositoryException {

		Map<Port, T2Reference> bindings = provenanceAccess
				.getDataBindings(dataBindingId);

		for (Entry<Port, T2Reference> binding : bindings.entrySet()) {
			Port port = binding.getKey();
			T2Reference t2Ref = binding.getValue();

			Artifact entity = describeEntity(t2Ref);
			if (!seenReference(t2Ref)) {
				saveReference(t2Ref);
			}

			String id = t2Ref.getLocalPart();
			String prefix = id.substring(0, 2);

			if (direction == Direction.INPUTS) {
				activity.getProvUsed().add(entity);
				activity.getWfprovUsedInputs_1().add(entity);				
			} else {
				entity.getProvWasGeneratedBy().add(activity);
				entity.getWfprovWasOutputFrom_1().add(activity);
			}

			AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage involvement;
			if (direction == Direction.INPUTS) {
				Usage usage = createObject(Usage.class);
				involvement = usage;
				activity.getProvQualifiedUsages().add(usage);
				usage.getProvEntities_1().add(entity);
			} else {
				Generation generation = createObject(Generation.class);
				involvement = generation;
				entity.getProvQualifiedGenerations().add(generation);
				generation.getProvActivities_1().add(activity);
			}

			String processorName = null;
			if (port.getProcessorId() != null) {
				// Not a workflow port
				ProvenanceProcessor p = provenanceAccess
						.getProvenanceProcessor(port.getProcessorId());
				processorName = p.getProcessorName();
			}
			port.getProcessorId();
			String portURI = uriGenerator.makePortURI(port.getWorkflowId(),
					processorName, port.getPortName(), port.isInputPort());
			Parameter portRole = objFact.createObject(portURI, port.isInputPort() ? Input.class : Output.class);
			if (processorName == null) {
				label(portRole,
						"Workflow"
								+ (port.isInputPort() ? " input " : " output ")
								+ port.getPortName());
			} else {
				label(portRole,
						processorName
								+ (port.isInputPort() ? " input " : " output ")
								+ port.getPortName());
			}
			entity.getWfprovDescribedByParameters().add(portRole);
			
			involvement.getProvHadRoles().add(portRole);

		}

	}

	protected Artifact describeEntity(T2Reference t2Ref)
			throws RepositoryException, IOException {
		String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
				.toASCIIString());

		Artifact artifact = describedEntities.get(dataURI);
		if (artifact != null) {
			return artifact;
		}
		artifact = objFact.createObject(dataURI, Artifact.class);
		describedEntities.put(dataURI, artifact);

		if (t2Ref.getReferenceType() == T2ReferenceType.ErrorDocument) {
			tavernaprov.Error error = objCon.addDesignation(artifact,
					tavernaprov.Error.class);

			ErrorDocument errorDoc = saver.getReferenceService()
					.getErrorDocumentService().getError(t2Ref);
			addMessageIfNonEmpty(error, errorDoc.getMessage());
			// getExceptionMEssage added by addStackTrace
			addStackTrace(error, errorDoc);
		} else if (t2Ref.getReferenceType() == T2ReferenceType.IdentifiedList) {
			IdentifiedList<T2Reference> list = saver.getReferenceService()
					.getListService().getList(t2Ref);
			Collection coll = objCon.addDesignation(artifact, Collection.class);

			for (T2Reference ref : list) {
				String itemURI = uriGenerator.makeT2ReferenceURI(ref.toUri()
						.toASCIIString());
				coll.getProvHadMembers().add(
						objFact.createObject(itemURI, Artifact.class));
				describeEntity(ref);
				// TODO: Record list position as well!
			}
			if (list.isEmpty()){
				objCon.addDesignation(coll, EmptyCollection.class);
			}
		}

		return artifact;
	}

	private boolean seenReference(T2Reference t2Ref) {
		return seenReferences.containsKey(t2Ref);
	}

	private File saveReference(T2Reference t2Ref) throws IOException,
			RepositoryException {
		// Avoid double-saving
		File f = seenReferences.get(t2Ref);
		if (f != null) {
			return f;
		}

		File file = referencePath(t2Ref);
		File parent = file.getParentFile();
		parent.mkdirs();
		if (t2Ref.getReferenceType() == T2ReferenceType.IdentifiedList) {
			// Write a kind of text/uri-list (but with relative URIs)
			IdentifiedList<T2Reference> list = saver.getReferenceService()
					.getListService().getList(t2Ref);
			file = new File(file.getParentFile(), file.getName() + ".list");
			FileWriterWithEncoding writer = new FileWriterWithEncoding(file,
					"utf-8");
			for (T2Reference ref : list) {
				File refFile = saveReference(ref).getAbsoluteFile();
				URI relRef = uriTools.relativePath(parent.getAbsoluteFile()
						.toURI(), refFile.getAbsoluteFile().toURI());
				writer.append(relRef.toASCIIString() + "\n");
			}

			writer.close();
		} else {

			String extension = "";
			if (t2Ref.getReferenceType() == T2ReferenceType.ErrorDocument) {
				extension = ".err";
			}

			// Capture filename with extension
			file = saver.writeDataObject(parent, file.getName(), t2Ref,
					extension);
		
			
		}
		seenReference(t2Ref, file);
		return file;
	}

	protected void addStackTrace(Error error, ErrorDocument errorDoc)
			throws RepositoryException, IOException {
		StringBuffer sb = new StringBuffer();
		addStackTrace(sb, errorDoc);
		if (sb.length() > 0) {
			error.getTavernaprovStackTrace().add(sb.toString());
		}

		for (T2Reference errRef : errorDoc.getErrorReferences()) {
			String errorURI = uriGenerator.makeT2ReferenceURI(errRef.toUri()
					.toASCIIString());
			tavernaprov.Error nested = objFact.createObject(errorURI,
					tavernaprov.Error.class);
			Entity errEntity = objCon.addDesignation(error, Entity.class);
			errEntity.getProvWasDerivedFrom().add(
					objCon.addDesignation(nested, Entity.class));
			describeEntity(errRef);
		}
	}

	protected void addStackTrace(StringBuffer sb, ErrorDocument errorDoc) {
		if (errorDoc.getExceptionMessage() != null
				&& !errorDoc.getExceptionMessage().isEmpty()) {
			sb.append(errorDoc.getExceptionMessage());
			sb.append("\n");
		}
		if (errorDoc.getStackTraceStrings() == null) {
			return;
		}
		if (sb.length() == 0) {
			sb.append("Stack trace:\n");
		}
		// Attempt to recreate Java stacktrace style
		for (StackTraceElementBean trace : errorDoc.getStackTraceStrings()) {
			sb.append("        at ");
			sb.append(trace.getClassName());
			sb.append(".");
			sb.append(trace.getMethodName());
			sb.append("(");
			sb.append(trace.getFileName());
			sb.append(":");
			sb.append(trace.getLineNumber());
			sb.append(")");
			sb.append("\n");
		}
	}

	protected void addMessageIfNonEmpty(Error error, String message) {
		if (message == null || message.isEmpty()) {
			return;
		}
		error.getTavernaprovErrorMessage().add(message);
	}

	private File referencePath(T2Reference t2Ref) {
		String local = t2Ref.getLocalPart();
		try {
			local = UUID.fromString(local).toString();
		} catch (IllegalArgumentException ex) {
			// Fallback - use full namespace/localpart
			return new File(new File(getIntermediatesDirectory(),
					t2Ref.getNamespacePart()), t2Ref.getLocalPart());
		}
		return new File(new File(getIntermediatesDirectory(), local.substring(
				0, 2)), local);
	}

	private boolean seenReference(T2Reference t2Ref, File file) {
		getFileToT2Reference().put(file, t2Ref);
		if (seenReference(t2Ref)) {
			return true;
		}
		return seenReferences.put(t2Ref, file) != null;
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
		this.fileToT2Reference = new HashMap<File, T2Reference>();
		for (Entry<File, T2Reference> entry : fileToT2Reference.entrySet()) {
			seenReference(entry.getValue(), entry.getKey());
		}
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;

	}

	public void setIntermediatesDirectory(File intermediatesDirectory) {
		this.intermediatesDirectory = intermediatesDirectory;
	}

}
