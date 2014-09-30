/*******************************************************************************
 * Copyright (C) 2014 The University of Manchester
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
 * A parsed Distinguished Name with getters for parts.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author Christian Brenninkmeijer
 */
public interface ParsedDistinguishedName {
    
        /**
         * 
         * @return The common name
         */
	public String getCN();

        /**
         * 
         * @return The Email address
         */
	public String getEmailAddress();

        /**
         * 
         * @return The organizational unit name
         */
	public String getOU();
        
        /**
         * 
         * @return The organization name
         */
	public String getO();

        /**
         * 
         * @return The locality name 
         */
	public String getL();

        /**
         * 
         * @return The state or province name
         */
	public String getST();

        /**
         * 
         * @return The country name 
         */
	public String getC();
}
