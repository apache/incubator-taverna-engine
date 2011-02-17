package uk.org.taverna.platform;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.osgi.framework.Bundle;
import org.springframework.osgi.util.OsgiStringUtils;

public class OSGIFrameworkTest extends PlatformTest {

	public void testOsgiEnvironment() throws Exception {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out.println(OsgiStringUtils.nullSafeName(bundles[i]));
		}
		System.out.println();
	}

}
