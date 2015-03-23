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

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.annotation.annotationbeans.MimeType;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import org.apache.taverna.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

import org.apache.log4j.Logger;

/**
 * Convenience abstract superclass for generic Activity instances. Parameterised
 * on the configuration type used by the Activity implementation - when this
 * object is serialised the getConfiguration method is used to store specific
 * details of the activity, this is then used immediately after a call to the
 * default constructor when deserialising from XML on a workflow load.
 * <p>
 * This class holds port sets and mappings, and returns references directly to
 * them rather than copies thereof.
 * <p>
 * If you're writing an abstract activity (one that cannot be directly invoked)
 * you should extend this class for convenience. This can be useful when you
 * wish to specify some kind of abstract definition of a process which will be
 * bound at workflow invocation time to a particular concrete activity through
 * the action of a custom dispatch stack layer (which you will also provide)
 *
 * @author Tom Oinn
 * @author Stuart Owen
 *
 * @param <ConfigType>
 *            type of configuration object to be used to hold configuration
 *            information
 */
public abstract class AbstractActivity<ConfigType> extends
		AbstractAnnotatedThing<Activity<?>> implements Activity<ConfigType> {
	private static Logger logger = Logger.getLogger(AbstractActivity.class);

	private Edits edits;

	protected Map<String, String> inputPortMapping = new HashMap<>();
	protected Map<String, String> outputPortMapping = new HashMap<>();
	protected Set<ActivityOutputPort> outputPorts = new HashSet<>();
	protected Set<ActivityInputPort> inputPorts = new HashSet<>();

	@Override
	public void setEdits(Edits edits) {
		if (edits == null)
			throw new IllegalArgumentException("Edits can not be null.");
		this.edits = edits;
	}

	/**
	 * @return the edits
	 */
	public Edits getEdits() {
		if (edits == null)
			throw new IllegalStateException(
					"Unable to run this meathod until setEdits has been called");
		return edits;
	}

	/**
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#configure(java.lang.Object)
	 */
	@Override
	public abstract void configure(ConfigType conf)
			throws ActivityConfigurationException;

	/**
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#getConfiguration()
	 */
	@Override
	public abstract ConfigType getConfiguration();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#getInputPortMapping()
	 */
	@Override
	public final Map<String, String> getInputPortMapping() {
		return this.inputPortMapping;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#getInputPorts()
	 */
	@Override
	public final Set<ActivityInputPort> getInputPorts() {
		return inputPorts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#getOutputPortMapping()
	 */
	@Override
	public final Map<String, String> getOutputPortMapping() {
		return this.outputPortMapping;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.taverna.workflowmodel.processor.activity.Activity#getOutputPorts()
	 */
	@Override
	public final Set<ActivityOutputPort> getOutputPorts() {
		return outputPorts;
	}

	/**
	 * Creates and adds a new input port with the provided properties.
	 *
	 * @see #removeInputs()
	 * @param portName -
	 *            the name of the port to be created.
	 * @param portDepth -
	 *            the depth of the port to be created.
	 */
	protected void addInput(
			String portName,
			int portDepth,
			boolean allowsLiteralValues,
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes,
			Class<?> translatedElementClass) {
		if (handledReferenceSchemes == null)
			handledReferenceSchemes = Collections.emptyList();
		inputPorts.add(getEdits().createActivityInputPort(portName, portDepth,
				allowsLiteralValues, handledReferenceSchemes,
				translatedElementClass));
	}

	/**
	 * Creates and adds a new output port with the provided properties.
	 *
	 * @see #removeOutputs()
	 * @param portName -
	 *            the name of the port to be created.
	 * @param portDepth -
	 *            the depth of the port to be created
	 * @param granularDepth -
	 *            the granular depth of the port to be created
	 * @param mimeTypes -
	 *            a List of String representations of the MIME type this port
	 *            will emit as outputs.
	 */
	protected void addOutput(String portName, int portDepth, int granularDepth) {
		outputPorts.add(getEdits().createActivityOutputPort(
				portName, portDepth, granularDepth));
	}

	/**
	 * Convenience method, creates a new output port with depth and granular
	 * depth both set to the value for depth, i.e. no streaming behaviour.
	 * <p>
	 *
	 * @see #removeOutputs()
	 * @param portName
	 * @param portDepth
	 */
	protected void addOutput(String portName, int portDepth) {
		addOutput(portName, portDepth, portDepth);
	}

	/**
	 * <p>
	 * Simplifies configuring the Activity input and output ports if its
	 * ConfigType is an implementation of {@link ActivityPortsDefinitionBean}
	 * </p>
	 * <p>
	 * For an Activity that has ports that are defined dynamically it is natural
	 * that is ConfigType will not implement this interface.
	 * </p>
	 *
	 * @param configBean
	 */
	protected void configurePorts(ActivityPortsDefinitionBean configBean) {
		removeInputs();
		for (ActivityInputPortDefinitionBean inputDef : configBean
				.getInputPortDefinitions()) {
			addInput(inputDef.getName(), inputDef.getDepth(), inputDef
					.getAllowsLiteralValues(), inputDef
					.getHandledReferenceSchemes(), inputDef
					.getTranslatedElementType());
			// TODO - use the mime types from the config bean if required,
			// probably best handled elsewhere though
		}
		removeOutputs();

		for (ActivityOutputPortDefinitionBean outputDef : configBean
				.getOutputPortDefinitions()) {
			ActivityOutputPort createActivityOutputPort = getEdits()
					.createActivityOutputPort(outputDef.getName(),
							outputDef.getDepth(), outputDef.getGranularDepth());
//			addOutput(outputDef.getName(), outputDef.getDepth(), outputDef
//					.getGranularDepth());
			outputPorts.add(createActivityOutputPort);
			// add the mime types as annotations
			for (String mimeType : outputDef.getMimeTypes())
				setMimeType(createActivityOutputPort, mimeType);
		}
	}

	private void setMimeType(ActivityOutputPort outputPort, String mimeType) {
		MimeType mimeTypeAnnotation = new MimeType();
		mimeTypeAnnotation.setText(mimeType);
		try {
			getEdits()
					.getAddAnnotationChainEdit(outputPort, mimeTypeAnnotation)
					.doEdit();
		} catch (EditException e) {
			logger.error(e);
		}
	}

	/**
	 * Remove existing output ports.
	 */
	protected void removeOutputs() {
		outputPorts.clear();
	}

	/**
	 * Remove existing input ports
	 *
	 */
	protected void removeInputs() {
		inputPorts.clear();
	}
}
