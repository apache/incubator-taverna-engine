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
package net.sf.taverna.t2.workflowmodel;

/**
 * Potentially thrown when an edit fails due to naming of entities created or
 * modified by the edit. This could be because there are duplicate names in e.g.
 * processor input ports or invalid characters in the name itself
 * 
 * @author Tom Oinn
 * 
 */
public class NamingException extends EditException {

	private static final long serialVersionUID = -6945542133180017313L;

	public NamingException(String message) {
		super(message);
	}

	public NamingException(Throwable cause) {
		super(cause);
	}

	public NamingException(String message, Throwable cause) {
		super(message, cause);
	}

}
