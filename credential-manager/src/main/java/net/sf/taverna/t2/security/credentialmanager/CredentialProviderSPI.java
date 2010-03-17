/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.security.credentialmanager;

import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * Defines an interface for providing credentials for the Credential Manager.
 * <p>
 * One typical implementation of this class would be able to pop up a user
 * interface for one or more methods. Such providers should check
 * {@link GraphicsEnvironment#isHeadless()} before returning true on any
 * canHandle/canProvide method, to avoid attempts to pop up dialogues on server
 * installations.
 * <p>
 * Providers must return a priority from {@link #getProviderPriority()},
 * allowing for multiple implementations of this SPI with partial
 * responsibilities..
 * <p>
 * Providers must respond <code>true</code> on the corresponding
 * canHandle/canProvide method in order to be asked. For instance, if a provider
 * returns true on {@link #canProvideMasterPassword()} and
 * {@link #canProvideUsernamePassword(URI)} it might be asked using
 * {@link #getMasterPassword()} and {@link #getUsernamePassword(URI, String)},
 * but would not be asked on {@link #shouldTrust(X509Certificate[])} or
 * {@link #getJavaTruststorePassword()}.
 * <p>
 * It is safe to return <code>null</code> if the provider does not have an
 * opinion, in which case the Credential Manager would ask a provider with a
 * lower priority that can handle the request.
 * 
 * @see CredentialManager
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * 
 */
public interface CredentialProviderSPI {

	/**
	 * Priority of this provider.
	 * <p>
	 * The providers with highest priority will be asked first, lower-priority
	 * providers will be asked only if the higher ones either return
	 * <code>false</code> on the canProvide/canHandle request, or return
	 * <code>null</code> on the corresponding actual request.
	 * <p>
	 * It is undetermined who will be asked first if providers have the same
	 * priority, unless they have non-overlapping features in their
	 * canHandle/canProvide.
	 * <p>
	 * A typical priority for user interfaces could be <code>100</code>,
	 * allowing server-side providers to override with priorities like
	 * <code>500</code>, or fall-back providers (say by reading system
	 * properties) to have a priority of <code>10</code>.
	 * 
	 * @return The priority of this provider. Higher number means higher
	 *         priority.
	 */
	public int getProviderPriority();

	/**
	 * Return <code>true</code> if this provider can handle trust confirmation
	 * of a X509 certificate.
	 * <p>
	 * The actual confirmation will be requested later by calling
	 * {@link #shouldTrust(X509Certificate[])}.
	 * <p>
	 * The certificate in question is provided in case this provider only can
	 * confirm a specific set of certificates. General providers must always
	 * return <code>true</code>, as the actual confirmation is requested using
	 * {@link #shouldTrust(X509Certificate[])}.
	 * 
	 * @see #shouldTrust(X509Certificate[])
	 * @param chain
	 *            X509 certificate that will be asked confirmation for
	 * @return <code>true</code> if this provider is able to handle trust
	 *         confirmations
	 */
	public boolean canHandleTrustConfirmation(X509Certificate[] chain);

	/**
	 * Return <code>true</code> if this provider can handle requests for the
	 * password for unlocking the Java trust store.
	 * <p>
	 * The password will be requested using {@link #getJavaTruststorePassword()}.
	 * 
	 * @see #getJavaTruststorePassword()
	 * @return <code>true</code> if this provider is able to handle requests for
	 *         the Java truststore password
	 */
	public boolean canProvideJavaTruststorePassword();

	/**
	 * Return <code>true</code> if this provider can handle requests for the
	 * master password forCredential Manager's keychain.
	 * <p>
	 * The master password will be requested using {@link #getMasterPassword()}.
	 * 
	 * @see #getMasterPassword()
	 * @return <code>true</code> if this provider is able to handle requests for
	 *         the Credential Manager master password.
	 */
	public boolean canProvideMasterPassword();

	/**
	 * Return <code>true</code> if this provider can handle requests for
	 * username and password for services and resources.
	 * <p>
	 * The actual request for credentials will be performed using
	 * {@link #getUsernamePassword(URI, String)}.
	 * <p>
	 * The URI for the service in question is provided in case this provider
	 * only can confirm a specific set of services. General providers must
	 * always return <code>true</code>, as the actual request would come by
	 * calling {@link #getUsernamePassword(URI, String)} .
	 * 
	 * @see #getUsernamePassword(URI, String)
	 * @param serviceURI
	 *            URI of service which confirmation is asked for
	 * @return <code>true</code> if this provider is able to provide username
	 *         and password
	 */
	public boolean canProvideUsernamePassword(URI serviceURI);

	/**
	 * Get the Java truststore password.
	 * <p>
	 * This method will only be called if the provider returned
	 * <code>true</code> from {@link #canProvideJavaTruststorePassword()}.
	 * <p>
	 * This method will be called when initialising the Credential Manager (CM)
	 * for the first time, in the cases where the Java trust store password has
	 * been changed from the VM default. The credential manager will need this
	 * password to unlock the Java trust store and copy the trusted certificate
	 * into the CM's own keychain.
	 * <p>
	 * Generally only advanced users would change this password.
	 * 
	 * @see #canProvideJavaTruststorePassword()
	 * @return The Java truststore password, or <code>null</code> if not
	 *         available (for instance if user action was cancelled).
	 */
	public String getJavaTruststorePassword();

	/**
	 * Get the master password for keychain of the Credential Manager.
	 * <p>
	 * This method will only be called if the provider returned
	 * <code>true</code> from {@link #canProvideMasterPassword()}.
	 * <p>
	 * This master password is used to encrypt the credential manager keychain.
	 * If the parameter <code>firstTime</code> is <code>true</code>, this is a
	 * request for <em>setting</em> the master password, as the keychain is not
	 * yet created.
	 * 
	 * @see #canProvideMasterPassword()
	 * @param firstTime
	 *            <code>true</code> if this is the first time the keychain is
	 *            accessed, in which case the returned password will be used to
	 *            encrypt the chain. If <code>false</code>, the returned
	 *            password will be used to decrypt (unlock) the chain.
	 * @return The master password, or <code>null</code> if not available (user
	 *         cancelled, etc)
	 */
	public String getMasterPassword(boolean firstTime);

	/**
	 * Get username and password for a given service.
	 * <p>
	 * This method will only be called if the provider returned
	 * <code>true</code> from {@link #canProvideUsernamePassword(URI)}.
	 * <p>
	 * This method will be called if the credential manager has been requested
	 * for the username and password to authenticate against the given service,
	 * and no stored/valid credentials already existed in credential manager.
	 * <p>
	 * The <code>serviceURI</code> parameter will identify the service or
	 * resource that needs authentication. For HTTP Basic Auth and Digest this
	 * URI will include a <em>fragment</em> identifying the <em>realm</em> of
	 * the service, for instance the realm <code>myExperiment API</code>:
	 * 
	 * <pre>
	 *     http://www.myexperiment.org/workflows.xml?id=13#myExperiment%20API
	 * </pre>
	 * <p>
	 * If the <code>requestingPrompt</code> is not empty, this is a message or
	 * prompt that should be included in any user interface.
	 * <p>
	 * The return <code>null</code> means the user cancelled the request. The
	 * returned {@link UsernamePassword} must have its
	 * {@link UsernamePassword#setUsername(String)} and
	 * {@link UsernamePassword#setPassword(char[])} set. If
	 * {@link UsernamePassword#isShouldSave()} is true, the credential manager
	 * will store the credentials in its keychain.
	 * 
	 * @see #canProvideUsernamePassword(URI)
	 * @param serviceURI
	 *            Service which credentials are requested.
	 * @param requestingPrompt
	 *            If not <code>null</code>, an additional prompt identifiying
	 *            the service or request. For HTTP Basic Auth, this is the
	 *            <em>realm</em> of the service.
	 * @return An initialized {@link UsernamePassword} instance, or
	 *         <code>null</code> if credentials could not be provided. (say if
	 *         user cancelled).
	 */
	public UsernamePassword getUsernamePassword(URI serviceURI,
			String requestingPrompt);

	/**
	 * Ask if the given certificate should be trusted or not.
	 * <p>
	 * This method will only be called if
	 * {@link #canHandleTrustConfirmation(X509Certificate[])} returned
	 * <code>true</code>.
	 * <p>
	 * This method is called when an SSL connection is attempted against a
	 * certificate which could not be confirmed using the credential manager's
	 * keychain.
	 * <p>
	 * The provider can return <code>null</code> if it does not have an opinion
	 * whether the certificate should be confirmed or not (in which case other
	 * providers will be asked), or an instance of {@link TrustConfirmation}
	 * confirming or denying if the certificate is to be trusted.
	 * <p>
	 * If {@link TrustConfirmation#isShouldSave()} and
	 * {@link TrustConfirmation#isShouldTrust()} are <code>true</code>, the
	 * credential manager will save the first link of the certificate chain
	 * (chain[0]) in its keychain.
	 * 
	 * @param chain
	 *            X509 certificate chain to confirm to be trusted or not
	 * @return <code>null</code> if the provider does not have an opinion, or a
	 *         {@link TrustConfirmation} which
	 *         {@link TrustConfirmation#isShouldTrust()} determines if
	 *         certificate is to be trusted.
	 */
	public TrustConfirmation shouldTrust(X509Certificate[] chain);

}
