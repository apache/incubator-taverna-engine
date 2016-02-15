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

import static org.apache.taverna.security.credentialmanager.CredentialManager.USER_SET_MASTER_PASSWORD_INDICATOR_FILE_NAME;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.security.credentialmanager.MasterPasswordProvider;

//import org.apache.log4j.Logger;

public class DefaultMasterPasswordProvider implements MasterPasswordProvider {
	/**
	 * Default master password for Credential Manager - used by default and
	 * ignored if user sets their own
	 */
	private final String DEFAULT_MASTER_PASSWORD = "taverna";
	private ApplicationConfiguration appConfig;

	@Override
	public int getProviderPriority() {
		// Higher priority then the UI provider so this one will be tried first
		return 101;
	}

	/**
	 * Sets the applicationConfiguration.
	 * 
	 * @param applicationConfiguration
	 *            the new value of applicationConfiguration
	 */
	public void setApplicationConfiguration(
			ApplicationConfiguration applicationConfiguration) {
		appConfig = applicationConfiguration;
	}

	@Override
	public String getMasterPassword(boolean firstTime) {
		Path cmDir = DistinguishedNameParserImpl.getTheCredentialManagerDefaultDirectory(appConfig);
		Path flagFile = cmDir.resolve(USER_SET_MASTER_PASSWORD_INDICATOR_FILE_NAME);
		if (Files.exists(flagFile))
			return null;
		return DEFAULT_MASTER_PASSWORD;
	}

	@Override
	public void setMasterPassword(String password) {
		// We always ignore this; we're never changing our password
	}
}
