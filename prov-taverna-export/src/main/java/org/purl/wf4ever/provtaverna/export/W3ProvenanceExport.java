package org.purl.wf4ever.provtaverna.export;

import info.aduna.lang.service.ServiceRegistry;

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
import javax.xml.namespace.QName;

import net.sf.taverna.raven.appconfig.ApplicationConfig;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.lineageservice.URIGenerator;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataflowInvocation;
import net.sf.taverna.t2.provenance.lineageservice.utils.Port;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcessorEnactment;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.w3.prov.Activity;
import org.w3.prov.Agent;
import org.w3.prov.Association;
import org.w3.prov.AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage;
import org.w3.prov.Bundle;
import org.w3.prov.Entity;
import org.w3.prov.Generation;
import org.w3.prov.Plan;
import org.w3.prov.Role;
import org.w3.prov.Usage;

public class W3ProvenanceExport {

	private static Logger logger = Logger.getLogger(W3ProvenanceExport.class);

	protected Map<T2Reference, File> seenReferences = new HashMap<T2Reference, File>();
	
	private static final int NANOSCALE = 9;

	private static final URIImpl SAMEAS = new URIImpl(
			"http://www.w3.org/2002/07/owl#sameAs");

	private ProvenanceAccess provenanceAccess;

	private DatatypeFactory datatypeFactory;

	private ProvenanceURIGenerator uriGenerator = new ProvenanceURIGenerator();

	private String workflowRunId;

	private Map<File, T2Reference> fileToT2Reference = Collections.emptyMap();

	private File baseFolder;

	private File intermediatesDirectory;

	private Saver saver;

	public File getIntermediatesDirectory() {
		return intermediatesDirectory;
	}

	public File getBaseFolder() {
		return baseFolder;
	}

	public Map<File, T2Reference> getFileToT2Reference() {
		return fileToT2Reference;
	}

	public SesameManager makeElmoManager() {
		ElmoModule module = new ElmoModule(getClass().getClassLoader());
		SesameManagerFactory factory = new SesameManagerFactory(module);
		factory.setInferencingEnabled(true);
		return factory.createElmoManager();
	}

	protected void initializeRegistries() {
		// Thanks to info.aduna.lang.service.ServiceRegistry for passing down
		// the classloader
		// of the interface (!!) rather than the current thread's context class
		// loader, we'll#
		// have to do this ourself for these registries to work within Raven or
		// OSGi.

		// These are all the subclasses of
		// info.aduna.lang.service.ServiceRegistry<String, SailFactory>
		// as far as Eclipse could find..

		/*
		 * For some reason this fails with: ERROR 2012-07-04 16:06:22,830
		 * (net.sf.taverna.t2.workbench.ui.impl.Workbench:115) - Uncaught
		 * exception in Thread[SaveAllResults: Saving results to
		 * /home/stain/Desktop/popopopo.prov.ttl,6,main] java.lang.VerifyError:
		 * (class: no/s11/w3/prov/taverna/ui/W3ProvenanceExport, method:
		 * initializeRegistries signature: ()V) Incompatible argument to
		 * function at
		 * org.purl.wf4ever.provtaverna.export.SaveProvAction.saveData
		 * (SaveProvAction.java:65) at
		 * net.sf.taverna.t2.workbench.views.results.
		 * saveactions.SaveAllResultsSPI$2.run(SaveAllResultsSPI.java:177)
		 * 
		 * 
		 * or with java -noverify (..)
		 * 
		 * ERROR 2012-07-04 16:28:47,814
		 * (net.sf.taverna.t2.workbench.ui.impl.Workbench:115) - Uncaught
		 * exception in Thread[SaveAllResults: Saving results to
		 * /home/stain/Desktop/ppp.prov.ttl,6,main]
		 * java.lang.AbstractMethodError:
		 * info.aduna.lang.service.ServiceRegistry
		 * .add(Ljava/lang/Object;)Ljava/lang/Object; at
		 * org.purl.wf4ever.provtaverna
		 * .export.W3ProvenanceExport.repopulateRegistry
		 * (W3ProvenanceExport.java:132) at
		 * org.purl.wf4ever.provtaverna.export.W3ProvenanceExport
		 * .initializeRegistries(W3ProvenanceExport.java:111) at
		 * org.purl.wf4ever
		 * .provtaverna.export.W3ProvenanceExport.<init>(W3ProvenanceExport
		 * .java:162) at
		 * org.purl.wf4ever.provtaverna.export.SaveProvAction.saveData
		 * (SaveProvAction.java:65) at
		 * net.sf.taverna.t2.workbench.views.results.
		 * saveactions.SaveAllResultsSPI$2.run(SaveAllResultsSPI.java:177)
		 */

		// repopulateRegistry(BooleanQueryResultParserRegistry.getInstance(),
		// BooleanQueryResultParserFactory.class);
		// repopulateRegistry(BooleanQueryResultWriterRegistry.getInstance(),
		// BooleanQueryResultWriterFactory.class);
		// repopulateRegistry(RDFParserRegistry.getInstance(),
		// RDFParserFactory.class);
		// repopulateRegistry(RDFWriterRegistry.getInstance(),
		// RDFWriterFactory.class);
		// repopulateRegistry(TupleQueryResultParserRegistry.getInstance(),
		// TupleQueryResultParserFactory.class);
		// repopulateRegistry(TupleQueryResultWriterRegistry.getInstance(),
		// TupleQueryResultWriterFactory.class);
		// repopulateRegistry(FunctionRegistry.getInstance(), Function.class);
		// repopulateRegistry(QueryParserRegistry.getInstance(),
		// QueryParserFactory.class);
		// repopulateRegistry(RepositoryRegistry.getInstance(),
		// RepositoryFactory.class);
		// repopulateRegistry(SailRegistry.getInstance(), SailFactory.class);

		/* So instead we just do a silly, minimal workaround for what we need */
		QueryParserRegistry.getInstance().add(new SPARQLParserFactory());
	}

	protected <I> void repopulateRegistry(ServiceRegistry<?, I> registry,
			Class<I> spi) {
		ClassLoader cl = classLoaderForServiceLoader(spi);
		logger.info("Selected classloader " + cl + " for registry of " + spi);
		for (I service : ServiceLoader.load(spi, cl)) {
			registry.add(service);
		}
	}

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
		initializeRegistries();
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

	public void exportAsW3Prov(BufferedOutputStream outStream)
			throws RepositoryException, RDFHandlerException, IOException {

		GregorianCalendar startedProvExportAt = new GregorianCalendar();

		SesameManager elmoManager = makeElmoManager();
		String runURI = uriGenerator.makeWFInstanceURI(getWorkflowRunId());
		// FIXME: Should this be "" to indicate the current file?
		// FIXME: Should this not be an Account instead?
		Bundle bundle = elmoManager.create(new QName(runURI, "bundle"),
				Bundle.class, Entity.class);
		// elmoManager.persist(provContainer);

		// Mini-provenance about this provenance trace

		Agent tavernaAgent = elmoManager.create(Agent.class);
		Activity storeProvenance = elmoManager.create(Activity.class);

		storeProvenance.getProvStartedAtTime().add(
				datatypeFactory.newXMLGregorianCalendar(startedProvExportAt));
		storeProvenance.getProvWasAssociatedWith().add(tavernaAgent);
		// The agent is an execution of the Taverna software (e.g. also an
		// Activity)
		String versionName = ApplicationConfig.getInstance().getName();

		// Qualify it to add the plan
		Association association = elmoManager.create(Association.class);
		association.getProvAgent().add(tavernaAgent);
		storeProvenance.getProvQualifiedAssociation().add(association);
		association.getProvHadPlan().add(
				elmoManager.create(
						new QName("http://ns.taverna.org.uk/2011/software/",
								versionName), Plan.class));

		bundle.getProvWasGeneratedBy().add(storeProvenance);
		// The store-provenance-process used the workflow run as input
		storeProvenance.getProvWasInformedBy().add(
				elmoManager.create(new QName(runURI), Activity.class));

		DataflowInvocation dataflowInvocation = provenanceAccess
				.getDataflowInvocation(getWorkflowRunId());
		// String dataflowURI =
		// uriGenerator.makeDataflowInvocationURI(workflowRunId,
		// dataflowInvocation.getDataflowInvocationId());
		Activity wfProcess = elmoManager.create(new QName(runURI),
				Activity.class);
		wfProcess.getProvWasAssociatedWith().add(tavernaAgent);
		association = elmoManager.create(Association.class);
		association.getProvAgent().add(tavernaAgent);
		wfProcess.getProvQualifiedAssociation().add(association);

		String wfUri = uriGenerator.makeWorkflowURI(dataflowInvocation
				.getWorkflowId());
		// TODO: Also make the recipe a Scufl2 Workflow?
		Plan plan = elmoManager.create(new QName(wfUri), Plan.class);
		association.getProvHadPlan().add(plan);

		wfProcess.getProvStartedAtTime().add(
				timestampToXmlGreg(dataflowInvocation.getInvocationStarted()));
		wfProcess.getProvEndedAtTime().add(
				timestampToXmlGreg(dataflowInvocation.getInvocationEnded()));

		

		// Workflow inputs and outputs
		storeEntitities(dataflowInvocation.getInputsDataBindingId(), wfProcess,
				Direction.INPUTS, elmoManager, 
						getIntermediatesDirectory());
		// FIXME: These entities come out as "generated" by multiple processes
		storeEntitities(dataflowInvocation.getOutputsDataBindingId(),
				wfProcess, Direction.OUTPUTS, elmoManager, 
						getIntermediatesDirectory());
		// elmoManager.persist(wfProcess);
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
			Activity process = elmoManager.create(new QName(processURI),
					Activity.class);
			Activity parentProcess = elmoManager.designate(
					new QName(parentURI), Activity.class);
			process.getProvWasInformedBy().add(parentProcess);
			process.getProvStartedAtTime().add(
					timestampToXmlGreg(pe.getEnactmentStarted()));
			process.getProvEndedAtTime().add(
					timestampToXmlGreg(pe.getEnactmentEnded()));

			// TODO: Linking to the processor in the workflow definition?
			ProvenanceProcessor provenanceProcessor = provenanceAccess
					.getProvenanceProcessor(pe.getProcessorId());
			String processorURI = uriGenerator.makeProcessorURI(
					provenanceProcessor.getProcessorName(),
					provenanceProcessor.getWorkflowId());
			// TODO: Also make the plan a Scufl2 Processor

			process.getProvQualifiedAssociation().add(association);
			association = elmoManager.create(Association.class);
			association.getProvAgent().add(tavernaAgent);
			plan = elmoManager.create(new QName(processorURI), Plan.class);
			association.getProvHadPlan().add(plan);

			// TODO: How to link together iterations on a single processor and
			// the collections
			// they are iterating over and creating?
			// Need 'virtual' ProcessExecution for iteration?

			// TODO: Activity/service details from definition?

			File path = getIntermediatesDirectory();

			// Inputs and outputs
			storeEntitities(pe.getInitialInputsDataBindingId(), process,
					Direction.INPUTS, elmoManager, path);
			storeEntitities(pe.getFinalOutputsDataBindingId(), process,
					Direction.OUTPUTS, elmoManager, path);

			// elmoManager.persist(process);
		}

		storeFileReferences(elmoManager);
		
		GregorianCalendar endedProvExportAt = new GregorianCalendar();
		storeProvenance.getProvEndedAtTime().add(
				datatypeFactory.newXMLGregorianCalendar(endedProvExportAt));

		// Save the whole thing
		ContextAwareConnection connection = elmoManager.getConnection();
		connection.setNamespace("scufl2",
				"http://ns.taverna.org.uk/2010/scufl2#");
		connection.setNamespace("prov", "http://www.w3.org/ns/prov#");
		connection.setNamespace("owl", "http://www.w3.org/2002/07/owl#");
		// connection.export(new OrganizedRDFWriter(new
		// RDFXMLPrettyWriter(outStream)));
		// connection.export(new OrganizedRDFWriter(new
		// RDFXMLPrettyWriter(outStream)));
		connection.export(new OrganizedRDFWriter(new TurtleWriterWithBase(
				outStream, getBaseFolder().toURI())));

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

	protected void storeFileReferences(SesameManager elmoManager) {

		for (Entry<File, T2Reference> entry : getFileToT2Reference().entrySet()) {
			File file = entry.getKey();
			T2Reference t2Ref = entry.getValue();
			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());
			// try {
			Entity entity = elmoManager
					.create(new QName(dataURI), Entity.class);
			entity.getProvAlternateOf().add(
					elmoManager.create(new QName(file.toURI().toASCIIString()),
							Entity.class));
			// elmoManager.getConnection().add(new URIImpl(dataURI), SAMEAS,
			// new URIImpl(file.toURI().toASCIIString()));
			// } catch (RepositoryException e) {
			// // FIXME: Fail properly
			// throw new RuntimeException("Can't store reference for " + file,
			// e);
			// }
		}
	}

	protected void storeEntitities(String dataBindingId, Activity activity,
			Direction direction, SesameManager elmoManager, File path) throws IOException {

		Map<Port, T2Reference> bindings = provenanceAccess
				.getDataBindings(dataBindingId);

		for (Entry<Port, T2Reference> binding : bindings.entrySet()) {
			Port port = binding.getKey();
			T2Reference t2Ref = binding.getValue();

			String dataURI = uriGenerator.makeT2ReferenceURI(t2Ref.toUri()
					.toASCIIString());

			Entity entity = elmoManager
					.create(new QName(dataURI), Entity.class);

			if (! seenReference(t2Ref)) {
				saveReference(t2Ref);
			}
				
			
			String id = t2Ref.getLocalPart();
			String prefix = id.substring(0, 2);
			
			File prefixDir = new File(path, prefix);

			if (direction == Direction.INPUTS) {
				activity.getProvUsed().add(entity);
			} else {
				if (!entity.getProvWasGeneratedBy().isEmpty()) {
					// Double-output, alias the entity with a fresh one
					// to avoid double-generation
					Entity viewOfEntity = elmoManager.create(Entity.class);
					viewOfEntity.getProvAlternateOf().add(entity);
					entity = viewOfEntity;
				}
				entity.getProvWasGeneratedBy().add(activity);
			}

			AssociationOrEndOrGenerationOrInvalidationOrStartOrUsage involvement;
			if (direction == Direction.INPUTS) {
				Usage usage = elmoManager.create(Usage.class);
				involvement = usage;
				activity.getProvQualifiedUsage().add(usage);
				usage.getProvEntity().add(entity);
			} else {
				Generation generation = elmoManager.create(Generation.class);
				involvement = generation;
				entity.getProvQualifiedGeneration().add(generation);
				generation.getProvActivity().add(activity);
			}

			String processerName = null;
			if (port.getProcessorId() != null) {
				// Not a workflow port
				ProvenanceProcessor p = provenanceAccess
						.getProvenanceProcessor(port.getProcessorId());
				processerName = p.getProcessorName();
			}
			port.getProcessorId();
			String portURI = uriGenerator.makePortURI(port.getWorkflowId(),
					processerName, port.getPortName(), port.isInputPort());
			Role portRole = elmoManager.create(new QName(portURI), Role.class);
			involvement.getProvHadRole().add(portRole);

			// elmoManager.persist(entity);
		}

	}

	
	private boolean seenReference(T2Reference t2Ref) {
		return seenReferences.containsKey(t2Ref);
	}

	private File saveReference(T2Reference t2Ref) throws IOException {
		// Avoid double-saving
		File f = seenReferences.get(t2Ref);
		if (f != null) {
			return f;
		}
		
		String extension = "";
		if (t2Ref.getReferenceType() == T2ReferenceType.IdentifiedList) {
			extension = ".list";
		} else if (t2Ref.getReferenceType() == T2ReferenceType.ErrorDocument) {
			extension = ".err";
		}
		
		File file = referencePath(t2Ref);
		File parent = file.getParentFile();
		parent.mkdirs();
		if (t2Ref.getReferenceType() == T2ReferenceType.IdentifiedList) {
			// Write a kind of text/uri-list (but with relative URIs)			
			IdentifiedList<T2Reference> list = saver.getReferenceService().getListService().getList(t2Ref);
			FileWriterWithEncoding writer = new FileWriterWithEncoding(file, "utf-8");			
			for (T2Reference ref : list) {
				File refFile = saveReference(ref);
				URI relRef = parent.toURI().relativize(refFile.toURI());
				writer.append(relRef.toASCIIString() + "\n");	
			}
			
		} else {
			// Capture filename with extension
			file = saver.writeDataObject(parent, file.getName(), t2Ref, extension);
			// FIXME: The above will save the same reference every time!
		}
		seenReference(t2Ref, file);		
		return file;		
	}

	private File referencePath(T2Reference t2Ref) {				
		String local = t2Ref.getLocalPart();
		try {
			local = UUID.fromString(local).toString();
		} catch (IllegalArgumentException ex) {
			// Fallback - use full namespace/localpart
			return new File(new File(getIntermediatesDirectory(), t2Ref.getNamespacePart()), t2Ref.getLocalPart());
		}
		return new File(new File(getIntermediatesDirectory(), local.substring(0, 2)), local);
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
