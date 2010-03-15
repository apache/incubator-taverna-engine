package net.sf.taverna.t2.security.credentialmanager;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Credential manager backed {@link Authenticator}. 
 * <p>
 * Initialize by using:
 * <code>
 * Authenticator.setDefault(new CredentialManagerAuthenticator());
 * </code>
 * <p>
 * Special case included for proxy authentication
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class CredentialManagerAuthenticator extends Authenticator {

	private static Logger logger = Logger
			.getLogger(CredentialManagerAuthenticator.class);

	private CredentialManager credManager;

	public CredentialManagerAuthenticator() {
		this.setCredManager(null); // Discover when first needed
		
	}

	public CredentialManagerAuthenticator(CredentialManager credManager) {
		this.setCredManager(credManager);
	}

	public void setCredManager(CredentialManager credManager) {
		this.credManager = credManager;
	}

	public CredentialManager getCredManager() {
		if (credManager == null) {
			try {
				credManager = CredentialManager.getInstance();
			} catch (CMException e) {
				logger.warn("Could not find credential manager", e);
			}
		}
		return credManager;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		if (getRequestorType().equals(RequestorType.PROXY)) {
			String password = System.getProperty("http.proxyPassword");
			String username = System.getProperty("http.proxyUser");
			if (username == null || password == null) {
				// No proxy authentication set
				return null;
			} else {
				return new PasswordAuthentication(username, password
						.toCharArray());
			}
		}

		CredentialManager cm = getCredManager();
		if (cm == null) {
			return null;
		}
		URL url = getRequestingURL();
		if (url == null) {
			logger.warn("Unsupported request (no URL) for "
					+ getRequestingHost());
			return null;
		}
		try {
			String[] usernameAndPassword;
			usernameAndPassword = cm.getUsernameAndPasswordForService(url
					.toExternalForm());
			if (usernameAndPassword == null) {
				logger.warn("No username/password found for " + url);
				return null;
			}
			return new PasswordAuthentication(usernameAndPassword[0],
					usernameAndPassword[1].toCharArray());
		} catch (CMException e) {
			logger.warn("Could not get username and password for " + url, e);
			return null;
		}
	}
}
