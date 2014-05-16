package net.sf.taverna.t2.security.credentialmanager.impl;

import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;

//import org.apache.log4j.Logger;

public class DefaultMasterPasswordProvider implements MasterPasswordProvider {
	/**
	 * Default master password for Credential Manager - used by default and
	 * ignored if user sets their own
	 */
	private final String DEFAULT_MASTER_PASSWORD = "taverna";
	
	@Override
	public int getProviderPriority() {
		// Higher priority then the UI provider so this one will be tried first
		return 101;
	}

	@Override
	public String getMasterPassword(boolean firstTime) {
		return DEFAULT_MASTER_PASSWORD;
	}

	@Override
	public void setMasterPassword(String password) {
		// We always ignore this; we're never changing our password
	}
}
