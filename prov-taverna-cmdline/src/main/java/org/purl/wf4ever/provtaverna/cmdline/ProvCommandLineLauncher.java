package org.purl.wf4ever.provtaverna.cmdline;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.commandline.CommandLineLauncher;
import net.sf.taverna.t2.commandline.CommandLineResultListener;
import net.sf.taverna.t2.commandline.data.SaveResultsHandler;
import net.sf.taverna.t2.commandline.exceptions.DatabaseConfigurationException;
import net.sf.taverna.t2.commandline.exceptions.InvalidOptionException;
import net.sf.taverna.t2.commandline.exceptions.OpenDataflowException;
import net.sf.taverna.t2.commandline.exceptions.ReadInputException;
import net.sf.taverna.t2.commandline.options.CommandLineOptions;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.security.credentialmanager.CMException;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

public class ProvCommandLineLauncher extends CommandLineLauncher {
	
	private ThreadLocal<CommandLineOptions> options = new ThreadLocal<CommandLineOptions>();

	@Override
	public int setupAndExecute(String[] args, CommandLineOptions options)
			throws InvalidOptionException, EditException,
			DeserializationException, InvalidDataflowException,
			TokenOrderException, ReadInputException, OpenDataflowException,
			DatabaseConfigurationException, CMException {
		// Steal the options for later use by executeWorkflow() 
		this.options.set(options);
		try {
			return super.setupAndExecute(args, options);
		} finally {
			this.options.remove();
		}
	}
	
	@Override
	protected void executeWorkflow(WorkflowInstanceFacade facade,
			Map<String, WorkflowDataToken> inputs,
			CommandLineResultListener resultListener)
			throws TokenOrderException {		

		
		// Rather than trying to modify the options, we'll just say that
		// some options are not supported
		String outputDirectory = options.get().getOutputDirectory();
		if (outputDirectory == null) {
			error("Option -outputdir mandatory");			
		}
		if (options.get().getOutputDocument() != null) {
			error("Option -outputdoc not supported");			
		}
		if (options.get().getJanus() != null) {
			error("Option -janus not supported");			
		}
		if (options.get().getOPM() != null) {
			error("Option -opm not supported");			
		}


		/* Modified from should-not-have-been-private method
		 * CommandLineLauncher.addResultListener()
		 */
		Dataflow dataflow = facade.getDataflow();
		// Replace the facade with a fresh one (to remove the old listener)
		try {
			facade = compileFacade(dataflow, facade.getContext());
		} catch (InvalidDataflowException e) {
			error("There was an error validating the workflow: "
					+ e.getMessage());
		}

		File outputDir = new File(outputDirectory);
		
		Map<String, Integer> outputPortNamesAndDepth = new HashMap<String, Integer>();
		for (DataflowOutputPort port : dataflow.getOutputPorts()) {
			outputPortNamesAndDepth.put(port.getName(), port.getDepth());
		}
		SaveResultsHandler resultsHandler = new SaveResultsHandler(
				outputPortNamesAndDepth, outputDir, null, null, null);
		
		// For PROV export we'll require outputDir, won't support 
		// baclava, and don't export Janus or OPM
		CommandLineResultListener myResultListener = new CommandLineResultListener(
				outputPortNamesAndDepth.size(), resultsHandler,
				true, false, false, false, facade.getWorkflowRunId()) {
			@Override
			public void saveProvenance() {
				System.out.println("Here we will save provenance!");
			}
		};
		
		
		facade.addResultListener(myResultListener);		
		super.executeWorkflow(facade, inputs, myResultListener);
	}
}
