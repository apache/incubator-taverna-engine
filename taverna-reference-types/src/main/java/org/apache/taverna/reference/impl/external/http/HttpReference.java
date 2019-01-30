/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.reference.impl.external.http;

import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.DereferenceException;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ExternalReferenceValidationException;
import org.apache.taverna.reference.ReferenceContext;

import org.apache.http.entity.ContentType;

/**
 * Implementation of ExternalReference used to refer to data held in a locally
 * accessible file. Inherits from
 * {@link org.apache.taverna.reference.AbstractExternalReference
 * AbstractExternalReference} to enable hibernate based persistence.
 * 
 */
public class HttpReference extends AbstractExternalReference implements
		ExternalReferenceSPI {
	private String httpUrlString = null;
	private URL httpUrl = null;
	private String charsetName = null;
	private boolean charsetFetched = false;
	private transient Long cachedLength;
	private transient Date cacheTime;

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
	@Override
	public InputStream openStream(ReferenceContext context)
			throws DereferenceException {
		try {
			return httpUrl.openStream();
		} catch (IOException e) {
			throw new DereferenceException(e);
		}
	}

	@Override
	public String getCharset() throws DereferenceException {
		if (charsetFetched)
			return charsetName;
		if (!httpUrl.getProtocol().equals("http")
				&& !httpUrl.getProtocol().equals("https")) {
			
			// We don't know, probably system charset
			return null;
		}
		// We already ruled out non-http/https above, so somewhat safe cast
		HttpURLConnection c;
		try {
			c = (HttpURLConnection) httpUrl.openConnection();
		} catch (IOException e) {
			// Does not matter, then we don't know charset. Assume this may fail later as well.
			charsetFetched = true;
			return null;
		}
		try {
			c.setRequestMethod("HEAD");
		} catch (ProtocolException e1) {
			throw new RuntimeException("HttpURLConnection does not recognize HEAD method", e1);
		}
				
		// We don't use the input stream, but do need to close it after
		try (InputStream is = c.getInputStream()) {
			//connection.connect();
			String type = c.getHeaderField("Content-Type");
			if (type != null) { 
				ContentType ct = ContentType.parse(type);
				Charset charset = ct.getCharset();
				if (charset != null) {					
					charsetName = charset.name();
				}
			}
			charsetFetched = true;
			return charsetName;
		} catch (IOException e) {
			// Does not matter, then we don't know charset. Assume this may fail later.
			charsetFetched = true;
			return null;
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

	// One minute
	private static final int CACHE_TIMEOUT = 60000;

	@Override
	public Long getApproximateSizeInBytes() {
		long now = currentTimeMillis();
		if (cachedLength != null && cacheTime != null
				&& cacheTime.getTime() + CACHE_TIMEOUT > now)
			return cachedLength;
		try {
			HttpURLConnection c = (HttpURLConnection) httpUrl.openConnection();
			c.setRequestMethod("HEAD");
			c.connect();
			String lenString = c.getHeaderField("Content-Length");
			if (lenString != null && !lenString.isEmpty()) {
				cachedLength = new Long(lenString);
				cacheTime = new Date(now);
				return cachedLength;
			}
			// there is no Content-Length field so we cannot know the size
		} catch (Exception e) {
			// something went wrong, but we don't care what
		}
		cachedLength = null;
		cacheTime = null;
		return new Long(-1);
	}

	/**
	 * @return the httpUrl
	 */
	public final URL getHttpUrl() {
		return httpUrl;
	}

	@Override
	public float getResolutionCost() {
		return (float) 200.0;
	}

	public void deleteData() {
		throw new UnsupportedOperationException(
				"Cannot delete data referenced by a URL");
	}

	@Override
	public HttpReference clone() {
		HttpReference result = new HttpReference();
		result.setHttpUrlString(this.getHttpUrlString());
		return result;
	}
}
