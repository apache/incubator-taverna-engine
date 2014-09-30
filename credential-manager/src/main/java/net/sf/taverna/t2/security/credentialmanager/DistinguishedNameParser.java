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

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Utility methods for Credential Manager and security-related stuff.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author Christian Brenninkmeijer
 */
public interface DistinguishedNameParser {

	/**
	 * Parses a DN string and fills in fields with DN parts. Heavily based on
	 * uk.ac.omii.security.utils.DNParser class from omii-security-utils
	 * library.
	 * 
	 * http://maven.omii.ac.uk/maven2/repository/omii/omii-security-utils/
	 */
	public ParsedDistinguishedName parseDN(String DNstr);

        public String getMessageDigestAsFormattedString(byte[] certBinaryEncoding, String shA1);

	public X509Certificate convertCertificate(Certificate certificate) throws CMException;

 }
