#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.ui.serviceprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;

public class ${classPrefix}ServiceProvider implements ServiceDescriptionProvider {

	private static final URI providerId = URI
		.create("http://example.com/2011/service-provider/${rootArtifactId}");

	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		// Use callback.status() for long-running searches
		// callBack.status("Resolving example services");

		List<ServiceDescription> results = new ArrayList<ServiceDescription>();

		// FIXME: Implement the actual service search/lookup instead
		// of dummy for-loop
		for (int i = 1; i <= 5; i++) {
			${classPrefix}ServiceDesc service = new ${classPrefix}ServiceDesc();
			// Populate the service description bean
			service.setExampleString("Example " + i);
			service.setExampleUri("http://localhost:8192/service");

			// Optional: set description
			service.setDescription("Service example number " + i);
			results.add(service);
		}

		// partialResults() can also be called several times from inside
		// for-loop if the full search takes a long time
		callBack.partialResults(results);

		// No more results will be coming
		callBack.finished();
	}

	/**
	 * Icon for service provider
	 */
	public Icon getIcon() {
		return ${classPrefix}ServiceIcon.getIcon();
	}

	/**
	 * Name of service provider, appears in right click for 'Remove service
	 * provider'
	 */
	public String getName() {
		return "My example service";
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getId() {
		return providerId.toASCIIString();
	}

}
