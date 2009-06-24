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

/**
 * Thrown by methods in the ListService interface if anything goes wrong with
 * list registration or retrieval. Any underlying exceptions that can't be
 * handled in the service layer are wrapped in this and re-thrown.
 * 
 * @author Tom Oinn
 */
public class ListServiceException extends RuntimeException {

	private static final long serialVersionUID = 5049346991071587866L;

	public ListServiceException() {
		super();
	}

	public ListServiceException(String message) {
		super(message);
	}

	public ListServiceException(Throwable cause) {
		super(cause);
	}

	public ListServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
