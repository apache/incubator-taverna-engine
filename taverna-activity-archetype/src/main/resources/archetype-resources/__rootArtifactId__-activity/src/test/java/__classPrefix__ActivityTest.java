#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.taverna.t2.activities.testutils.ActivityInvoker;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

import org.junit.Before;
import org.junit.Test;

public class ${classPrefix}ActivityTest {

	private ObjectNode configuration;

	private ${classPrefix}Activity activity = new ${classPrefix}Activity();

	@Before
	public void makeConfiguration() throws Exception {
		configuration = JsonNodeFactory.instance.objectNode();
		configuration.put("exampleString", "something");
		configuration.put("exampleUri", "http://localhost:8080/myEndPoint");
	}

	@Test
	public void configureActivity() throws Exception {
		activity.configure(configuration);
		assertTrue(configuration.equals(activity.getConfiguration()));
	}

	@Test(expected = ActivityConfigurationException.class)
	public void invalidConfiguration() throws ActivityConfigurationException {
		ObjectNode invalidBean = JsonNodeFactory.instance.objectNode();
		invalidBean.put("exampleString", "invalidExample");
		// Should throw ActivityConfigurationException
		activity.configure(invalidBean);
	}

	@Test
	public void executeAsynch() throws Exception {
		activity.configure(configuration);

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("firstInput", "hello");

		Map<String, Class<?>> expectedOutputTypes = new HashMap<String, Class<?>>();
		expectedOutputTypes.put("simpleOutput", String.class);
		expectedOutputTypes.put("moreOutputs", String.class);

		Map<String, Object> outputs = ActivityInvoker.invokeAsyncActivity(
				activity, inputs, expectedOutputTypes);

		assertEquals("Unexpected outputs", 2, outputs.size());
		assertEquals("simple", outputs.get("simpleOutput"));
		assertEquals(Arrays.asList("Value 1", "Value 2"), outputs
				.get("moreOutputs"));

	}

}
