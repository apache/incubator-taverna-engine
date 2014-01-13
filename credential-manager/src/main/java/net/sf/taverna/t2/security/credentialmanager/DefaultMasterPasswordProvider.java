package net.sf.taverna.t2.security.credentialmanager;

import java.net.URI;
import java.security.cert.X509Certificate;

//import org.apache.log4j.Logger;

public class DefaultMasterPasswordProvider implements CredentialProviderSPI {

	//private static Logger logger = Logger.getLogger(DefaultMasterPasswordProvider.class);
	
	// Default master password for Credential Manager - used by default and ignored if user sets their own
	private final String DEFAULT_MASTER_PASSWORD = "taverna";
	
	@Override
	public int getProviderPriority() {
		// Higher priority then the UI provider so this one will be tried first
		return 101;
	}

	@Override
	public boolean canHandleTrustConfirmation(X509Certificate[] chain) {
		return false;
	}

	@Override
	public boolean canProvideJavaTruststorePassword() {
		return false;
	}

	@Override
	public boolean canProvideMasterPassword() {		
		if (CredentialManager.getUseDefaultMasterPassword()) {
			return true;
		}
		else{
			// User has changed the default master password - we cannot provide master password in this case
			return false;
		}
	}

	public boolean canProvideUsernamePassword(URI serviceURI) {
		return false;
	}

	@Override
	public String getJavaTruststorePassword() {
		return null;
	}

	@Override
	public String getMasterPassword(boolean firstTime) {
		if (CredentialManager.getUseDefaultMasterPassword()){
			return DEFAULT_MASTER_PASSWORD;
		}
		else{
			return null;
		}
	}

	@Override
	public UsernamePassword getUsernamePassword(URI serviceURI,
			String requestingPrompt) {
		return null;
	}

	@Override
	public TrustConfirmation shouldTrust(X509Certificate[] chain) {
		return null;
	}


}
