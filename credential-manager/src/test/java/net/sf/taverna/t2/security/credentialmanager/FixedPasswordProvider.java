package net.sf.taverna.t2.security.credentialmanager;

import java.net.URI;

public class FixedPasswordProvider implements UsernamePasswordProviderSPI {

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


	public boolean canProvideCredentialFor(URI serviceURI) {
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
		return usernamePassword;
	}

	public int providerPriority() {
		// Well, only for testing
		return -1;
	}

	public static void setUsernamePassword(UsernamePassword usernamePassword) {
		FixedPasswordProvider.usernamePassword = usernamePassword;
	}

	public static UsernamePassword getUsernamePassword() {
		return usernamePassword;
	}
}