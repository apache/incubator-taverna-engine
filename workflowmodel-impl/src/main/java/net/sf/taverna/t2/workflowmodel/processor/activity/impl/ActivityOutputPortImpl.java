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

import java.util.Set;

import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.workflowmodel.AbstractOutputPort;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

/**
 * An output port on an Activity instance, used as a bean to hold port name,
 * depth and granular depth properties.
 * 
 * @author Tom Oinn
 * 
 */
public class ActivityOutputPortImpl extends AbstractOutputPort implements ActivityOutputPort {

	/**
	 * Constructs an Activity output port instance with the provided name,depth
	 * and granular depth.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 */
	public ActivityOutputPortImpl(String portName, int portDepth,
			int granularDepth) {
		super(portName, portDepth, granularDepth);
	}

	/**
	 * Constructs an Activity input port with the provided name, depth and
	 * granularDepth together with a list of predetermined annotations.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 * @param annotations
	 */
	public ActivityOutputPortImpl(String portName, int portDepth,
			int granularDepth, Set<AnnotationChain> annotations) {
		this(portName, portDepth, granularDepth);
		for (AnnotationChain newAnnotation : annotations) {
			try {
				getAddAnnotationEdit(newAnnotation).doEdit();
			} catch (EditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
