package org.purl.wf4ever.provtaverna.cmdline;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.taverna.raven.launcher.Launchable;
import net.sf.taverna.t2.commandline.CommandLineLauncher;
import net.sf.taverna.t2.commandline.CommandLineResultListener;
import net.sf.taverna.t2.commandline.data.SaveResultsHandler;
import net.sf.taverna.t2.commandline.exceptions.DatabaseConfigurationException;
import net.sf.taverna.t2.commandline.exceptions.InvalidOptionException;
import net.sf.taverna.t2.commandline.exceptions.OpenDataflowException;
import net.sf.taverna.t2.commandline.exceptions.ReadInputException;
import net.sf.taverna.t2.commandline.options.CommandLineOptions;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.purl.wf4ever.provtaverna.export.Saver;

/**
 * An extension of CommandLineLauncher to store PROV-O provenance.
 * <p>
 * To export provenance, run with parameters:
 * 
 * <pre>
 * 		executeworkflow -Draven.launcher.app.main=org.purl.wf4ever.provtaverna.cmdline.ProvCommandLineLauncher \ 
 *           -embedded -provenance -outputdir X ...
 * </pre>
 * <p>
 * Note: Various tricks are needed as CommandLineLauncher was not written for
 * extensibility.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class ProvCommandLineLauncher extends CommandLineLauncher implements
		Launchable {
	
	private CommandLineOptions options = null;

	@Override
	public int setupAndExecute(String[] args, CommandLineOptions options)
			throws InvalidOptionException, EditException,
			DeserializationException, InvalidDataflowException,
			TokenOrderException, ReadInputException, OpenDataflowException,
			DatabaseConfigurationException, CMException {
		// Steal the options for later use by executeWorkflow() 
		this.options = options;		
		return super.setupAndExecute(args, options);
		
	}
	
	@Override
	protected void executeWorkflow(final WorkflowInstanceFacade facade,
			Map<String, WorkflowDataToken> inputs,
			CommandLineResultListener resultListener)
			throws TokenOrderException {		
		
		// Rather than trying to modify the options, we'll just say that
		// some options are not supported
		String outputDirectory = options.getOutputDirectory();
		if (outputDirectory == null) {
			error("Option -outputdir mandatory");			
		}
		if (options.getOutputDocument() != null) {
			error("Option -outputdoc not supported");			
		}
		if (options.getJanus() != null) {
			error("Option -janus not supported");			
		}
		if (options.getOPM() != null) {
			error("Option -opm not supported");			
		}


		/* Modified from should-not-have-been-private method
		 * CommandLineLauncher.addResultListener()
		 */
		Dataflow dataflow = facade.getDataflow();
		final WorkflowInstanceFacade realFacade;
		// Replace the facade with a fresh one (to remove the old listener)
		try {
			realFacade = compileFacade(dataflow, facade.getContext());
		} catch (InvalidDataflowException e) {
			error("There was an error validating the workflow: "
					+ e.getMessage());
			return;
		}

		final File outputDir = new File(outputDirectory);
		
		Map<String, Integer> outputPortNamesAndDepth = new HashMap<String, Integer>();
		for (DataflowOutputPort port : dataflow.getOutputPorts()) {
			outputPortNamesAndDepth.put(port.getName(), port.getDepth());
		}
		
		// Capture which file got which reference
		final Map<File, T2Reference> fileToId = new HashMap<File, T2Reference>();		
		SaveResultsHandler resultsHandler = new SaveResultsHandler(
				outputPortNamesAndDepth, outputDir, null, null, null) {
			@Override
			protected void saveIndividualDataFile(
					T2Reference reference, File dataFile,
					InvocationContext context) {				
				super.saveIndividualDataFile(reference, dataFile, context);
				fileToId.put(dataFile, reference);
			}
		};
		
		// For PROV export we'll require outputDir, won't support 
		// baclava, and don't export Janus or OPM
		CommandLineResultListener myResultListener = new CommandLineResultListener(
				outputPortNamesAndDepth.size(), resultsHandler,
				true, false, false, false, realFacade.getWorkflowRunId()) {
			@Override
			public void saveProvenance() {
				if (options.isProvenanceEnabled()) {
					Map<String, T2Reference> chosenReferences = new HashMap<String, T2Reference>();
					for (Entry<String, WorkflowDataToken> entry :  getOutputMap().entrySet()) {
						chosenReferences.put(entry.getKey(), entry.getValue().getData());
					}
					Saver saver = new Saver(realFacade.getContext().getReferenceService(), 
							realFacade.getContext(), realFacade.getWorkflowRunId(),
							chosenReferences);
					try {
						saver.saveData(outputDir);
					} catch (IOException e1) {
						System.err.println("Can't store output to " + outputDir);
					}
				}
			}
		};
		
		
		realFacade.addResultListener(myResultListener);		
		super.executeWorkflow(realFacade, inputs, myResultListener);
	}
}
