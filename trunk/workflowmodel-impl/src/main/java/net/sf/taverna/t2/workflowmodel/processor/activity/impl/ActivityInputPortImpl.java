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
package net.sf.taverna.t2.workflowmodel.processor.activity.impl;

import java.util.Collections;
import java.util.List;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.AbstractPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;

/**
 * An input port on an Activity instance. Simply used as a bean to hold port
 * name and depth properties.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * 
 */
public class ActivityInputPortImpl extends AbstractPort implements
		ActivityInputPort {

	private Class<?> translatedElementClass;
	private List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes;
	boolean allowsLiteralValues;

	/**
	 * Constructs an Activity input port instance with the provided name and
	 * depth.
	 * 
	 * @param portName
	 * @param portDepth
	 */
	public ActivityInputPortImpl(String portName, int portDepth) {
		super(portName, portDepth);
	}

	/**
	 * Constructs an Activity input port with the provided name and depth,
	 * together with a list of predetermined annotations.
	 * 
	 * @param portName
	 * @param portDepth
	 */
	public ActivityInputPortImpl(String portName, int portDepth,
			boolean allowsLiteralValues,
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes,
			Class<?> translatedElementClass) {
		this(portName, portDepth);
		this.allowsLiteralValues = allowsLiteralValues;
		this.handledReferenceSchemes = handledReferenceSchemes;
		this.translatedElementClass = translatedElementClass;
	}

	public boolean allowsLiteralValues() {
		return this.allowsLiteralValues();
	}

	public List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes() {
		return Collections.unmodifiableList(this.handledReferenceSchemes);
	}

	public Class<?> getTranslatedElementClass() {
		return this.translatedElementClass;
	}

}
