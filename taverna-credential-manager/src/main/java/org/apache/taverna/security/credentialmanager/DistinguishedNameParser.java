/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.security.credentialmanager;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import org.apache.taverna.configuration.app.ApplicationConfiguration;

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
    public Path getCredentialManagerDefaultDirectory(ApplicationConfiguration applicationConfiguration);

 } 
