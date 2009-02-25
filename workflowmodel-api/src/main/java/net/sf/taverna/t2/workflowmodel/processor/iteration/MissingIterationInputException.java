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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

/**
 * Thrown when an attempt is made to evaluate the type of the iteration strategy
 * but one or more input ports aren't defined in the input array of types.
 * Shouldn't normally happen as this will be handled by the type checker
 * detecting that there aren't enough inputs to check but we indicate it for
 * extra robustness.
 * 
 * @author Tom Oinn
 * 
 */
public class MissingIterationInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1615949178096496592L;

	public MissingIterationInputException() {
		// TODO Auto-generated constructor stub
	}

	public MissingIterationInputException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public MissingIterationInputException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public MissingIterationInputException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
