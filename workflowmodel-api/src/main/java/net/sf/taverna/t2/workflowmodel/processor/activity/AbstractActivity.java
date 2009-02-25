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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;
import net.sf.taverna.t2.annotation.annotationbeans.MimeType;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

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

	protected Map<String, String> inputPortMapping = new HashMap<String, String>();

	protected Map<String, String> outputPortMapping = new HashMap<String, String>();

	protected Set<OutputPort> outputPorts = new HashSet<OutputPort>();

	protected Set<ActivityInputPort> inputPorts = new HashSet<ActivityInputPort>();

	/**
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#configure(java.lang.Object)
	 */
	public abstract void configure(ConfigType conf)
			throws ActivityConfigurationException;

	/**
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#getConfiguration()
	 */
	public abstract ConfigType getConfiguration();

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#getInputPortMapping()
	 */
	public final Map<String, String> getInputPortMapping() {
		return this.inputPortMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#getInputPorts()
	 */
	public final Set<ActivityInputPort> getInputPorts() {
		return inputPorts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#getOutputPortMapping()
	 */
	public final Map<String, String> getOutputPortMapping() {
		return this.outputPortMapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.taverna.t2.workflowmodel.processor.activity.Activity#getOutputPorts()
	 */
	public final Set<OutputPort> getOutputPorts() {
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
		if (handledReferenceSchemes == null) {
			handledReferenceSchemes = Collections.emptyList();
		}
		inputPorts.add(EditsRegistry.getEdits().createActivityInputPort(
				portName, portDepth, allowsLiteralValues,
				handledReferenceSchemes, translatedElementClass));
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
		outputPorts.add(EditsRegistry.getEdits().createActivityOutputPort(
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
			OutputPort createActivityOutputPort = EditsRegistry.getEdits().createActivityOutputPort(
					outputDef.getName(), outputDef.getDepth(), outputDef
					.getGranularDepth());
//			addOutput(outputDef.getName(), outputDef.getDepth(), outputDef
//					.getGranularDepth());
			this.outputPorts.add(createActivityOutputPort);
			//add the mime types as annotations
			for (String mimeType:outputDef.getMimeTypes()) {
				MimeType mimeTypeAnnotation = new MimeType();
				mimeTypeAnnotation.setText(mimeType);
				try {
					EditsRegistry.getEdits().getAddAnnotationChainEdit(createActivityOutputPort, mimeTypeAnnotation).doEdit();
				} catch (EditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Remove existing output ports.
	 * 
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
