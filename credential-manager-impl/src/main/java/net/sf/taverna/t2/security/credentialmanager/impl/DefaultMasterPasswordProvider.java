package net.sf.taverna.t2.security.credentialmanager.impl;

import static net.sf.taverna.t2.security.credentialmanager.CredentialManager.USER_SET_MASTER_PASSWORD_INDICATOR_FILE_NAME;

import java.io.File;

import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

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
		File cmDir = CMUtils.getCredentialManagerDefaultDirectory(appConfig);
		File flagFile = new File(cmDir,
				USER_SET_MASTER_PASSWORD_INDICATOR_FILE_NAME);
		if (flagFile.exists())
			return null;
		return DEFAULT_MASTER_PASSWORD;
	}

	@Override
	public void setMasterPassword(String password) {
		// We always ignore this; we're never changing our password
	}
}
