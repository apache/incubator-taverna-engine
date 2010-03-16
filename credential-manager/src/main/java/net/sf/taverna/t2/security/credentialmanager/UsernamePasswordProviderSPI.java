package net.sf.taverna.t2.security.credentialmanager;

import java.net.URI;

public interface UsernamePasswordProviderSPI {

	public int providerPriority();

	public boolean canProvideCredentialFor(URI serviceURI);

	public UsernamePassword getUsernamePassword(URI serviceURI,
			String requestingPrompt);
	
}
