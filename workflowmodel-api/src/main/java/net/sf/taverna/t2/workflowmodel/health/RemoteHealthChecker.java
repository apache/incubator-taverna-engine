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
import java.util.List;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * A RemoteHealthChecker performs a visit to an Activity by trying to contact a
 * specific endpoint
 * 
 * @author alanrw
 * 
 */
public abstract class RemoteHealthChecker implements HealthChecker<Object> {

	private static final int TIMEOUT = 10000;

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
		try {
			URL url = new URL(endpoint);
			URLConnection connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("HEAD");
				httpConnection.setReadTimeout(TIMEOUT);
				httpConnection.connect();
				int responseCode = httpConnection.getResponseCode();
				if (responseCode != HttpURLConnection.HTTP_OK) {
					if ((responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR)
							|| (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
							|| (responseCode == HttpURLConnection.HTTP_GONE)) {
						status = Status.SEVERE;
						message = "Responded with code: " + responseCode;
						resultId = HealthCheck.CONNECTION_PROBLEM;
					}
				}
				httpConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			status = Status.SEVERE;
			message = "Location is not a valid URL";
			resultId = HealthCheck.INVALID_URL;
		} catch (SocketTimeoutException e) {
			status = Status.WARNING;
			message = "Failed to respond within 10s";
			resultId = HealthCheck.TIME_OUT;
		} catch (IOException e) {
			status = Status.SEVERE;
			message = "Error connecting : " + e.getMessage();
			resultId = HealthCheck.IO_PROBLEM;
		}

		return new VisitReport(HealthCheck.getInstance(), activity, message,
				resultId, status);
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
