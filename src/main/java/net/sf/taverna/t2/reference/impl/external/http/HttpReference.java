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
package net.sf.taverna.t2.reference.impl.external.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.taverna.t2.reference.AbstractExternalReference;
import net.sf.taverna.t2.reference.DereferenceException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ExternalReferenceValidationException;
import net.sf.taverna.t2.reference.ReferenceContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.HeadMethod;

/**
 * Implementation of ExternalReference used to refer to data held in a locally
 * accessible file. Inherits from
 * {@link net.sf.taverna.t2.reference.AbstractExternalReference AbstractExternalReference}
 * to enable hibernate based persistence.
 * 
 * @author Tom Oinn
 * 
 */
public class HttpReference extends AbstractExternalReference implements
		ExternalReferenceSPI {

	private String httpUrlString = null;
	private URL httpUrl = null;

	/**
	 * Explicitly declare default constructor, will be used by hibernate when
	 * constructing instances of this bean from the database.
	 */
	public HttpReference() {
		super();
	}

	/**
	 * Return the data at the {@link URL} represented by this external reference
	 */
	public InputStream openStream(ReferenceContext context)
			throws DereferenceException {
		try {
			return httpUrl.openStream();
		} catch (IOException e) {
			throw new DereferenceException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCharset() throws DereferenceException {
		if (!httpUrl.getProtocol().equals("http")) {
			return null; // Don't know
		}
		HeadMethod method = new HeadMethod(httpUrl.toExternalForm());
		HttpClient httpClient = new HttpClient();
		try {
			httpClient.executeMethod(method);
			return method.getResponseCharSet();
		} catch (HttpException e) {
			throw new DereferenceException(e);
		} catch (IOException e) {
			throw new DereferenceException(e);
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Setter used by hibernate to set the file path property of the file
	 * reference
	 * 
	 * @throws ExternalReferenceValidationException
	 *             if there is some problem parsing the supplied string as a URL
	 */
	public void setHttpUrlString(String httpUrlString) {
		try {
			this.httpUrlString = httpUrlString;
			this.httpUrl = new URL(httpUrlString);
		} catch (MalformedURLException e) {
			throw new ExternalReferenceValidationException(e);
		}
	}

	/**
	 * Getter used by hibernate to retrieve the file path string property
	 */
	public String getHttpUrlString() {
		return this.httpUrlString;
	}

	/**
	 * Human readable string form for debugging, should not be regarded as
	 * stable.
	 */
	@Override
	public String toString() {
		return "http{" + httpUrl.toExternalForm() + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((httpUrl == null) ? 0 : httpUrl.toExternalForm().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof HttpReference))
			return false;
		final HttpReference other = (HttpReference) obj;
		if (httpUrl == null) {
			if (other.httpUrl != null)
				return false;
		} else if (!httpUrl.toExternalForm().equals(
				other.httpUrl.toExternalForm()))
			return false;
		return true;
	}

}
