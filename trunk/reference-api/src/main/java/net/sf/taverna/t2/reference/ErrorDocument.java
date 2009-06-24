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
package net.sf.taverna.t2.reference;

import java.util.List;
import java.util.Set;

/**
 * Contains the definition of an error token within the workflow system.
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public interface ErrorDocument extends Identified {

	/**
	 * If the error document is created from a Throwable it will have a stack
	 * trace, in this case the stack trace is represented as a list of
	 * StackTraceElement beans
	 */
	public List<StackTraceElementBean> getStackTraceStrings();

	/**
	 * If the error document is created from a Throwable this contains the
	 * message part of the Throwable
	 */
	public String getExceptionMessage();

	/**
	 * Error documents can carry an arbitrary string message, this returns it.
	 */
	public String getMessage();
	
	/**
	 * If the error document is created from set of references that contain error
	 * documents, this method returns them. 
	 */
	public Set<T2Reference> getErrorReferences();

}
