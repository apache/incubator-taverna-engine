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
package org.apache.taverna.security.credentialmanager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * Methods for parsing Distinguished Names and various associated utility methods.
 * 
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author Christian Brenninkmeijer
 */
public interface DistinguishedNameParser {

    /**
     * Parses a DN string and fills in fields with DN parts. 
    */
    public ParsedDistinguishedName parseDN(String DNstr);
  
    public String getMessageDigestAsFormattedString(byte[] certBinaryEncoding, String shA1);

    /**
     * Convert the certificate object into an X509Certificate object.
     */
    public X509Certificate convertCertificate(Certificate certificate) throws CMException;

    public URI setUserInfoForURI(URI uri, String userinfo) throws URISyntaxException;

    public URI setFragmentForURI(URI uri, String userinfo) throws URISyntaxException;

    /**
     * Get the configuration directory where the security stuff will be/is saved
     * to.
     */
    public File getCredentialManagerDefaultDirectory(ApplicationConfiguration applicationConfiguration);

 } 
