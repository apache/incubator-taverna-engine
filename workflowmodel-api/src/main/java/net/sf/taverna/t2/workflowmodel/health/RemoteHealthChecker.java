/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

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

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

/**
 * A RemoteHealthChecker performs a visit to an Activity by trying to contact a
 * specific endpoint
 * 
 * @author alanrw
 * 
 */
public abstract class RemoteHealthChecker implements HealthChecker<Object> {
		
	public static final long ENDPOINT_EXPIRY_MILLIS = 30 * 1000; // 30 seconds

	private static Logger logger = Logger.getLogger(RemoteHealthChecker.class);
	
	private static int timeout = 10000;

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

	private static Map<String, WeakReference<VisitReport>> visitReportsByEndpoint = new ConcurrentHashMap<String, WeakReference<VisitReport>>();

	
	/**
	 * Try to contact the specified endpoint as part of the health-checking of the Activity.
	 * 
	 * @param activity The activity that is being checked
	 * @param endpoint The String corresponding to the URL of the endpoint
	 * 
	 * @return
	 */
	public static VisitReport contactEndpoint(Activity activity, String endpoint) {

		WeakReference<VisitReport> cachedReportRef = visitReportsByEndpoint.get(endpoint);
		VisitReport cachedReport = null;
		if (cachedReportRef != null) {
			cachedReport = cachedReportRef.get();
		}
		if (cachedReport != null) {
			long now = System.currentTimeMillis();
			long age = now - cachedReport.getCheckTime();
			if (age < getEndpointExpiryInMilliseconds()) {
				VisitReport newReport;
				try {
					// Make a copy
					newReport = cachedReport.clone();
					// But changed the subject
					newReport.setSubject(activity);
					logger.info("Returning cached report for endpoint " + endpoint + ": " + newReport);
					return newReport;
				} catch (CloneNotSupportedException e) {
					logger.warn("Could not clone VisitReport " + cachedReport, e);
				}				
			}
		}
		
		Status status = Status.OK;
		String message = "Responded OK";
		int resultId = HealthCheck.NO_PROBLEM;
		URLConnection connection = null;
		int responseCode = HttpURLConnection.HTTP_OK;
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
				if (responseCode != HttpURLConnection.HTTP_OK) {
					try {
						if ((connection != null) && (connection.getInputStream() != null)) {
							connection.getInputStream().close();
						}
					} catch (IOException e) {
						logger.info("Unable to close connection to " + endpoint, e);
					}
					connection = url.openConnection();
					connection.setReadTimeout(timeout);
					connection.setConnectTimeout(timeout);
					httpConnection = (HttpURLConnection) connection;
					httpConnection.setRequestMethod("GET");
					httpConnection.connect();
					responseCode = httpConnection.getResponseCode();
				}
 				if (responseCode != HttpURLConnection.HTTP_OK) {
					if ((responseCode > HttpURLConnection.HTTP_INTERNAL_ERROR)) {
						status = Status.WARNING;
						message = "Unexpected response";
						resultId = HealthCheck.CONNECTION_PROBLEM;
					}
					else if ((responseCode == HttpURLConnection.HTTP_NOT_FOUND)
							|| (responseCode == HttpURLConnection.HTTP_GONE)) {
						status = Status.WARNING;
						message = "Bad response";
						resultId = HealthCheck.CONNECTION_PROBLEM;
					}
				}
			} else {
			    connection.connect();
				status = Status.WARNING;
				message = "Not HTTP";
				resultId = HealthCheck.NOT_HTTP;
			}

		} catch (MalformedURLException e) {
			status = Status.SEVERE;
			message = "Invalid URL";
			resultId = HealthCheck.INVALID_URL;
			ex = e;
		} catch (SocketTimeoutException e) {
			status = Status.SEVERE;
			message = "Timed out";
			resultId = HealthCheck.TIME_OUT;
			ex = e;
		}  catch (SSLException e){
				// Some kind of error when trying to establish an HTTPS connection to the endpoint
				status = Status.SEVERE;
				message = "HTTPS connection problem";
				resultId = HealthCheck.IO_PROBLEM; // SSLException is an IOException
				ex = e;
		} catch (IOException e) {
			status = Status.SEVERE;
			message = "Read problem";
			resultId = HealthCheck.IO_PROBLEM;
			ex = e;
		} finally {
			try {
				if ((connection != null) && (connection.getInputStream() != null)) {
					connection.getInputStream().close();
				}
			} catch (IOException e) {
				logger.info("Unable to close connection to " + endpoint, e);
			}
		}
		
		VisitReport vr = new VisitReport(HealthCheck.getInstance(), activity, message,
				resultId, status);
		vr.setProperty("endpoint", endpoint);
		if (ex != null) {
		    vr.setProperty("exception", ex);
		}
		if (responseCode != HttpURLConnection.HTTP_OK) {
			vr.setProperty("responseCode", Integer.toString(responseCode));
		}
		if (resultId == HealthCheck.TIME_OUT) {
			vr.setProperty("timeOut", Integer.toString(timeout));
		}
		visitReportsByEndpoint.put(endpoint, new WeakReference<VisitReport>(vr));
		return vr;
	}

	/**
	 * A remote health-check is time consuming as it tries to contact an external resource.
	 * 
	 * (non-Javadoc)
	 * @see net.sf.taverna.t2.visit.Visitor#isTimeConsuming()
	 */
	public boolean isTimeConsuming() {
		return true;
	}

}
