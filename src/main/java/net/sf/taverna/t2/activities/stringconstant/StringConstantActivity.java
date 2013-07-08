/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.stringconstant;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.annotation.annotationbeans.MimeType;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An Activity that holds a constant string value.
 * <p>
 * It is automatically configured to have no input ports and only one output port named
 * <em>value</em>.
 *
 * @author Stuart Owen
 * @author David Withers
 */
public class StringConstantActivity extends AbstractAsynchronousActivity<JsonNode> {

	public static final String URI = "http://ns.taverna.org.uk/2010/activity/constant";

	private static final Logger logger = Logger.getLogger(StringConstantActivity.class);

	private String value;

	private JsonNode json;

	@Override
	public void configure(JsonNode json) throws ActivityConfigurationException {
		this.json = json;
		this.value = json.get("string").asText();
//		if (outputPorts.size() == 0) {
//			addOutput("value", 0, "text/plain");
//		}
	}

	public String getStringValue() {
		return json.get("string").asText();
	}

	@Override
	public JsonNode getConfiguration() {
		return json;
	}

	@Override
	public void executeAsynch(final Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {

			public void run() {
				ReferenceService referenceService = callback.getContext().getReferenceService();
				try {
					T2Reference id = referenceService.register(value, 0, true,
							callback.getContext());
					Map<String, T2Reference> outputData = new HashMap<String, T2Reference>();
					outputData.put("value", id);
					callback.receiveResult(outputData, new int[0]);
				} catch (ReferenceServiceException e) {
					callback.fail(e.getMessage(), e);
				}
			}

		});

	}

//	protected void addOutput(String portName, int portDepth, String type) {
//		ActivityOutputPort port = edits.createActivityOutputPort(portName, portDepth, portDepth);
//		MimeType mimeType = new MimeType();
//		mimeType.setText(type);
//		try {
//			edits.getAddAnnotationChainEdit(port, mimeType).doEdit();
//		} catch (EditException e) {
//			logger.debug("Error adding MimeType annotation to port", e);
//		}
//		outputPorts.add(port);
//	}

	public String getExtraDescription() {
		if (value.length() > 60) {
			return value.substring(0, 60 - 3) + "...";
		}
		return value;
	}

}
