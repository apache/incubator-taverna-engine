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

package org.apache.taverna.workflowmodel.health;

import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_GONE;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.taverna.visit.VisitReport.Status.SEVERE;
import static org.apache.taverna.visit.VisitReport.Status.WARNING;
import static org.apache.taverna.workflowmodel.health.HealthCheck.CONNECTION_PROBLEM;
import static org.apache.taverna.workflowmodel.health.HealthCheck.INVALID_URL;
import static org.apache.taverna.workflowmodel.health.HealthCheck.IO_PROBLEM;
import static org.apache.taverna.workflowmodel.health.HealthCheck.NOT_HTTP;
import static org.apache.taverna.workflowmodel.health.HealthCheck.NO_PROBLEM;
import static org.apache.taverna.workflowmodel.health.HealthCheck.TIME_OUT;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLException;

import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.visit.VisitReport.Status;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

/**
 * A RemoteHealthChecker performs a visit to an Activity by trying to contact a
 * specific endpoint
 * 
 * @author alanrw
 */
public abstract class RemoteHealthChecker implements HealthChecker<Object> {
	public static final long ENDPOINT_EXPIRY_MILLIS = 30 * 1000; // 30 seconds
	private static final Logger logger = Logger.getLogger(RemoteHealthChecker.class);
	private static int timeout = 10000; // TODO Manage via bean?
	private static long endpointExpiryMillis = ENDPOINT_EXPIRY_MILLIS;

	public static int getTimeoutInSeconds() {
		return timeout / 1000;
	}

	public static void setTimeoutInSeconds(int timeout) {
		RemoteHealthChecker.timeout = timeout * 1000;
	}

	public static long getEndpointExpiryInMilliseconds() {
		return endpointExpiryMillis;
	}

	public static void setEndpointExpiryInMilliseconds(int endpointExpiry) {
		endpointExpiryMillis = endpointExpiry;
	}

	/**
	 * Clear the cached endpoint statuses. Normally {@link RemoteHealthChecker}
	 * will only check an endpoint again if it has been more than
	 * {@link #getEndpointExpiryInMilliseconds()} milliseconds since last check,
	 * by default 30 seconds.
	 */
	public static void clearCachedEndpointStatus() {
		visitReportsByEndpoint.clear();
	}

	private static Map<String, WeakReference<VisitReport>> visitReportsByEndpoint = new ConcurrentHashMap<>();

	/**
	 * Try to contact the specified endpoint as part of the health-checking of
	 * the Activity.
	 * 
	 * @param activity
	 *            The activity that is being checked
	 * @param endpoint
	 *            The String corresponding to the URL of the endpoint
	 * 
	 * @return
	 */
	public static VisitReport contactEndpoint(Activity<?> activity,
			String endpoint) {
		WeakReference<VisitReport> cachedReportRef = visitReportsByEndpoint
				.get(endpoint);
		VisitReport cachedReport = null;
		if (cachedReportRef != null)
			cachedReport = cachedReportRef.get();
		if (cachedReport != null) {
			long now = currentTimeMillis();
			long age = now - cachedReport.getCheckTime();
			if (age < getEndpointExpiryInMilliseconds()) {
				VisitReport newReport;
				try {
					// Make a copy
					newReport = cachedReport.clone();
					// But changed the subject
					newReport.setSubject(activity);
					logger.info("Returning cached report for endpoint "
							+ endpoint + ": " + newReport);
					return newReport;
				} catch (CloneNotSupportedException e) {
					logger.warn("Could not clone VisitReport " + cachedReport,
							e);
				}
			}
		}
		
		Status status = Status.OK;
		String message = "Responded OK";
		int resultId = NO_PROBLEM;
		URLConnection connection = null;
		int responseCode = HTTP_OK;
		Exception ex = null;
		try {
			URL url = new URL(endpoint);
			connection = url.openConnection();
			connection.setReadTimeout(timeout);
			connection.setConnectTimeout(timeout);
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("HEAD");
				httpConnection.connect();
				responseCode = httpConnection.getResponseCode();
				if (responseCode != HTTP_OK) {
					try {
						if ((connection != null)
								&& (connection.getInputStream() != null))
							connection.getInputStream().close();
					} catch (IOException e) {
						logger.info(
								"Unable to close connection to " + endpoint, e);
					}
					connection = url.openConnection();
					connection.setReadTimeout(timeout);
					connection.setConnectTimeout(timeout);
					httpConnection = (HttpURLConnection) connection;
					httpConnection.setRequestMethod("GET");
					httpConnection.connect();
					responseCode = httpConnection.getResponseCode();
				}
				if (responseCode != HTTP_OK) {
					if ((responseCode > HTTP_INTERNAL_ERROR)) {
						status = WARNING;
						message = "Unexpected response";
						resultId = CONNECTION_PROBLEM;
					} else if ((responseCode == HTTP_NOT_FOUND)
							|| (responseCode == HTTP_GONE)) {
						status = WARNING;
						message = "Bad response";
						resultId = CONNECTION_PROBLEM;
					}
				}
			} else {
			    connection.connect();
				status = WARNING;
				message = "Not HTTP";
				resultId = NOT_HTTP;
			}
		} catch (MalformedURLException e) {
			status = SEVERE;
			message = "Invalid URL";
			resultId = INVALID_URL;
			ex = e;
		} catch (SocketTimeoutException e) {
			status = SEVERE;
			message = "Timed out";
			resultId = TIME_OUT;
			ex = e;
		} catch (SSLException e){
			// Some kind of error when trying to establish an HTTPS connection to the endpoint
			status = SEVERE;
			message = "HTTPS connection problem";
			resultId = IO_PROBLEM; // SSLException is an IOException
			ex = e;
		} catch (IOException e) {
			status = SEVERE;
			message = "Read problem";
			resultId = IO_PROBLEM;
			ex = e;
		} finally {
			try {
				if ((connection != null)
						&& (connection.getInputStream() != null))
					connection.getInputStream().close();
			} catch (IOException e) {
				logger.info("Unable to close connection to " + endpoint, e);
			}
		}
		
		VisitReport vr = new VisitReport(HealthCheck.getInstance(), activity, message,
				resultId, status);
		vr.setProperty("endpoint", endpoint);
		if (ex != null)
		    vr.setProperty("exception", ex);
		if (responseCode != HTTP_OK)
			vr.setProperty("responseCode", Integer.toString(responseCode));
		if (resultId == TIME_OUT)
			vr.setProperty("timeOut", Integer.toString(timeout));
		visitReportsByEndpoint.put(endpoint, new WeakReference<>(vr));
		return vr;
	}

	/**
	 * A remote health-check is time consuming as it tries to contact an
	 * external resource.
	 */
	@Override
	public boolean isTimeConsuming() {
		return true;
	}

}
