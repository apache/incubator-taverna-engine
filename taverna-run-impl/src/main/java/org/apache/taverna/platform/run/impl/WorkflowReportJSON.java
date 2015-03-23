/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.platform.run.impl;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_EMPTY_JSON_ARRAYS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.apache.taverna.databundle.DataBundles.getWorkflow;
import static org.apache.taverna.databundle.DataBundles.getWorkflowBundle;
import static org.apache.taverna.databundle.DataBundles.getWorkflowRunReport;
import static org.apache.taverna.databundle.DataBundles.setWorkflowBundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.Manifest.PathMixin;

import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.Invocation;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.StatusReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class WorkflowReportJSON {
	private static URITools uriTools = new URITools();
	private static final StdDateFormat STD_DATE_FORMAT = new StdDateFormat();

	public void save(WorkflowReport wfReport, Path path) throws IOException {
		ObjectMapper om = makeObjectMapperForSave();
		try (Writer w = newBufferedWriter(path, Charset.forName("UTF-8"),
				WRITE, CREATE, TRUNCATE_EXISTING)) {
			om.writeValue(w, wfReport);
		}
	}

	protected static ObjectMapper makeObjectMapperForLoad() {
		ObjectMapper om = new ObjectMapper();
		om.disable(FAIL_ON_UNKNOWN_PROPERTIES);
		return om;
	}

	protected static ObjectMapper makeObjectMapperForSave() {
		ObjectMapper om = new ObjectMapper();
		om.enable(INDENT_OUTPUT);
		om.disable(FAIL_ON_EMPTY_BEANS);
		om.enable(ORDER_MAP_ENTRIES_BY_KEYS);
		om.disable(WRITE_EMPTY_JSON_ARRAYS);
		om.disable(WRITE_NULL_MAP_VALUES);
		om.disable(WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
		om.disable(WRITE_DATES_AS_TIMESTAMPS);
		om.disable(WRITE_NULL_MAP_VALUES);
		om.addMixInAnnotations(Path.class, PathMixin.class);
		om.setSerializationInclusion(NON_NULL);
		return om;
	}

	@SuppressWarnings("unused")
	private void injectContext(ObjectNode objNode) {
		ObjectNode context = objNode.with("@context");
		context.put("wfprov", "http://purl.org/wf4ever/wfprov#");
		context.put("wfdesc", "http://purl.org/wf4ever/wfdesc#");
		context.put("prov", "http://www.w3.org/ns/prov#");
	}

	public void save(WorkflowReport wfReport, Bundle dataBundle)
			throws IOException {
		Path path = getWorkflowRunReport(dataBundle);
		save(wfReport, path);
		if (!Files.exists(getWorkflow(dataBundle)))
			// Usually already done by Run constructor
			setWorkflowBundle(wfReport.getDataBundle(), wfReport.getSubject()
					.getParent());
	}

	public WorkflowReport load(Bundle bundle) throws IOException,
			ReaderException, ParseException {
		Path path = getWorkflowRunReport(bundle);
		WorkflowBundle workflow = getWorkflowBundle(bundle);
		return load(path, workflow);
	}

	public WorkflowReport load(Path workflowReportJson,
			WorkflowBundle workflowBundle) throws IOException, ParseException {
		JsonNode json = loadWorkflowReportJson(workflowReportJson);
		if (!json.isObject())
			throw new IOException(
					"Invalid workflow report, expected JSON Object:\n" + json);
		return parseWorkflowReport(json, workflowReportJson, null,
				workflowBundle);
	}

	protected WorkflowReport parseWorkflowReport(JsonNode reportJson,
			Path workflowReportJson, ActivityReport actReport,
			WorkflowBundle workflowBundle) throws ParseException {
		Workflow wf = (Workflow) getSubject(reportJson, workflowBundle);
		WorkflowReport workflowReport = new WorkflowReport(wf);
		workflowReport.setParentReport(actReport);

		parseDates(reportJson, workflowReport);

		for (JsonNode invocJson : reportJson.path("invocations"))
			// NOTE: Invocation constructor will add to parents
			parseInvocation(invocJson, workflowReportJson, workflowReport);

		for (JsonNode procJson : reportJson.path("processorReports")) {
			ProcessorReport procReport = parseProcessorReport(procJson,
					workflowReportJson, workflowReport, workflowBundle);
			workflowReport.addProcessorReport(procReport);
		}
		return workflowReport;
	}

	protected ProcessorReport parseProcessorReport(JsonNode reportJson,
			Path workflowReportJson, WorkflowReport workflowReport,
			WorkflowBundle workflowBundle) throws ParseException {
		Processor p = (Processor) getSubject(reportJson, workflowBundle);
		ProcessorReport procReport = new ProcessorReport(p);
		procReport.setParentReport(workflowReport);

		procReport.setJobsQueued(reportJson.path("jobsQueued").asInt());
		procReport.setJobsStarted(reportJson.path("jobsStarted").asInt());
		procReport.setJobsCompleted(reportJson.path("jobsCompleted").asInt());
		procReport.setJobsCompletedWithErrors(reportJson.path(
				"jobsCompletedWithErrors").asInt());
		// TODO: procReport properties

		parseDates(reportJson, procReport);

		for (JsonNode invocJson : reportJson.path("invocations"))
			parseInvocation(invocJson, workflowReportJson, procReport);

		for (JsonNode actJson : reportJson.path("activityReports")) {
			ActivityReport activityReport = parseActivityReport(actJson,
					workflowReportJson, procReport, workflowBundle);
			procReport.addActivityReport(activityReport);
		}
		return procReport;
	}

	protected ActivityReport parseActivityReport(JsonNode actJson,
			Path workflowReportJson, ProcessorReport procReport,
			WorkflowBundle workflowBundle) throws ParseException {
		Activity a = (Activity) getSubject(actJson, workflowBundle);
		ActivityReport actReport = new ActivityReport(a);
		actReport.setParentReport(procReport);

		parseDates(actJson, actReport);

		for (JsonNode invocJson : actJson.path("invocations"))
			parseInvocation(invocJson, workflowReportJson, actReport);

		JsonNode nestedWf = actJson.get("nestedWorkflowReport");
		if (nestedWf != null)
			actReport.setNestedWorkflowReport(parseWorkflowReport(nestedWf,
					workflowReportJson, actReport, workflowBundle));
		return actReport;
	}

	protected void parseInvocation(JsonNode json, Path workflowReportJson,
			@SuppressWarnings("rawtypes") StatusReport report)
			throws ParseException {
		String name = json.path("name").asText();

		String parentId = json.path("parent").asText();
		Invocation parent = null;
		if (!parentId.isEmpty()) {
			@SuppressWarnings("rawtypes")
			StatusReport parentReport = report.getParentReport();
			if (parentReport != null)
				parent = parentReport.getInvocation(parentId);
		}

		int[] index;
		if (json.has("index")) {
			ArrayNode array = (ArrayNode) json.get("index");
			index = new int[array.size()];
			for (int i = 0; i < index.length; i++)
				index[i] = array.get(i).asInt();
		} else
			index = new int[0];

		Invocation invocation = new Invocation(name, index, parent, report);
		Date startedDate = getDate(json, "startedDate");
		if (startedDate != null)
			invocation.setStartedDate(startedDate);
		Date completedDate = getDate(json, "completedDate");
		if (completedDate != null)
			invocation.setCompletedDate(completedDate);

		invocation.setInputs(parseValues(json.path("inputs"),
				workflowReportJson));
		invocation.setOutputs(parseValues(json.path("outputs"),
				workflowReportJson));
	}

	protected Map<String, Path> parseValues(JsonNode json, Path basePath) {
		SortedMap<String, Path> values = new TreeMap<>();
		for (String port : iterate(json.fieldNames())) {
			String pathStr = json.get(port).asText();
			Path value = basePath.resolve(pathStr);
			values.put(port, value);
		}
		return values;
	}

	private static <T> Iterable<T> iterate(final Iterator<T> iterator) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}

	protected void parseDates(JsonNode json,
			@SuppressWarnings("rawtypes") StatusReport report)
			throws ParseException {
		Date createdDate = getDate(json, "createdDate");
		if (createdDate != null)
			report.setCreatedDate(createdDate);

		Date startedDate = getDate(json, "startedDate");
		if (startedDate != null)
			report.setStartedDate(startedDate);

		// Special case for paused and resumed dates>
		for (JsonNode s : json.path("pausedDates")) {
			Date pausedDate = STD_DATE_FORMAT.parse(s.asText());
			report.setPausedDate(pausedDate);
		}
		Date pausedDate = getDate(json, "pausedDate");
		if (report.getPausedDates().isEmpty() && pausedDate != null) {
			/*
			 * "pausedDate" is normally redundant (last value of "pausedDates")
			 * but here for some reason the list is missing, so we'll parse it
			 * separately.
			 * 
			 * Note that if there was a list, we will ignore "pauseDate" no
			 * matter its value
			 */
			report.setPausedDate(pausedDate);
		}

		for (JsonNode s : json.path("resumedDates")) {
			Date resumedDate = STD_DATE_FORMAT.parse(s.asText());
			report.setResumedDate(resumedDate);
		}
		Date resumedDate = getDate(json, "resumedDate");
		if (report.getResumedDates().isEmpty() && resumedDate != null)
			// Same fall-back as for "pausedDate" above
			report.setResumedDate(resumedDate);

		Date cancelledDate = getDate(json, "cancelledDate");
		if (cancelledDate != null)
			report.setCancelledDate(cancelledDate);

		Date failedDate = getDate(json, "failedDate");
		if (failedDate != null)
			report.setFailedDate(failedDate);

		Date completedDate = getDate(json, "completedDate");
		if (completedDate != null)
			report.setCompletedDate(completedDate);

		try {
			State state = State.valueOf(json.get("state").asText());
			report.setState(state);
		} catch (IllegalArgumentException ex) {
			throw new ParseException("Invalid state: " + json.get("state"), -1);
		}
	}

	protected Date getDate(JsonNode json, String name) throws ParseException {
		String date = json.path(name).asText();
		if (date.isEmpty())
			return null;
		return STD_DATE_FORMAT.parse(date);
	}

	private WorkflowBean getSubject(JsonNode reportJson,
			WorkflowBundle workflowBundle) {
		URI subjectUri = URI.create(reportJson.path("subject").asText());
		return uriTools.resolveUri(subjectUri, workflowBundle);
	}

	protected JsonNode loadWorkflowReportJson(Path path) throws IOException,
			JsonProcessingException {
		ObjectMapper om = makeObjectMapperForLoad();
		try (InputStream stream = Files.newInputStream(path)) {
			return om.readTree(stream);
		}
	}
}
