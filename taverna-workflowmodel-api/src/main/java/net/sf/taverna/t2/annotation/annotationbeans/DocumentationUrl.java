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
package net.sf.taverna.t2.annotation.annotationbeans;

import java.net.URL;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AppliesTo;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * A link to documentation for the target element contained at a particular
 * Uniform Resource Locator (URL)
 * 
 * @author Tom Oinn
 * @author Alan Williams
 */
@AppliesTo(targetObjectType = { Port.class, Activity.class, Processor.class, Dataflow.class }, many = true)
public class DocumentationUrl implements AnnotationBeanSPI {

	private URL documentationURL;

	/**
	 * Default constructor as mandated by java bean specification
	 */
	public DocumentationUrl() {
		//
	}

	public URL getDocumentationURL() {
		return documentationURL;
	}

	public void setDocumentationURL(URL documentationURL) {
		this.documentationURL = documentationURL;
	}

}
