package net.sf.taverna.t2.provenance.lineageservice;

import java.net.URI;
import java.net.URISyntaxException;

public class URIGenerator {

	public String makeCollectionURI(String collId) {

		// collId is of the form t2:list//<UUID>
		// map to a proper URI

		String[] tokens = collId.split("//");
		return makeURI(tokens[1]);
	}

	public String makeWFInstanceURI(String workflowRunId) {
		return "http://ns.taverna.org.uk/2011/run/" + workflowRunId + "/";
	}

	public String makeWorkflowURI(String wfId) {
		return "http://ns.taverna.org.uk/2010/scufl2/workflow/" + wfId + "/";
	}

	public String makePortURI(String wfId, String pName, String vName,
			boolean inputPort) {
		return makeProcessorURI(pName, wfId) + (inputPort ? "in/" : "out/")
				+ vName;
	}

	public String makeProcessorURI(String pName, String wfId) {
		return makeWorkflowURI(wfId) + "processor/" + pName + "/";
	}

	public String makeURI(String s) {

		URI u;
		try {
			u = new URI("http://ns.taverna.org.uk/2011/provenance/" + s);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return u.toASCIIString();
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
