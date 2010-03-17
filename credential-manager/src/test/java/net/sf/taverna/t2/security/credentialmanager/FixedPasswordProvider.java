package net.sf.taverna.t2.security.credentialmanager;

import java.net.URI;
import java.security.cert.X509Certificate;

public class FixedPasswordProvider implements CredentialProviderSPI {

	private static UsernamePassword usernamePassword;
	private static URI serviceURI;
	private static String requestingPrompt;
	private static long calls = 0;
	
	public static long getCalls() {
		return calls;
	}
	

	public static void resetCalls() {
		calls = 0;
	}


	public boolean canProvideUsernamePassword(URI serviceURI) {
		return true;
	}

	public static URI getServiceURI() {
		return serviceURI;
	}

	public static String getRequestingPrompt() {
		return requestingPrompt;
	}

	public UsernamePassword getUsernamePassword(URI serviceURI,
			String requestingPrompt) {
		FixedPasswordProvider.serviceURI = serviceURI;
		FixedPasswordProvider.requestingPrompt = requestingPrompt;
		calls++;
		return usernamePassword.clone();
	}

	public int getProviderPriority() {
		// Well, only for testing
		return -1;
	}

	public static void setUsernamePassword(UsernamePassword usernamePassword) {
		FixedPasswordProvider.usernamePassword = usernamePassword;
	}

	public static UsernamePassword getUsernamePassword() {
		return usernamePassword;
	}


	public boolean canHandleTrustConfirmation(X509Certificate[] chain) {
		return false;
	}


	public boolean canProvideMasterPassword() {
		return false;
	}


	public boolean canProvideJavaTruststorePassword() {
		return false;
	}


	public String getJavaTruststorePassword() {
		return null;
	}


	public String getMasterPassword(boolean firstTime) {
		return null;
	}


	public TrustConfirmation shouldTrust(X509Certificate[] chain) {
		return null;
	}
}