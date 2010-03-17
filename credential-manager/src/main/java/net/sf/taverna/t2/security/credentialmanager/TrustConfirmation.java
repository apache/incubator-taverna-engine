package net.sf.taverna.t2.security.credentialmanager;

public class TrustConfirmation {

	private boolean shouldTrust;
	private boolean shouldSave;

	public boolean isShouldTrust() {
		return shouldTrust;
	}

	public void setShouldTrust(boolean shouldTrust) {
		this.shouldTrust = shouldTrust;
	}

	public boolean isShouldSave() {
		return shouldSave;
	}

	public void setShouldSave(boolean shouldSave) {
		this.shouldSave = shouldSave;
	}

}
