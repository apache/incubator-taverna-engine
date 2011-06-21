package net.sf.taverna.t2.provenance.lineageservice;

import java.net.URI;
import java.net.URISyntaxException;

public class URIGenerator {

	public String makeT2ReferenceURI(String collId) {

		// collId is of the form t2:list//<UUID>
		// map to a proper URI
		
		String[] tokens = collId.split("//");
		String type = tokens[0].split(":")[1];
		String namespace = tokens[1].split("/")[0].split("\\?")[0];
		String dataId = tokens[1].split("\\?")[1];
		return "http://ns.taverna.org.uk/2011/data/" + namespace + "/" + type + "/" + dataId;
	}

	public String makeWFInstanceURI(String workflowRunId) {
		return "http://ns.taverna.org.uk/2011/run/" + workflowRunId + "/";
	}

	public String makeWorkflowURI(String wfId) {
		return "http://ns.taverna.org.uk/2010/workflow/" + wfId + "/";
	}

	public String makePortURI(String wfId, String pName, String vName,
			boolean inputPort) {
		return makeProcessorURI(pName, wfId) + (inputPort ? "in/" : "out/")
				+ vName;
	}

	public String makeProcessorURI(String pName, String wfId) {
		return makeWorkflowURI(wfId) + "processor/" + pName + "/";
	}

	public String makeIteration(String workflowRunId, String workflowId,
			String processorName, String iteration) {

		String iterationUri = iteration.replace(',', '-').replace('[', ' ')
				.replace(']', ' ').trim();

		return makeWFInstanceURI(workflowRunId) + "workflow/" + workflowId
				+ "/processor/" + processorName + "/iteration/" + iterationUri;
	}

	public URI makeRunUri(String workflowRunId) {
		return URI.create(makeWFInstanceURI(workflowRunId));
	}

}
