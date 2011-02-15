package uk.org.taverna.platform;

import java.net.URI;
import java.util.List;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;

public class ActivityTest extends PlatformTest {

	public void testGetActivityURIs() {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("uk.org.taverna.platform.activity.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);
		List<URI> activityURIs = activityService.getActivityURIs();
		System.out.println("================= Available Activities ===================");
		for (URI uri : activityURIs) {
			System.out.println(uri);
		}
		System.out.println("==========================================================");
		System.out.println("");
	}
		
	public void testGetActivityConfigurationDefinition() throws Exception {
		ServiceReference activityServiceReference = bundleContext.getServiceReference("uk.org.taverna.platform.activity.ActivityService");
		ActivityService activityService = (ActivityService) bundleContext.getService(activityServiceReference);

		List<URI> activityURIs = activityService.getActivityURIs();
		System.out.println("============ Activity Configuration Definitions ==========");
		for (URI uri : activityURIs) {
			ConfigurationDefinition configurationDefinition = activityService.getActivityConfigurationDefinition(uri);
			System.out.println(configurationDefinition);
		}
		System.out.println("==========================================================");
	}
}
