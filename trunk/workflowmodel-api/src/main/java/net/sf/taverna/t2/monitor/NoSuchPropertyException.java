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
package net.sf.taverna.t2.monitor;

/**
 * Thrown when an attempt is made to access a monitorable property which is no
 * longer current. This is quite a common event as the properties can change
 * extremely quickly whereas the logic accessing them is expected not to.
 * Consumers of state data must cope with this disparity by handling this
 * exception where it is thrown.
 * 
 * @author Tom Oinn
 * 
 */
public class NoSuchPropertyException extends Exception {

	private static final long serialVersionUID = 6320919057517500603L;

	public NoSuchPropertyException() {
		super();
	}

	public NoSuchPropertyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoSuchPropertyException(String arg0) {
		super(arg0);
	}

	public NoSuchPropertyException(Throwable arg0) {
		super(arg0);
	}

}
