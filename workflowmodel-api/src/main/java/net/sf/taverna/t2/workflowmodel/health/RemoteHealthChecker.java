/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.health;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

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
	
	private static Logger logger = Logger.getLogger(RemoteHealthChecker.class);

	private static int timeout = 1000;

	public static int getTimeoutInSeconds() {
		return timeout / 1000;
	}

	public static void setTimeoutInSeconds(int timeout) {
		RemoteHealthChecker.timeout = timeout * 1000;
	}

	/**
	 * Try to contact the specified endpoint as part of the health-checking of the Activity.
	 * 
	 * @param activity The activity that is being checked
	 * @param endpoint The String corresponding to the URL of the endpoint
	 * 
	 * @return
	 */
	public static VisitReport contactEndpoint(Activity activity, String endpoint) {

		Status status = Status.OK;
		String message = "Responded OK";
		int resultId = HealthCheck.NO_PROBLEM;
		URLConnection connection = null;
		int responseCode = HttpURLConnection.HTTP_OK;
		try {
			URL url = new URL(endpoint);
			connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("HEAD");
				httpConnection.setReadTimeout(timeout);
				httpConnection.connect();
				responseCode = httpConnection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK) {
					if ((responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR)
							|| (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
							|| (responseCode == HttpURLConnection.HTTP_GONE)) {
						status = Status.SEVERE;
						message = "Bad response";
						resultId = HealthCheck.CONNECTION_PROBLEM;
					}
				}
			} else {
				status = Status.WARNING;
				message = "Not HTTP";
				resultId = HealthCheck.NOT_HTTP;
				System.err.println(endpoint + " is not HTTP");
			}

		} catch (MalformedURLException e) {
			status = Status.SEVERE;
			message = "Invalid URL";
			resultId = HealthCheck.INVALID_URL;
		} catch (SocketTimeoutException e) {
			status = Status.SEVERE;
			message = "Timed out";
			resultId = HealthCheck.TIME_OUT;
		} catch (IOException e) {
			status = Status.SEVERE;
			message = "I/O problem";
			resultId = HealthCheck.IO_PROBLEM;
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
		if (responseCode != HttpURLConnection.HTTP_OK) {
			vr.setProperty("responseCode", responseCode);
		}
		if (resultId == HealthCheck.TIME_OUT) {
			vr.setProperty("timeOut", Integer.toString(timeout));
		}
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
