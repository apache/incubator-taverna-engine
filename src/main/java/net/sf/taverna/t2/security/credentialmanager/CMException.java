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
package net.sf.taverna.t2.security.credentialmanager;

/**
 * Represents a (cryptographic or any other) exception thrown by Credential Manager.
 * 
 * @author Alexandra Nenadic
 */
public class CMException
    extends Exception
{
	
	private static final long serialVersionUID = 3885885604048806903L;

	/**
     * Creates a new CMException.
     */
    public CMException()
    {
        super();
    }

    /**
     * Creates a new CMException with the specified message.
     *
     * @param sMessage Exception message
     */
    public CMException(String sMessage)
    {
        super(sMessage);
    }

    /**
     * Creates a new CMException with the specified message and cause
     * throwable.
     *
     * @param causeThrowable The throwable that caused this exception to be
     * thrown
     * @param sMessage Exception message
     */
    public CMException(String sMessage, Throwable causeThrowable)
    {
        super(sMessage, causeThrowable);
    }

    /**
     * Creates a new CMException with the specified cause throwable.
     *
     * @param causeThrowable The throwable that caused this exception to be
     * thrown
     */
    public CMException(Throwable causeThrowable)
    {
        super(causeThrowable);
    }
}
