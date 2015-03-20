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

package org.apache.taverna.security.credentialmanager.impl;

import static java.net.Authenticator.RequestorType.PROXY;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.security.credentialmanager.UsernamePassword;

import org.apache.log4j.Logger;

/**
 * Credential Manager backed {@link Authenticator}.
 * <p>
 * Initialize by using: <code>
 * Authenticator.setDefault(new CredentialManagerAuthenticator());
 * </code>
 * <p>
 * Special case included for proxy authentication.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class CredentialManagerAuthenticator extends Authenticator {
	private Logger logger;
	private CredentialManager credManager;

	public CredentialManagerAuthenticator(CredentialManager credManager) {
		logger = Logger.getLogger(CredentialManagerAuthenticator.class);
		setCredentialManager(credManager);
	}

	public void setCredentialManager(CredentialManager credManager) {
		this.credManager = credManager;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		if (getRequestorType().equals(PROXY)) {
			String password = System.getProperty("http.proxyPassword");
			String username = System.getProperty("http.proxyUser");
			if (username == null || password == null)
				// No proxy authentication set
				return null;

			return new PasswordAuthentication(username, password.toCharArray());
		}

		URI uri;
		if (getRequestingURL() != null) {
			try {
				uri = getRequestingURL().toURI();
			} catch (URISyntaxException e) {
				logger.warn("Unsupported request (invalid URL) for "
						+ getRequestingURL());
				return null;
			}
		} else {
			// Construct an URI of socket://hostname:port
			String host = getRequestingHost();
			if (host == null)
				// Use IP address
				host = getRequestingSite().getHostAddress();
			int port = getRequestingPort();
			if (host == null || port < 0) {
				logger.warn("Unsupported request for " + getRequestingScheme()
						+ " " + getRequestingSite());
				return null;
			}
			uri = URI.create("socket://" + host + ":" + port);
		}

		if (credManager == null) {
			logger.warn("No Credential Manager");
			return null;
		}
		boolean usePathRecursion = false;
		String realm = getRequestingPrompt();
		if (getRequestingScheme().equals("basic")
				|| getRequestingScheme().equals("digest")) {
			usePathRecursion = true;
			if (realm != null && realm.length() > 0)
				try {
					uri = DistinguishedNameParserImpl.resolveUriFragment(uri, realm);
				} catch (URISyntaxException e) {
					logger.warn("Could not URI-encode fragment for realm: "
							+ realm);
				}
		}

		UsernamePassword usernameAndPassword;
		try {
			usernameAndPassword = credManager.getUsernameAndPasswordForService(uri,
					usePathRecursion, realm);
		} catch (CMException e) {
			logger.warn("Could not get username and password for " + uri, e);
			return null;
		}
		if (usernameAndPassword == null) {
			logger.warn("No username/password found for " + uri);
			return null;
		}
		PasswordAuthentication pwAuth = new PasswordAuthentication(
				usernameAndPassword.getUsername(), usernameAndPassword
						.getPassword());
		usernameAndPassword.resetPassword();
		return pwAuth;
	}
}
