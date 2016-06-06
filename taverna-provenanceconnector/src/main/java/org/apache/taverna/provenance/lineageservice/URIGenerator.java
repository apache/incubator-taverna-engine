/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice;

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
				+ escape(vName);
	}

	public String escape(String part) {
		try {
			return new URI(null, null, part.replace("/", "%47"), null)
					.getRawPath();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Can't escape URI part " + part, e);
		}
	}

	public String makeProcessorURI(String pName, String wfId) {
		return makeWorkflowURI(wfId) + "processor/" + escape(pName) + "/";
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
