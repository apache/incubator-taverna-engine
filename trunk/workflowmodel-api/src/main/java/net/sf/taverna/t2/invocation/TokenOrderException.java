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
package net.sf.taverna.t2.invocation;

/**
 * Thrown when tokens are supplied in an invalid order. Examples of this are
 * where duplicate indices are supplied in the same token stream or where list
 * items are emitted at a point where the individual members haven't been fully
 * populated.
 * 
 * @author Tom Oinn
 * 
 */
public class TokenOrderException extends Exception {

	public TokenOrderException() {
		super();
	}

	public TokenOrderException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TokenOrderException(String arg0) {
		super(arg0);
	}

	public TokenOrderException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7870614853928171878L;

}
