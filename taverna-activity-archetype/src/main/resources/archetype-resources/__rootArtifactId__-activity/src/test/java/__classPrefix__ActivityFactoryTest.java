#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

import org.junit.Before;
import org.junit.Test;

public class ${classPrefix}ActivityFactoryTest {

	private ObjectNode configuration;

	private ${classPrefix}ActivityFactory activityFactory;

	@Before
	public void setUp() throws Exception {
		configuration = JsonNodeFactory.instance.objectNode();
		configuration.put("exampleString", "something");
		configuration.put("exampleUri", "http://localhost:8080/myEndPoint");

		activityFactory = new ${classPrefix}ActivityFactory();
		activityFactory.setEdits(new EditsImpl());
	}

	@Test
	public void testCreateActivity() {
		${classPrefix}Activity activity = activityFactory.createActivity();
		assertNotNull(activity);
		assertNotSame(activity, activityFactory.createActivity());
	}

	@Test
	public void testGetActivityURI() {
		assertEquals(URI.create(${classPrefix}Activity.ACTIVITY_TYPE), activityFactory.getActivityType());
	}

	@Test
	public void testGetActivityConfigurationSchema() {
		JsonNode configurationSchema = activityFactory.getActivityConfigurationSchema();
		assertNotNull(configurationSchema);
		assertTrue(configurationSchema.has("properties"));
		JsonNode propertiesNode = configurationSchema.get("properties");
		assertTrue(propertiesNode.has("exampleString"));
		assertTrue(propertiesNode.has("exampleUri"));
	}

	@Test
	public void testGetInputPorts() {
		Set<String> expectedInputs = new HashSet<String>();
		expectedInputs.add("firstInput");

		Set<ActivityInputPort> inputPorts = activityFactory.getInputPorts(configuration);
		assertEquals("Unexpected inputs", expectedInputs.size(), inputPorts.size());
		for (ActivityInputPort inputPort : inputPorts) {
			assertTrue("Wrong input : " + inputPort.getName(), expectedInputs
					.remove(inputPort.getName()));
		}

		ObjectNode specialConfiguration = JsonNodeFactory.instance.objectNode();
		specialConfiguration.put("exampleString", "specialCase");
		specialConfiguration.put("exampleUri", "http://localhost:8080/myEndPoint");

		assertEquals("Unexpected inputs", 2, activityFactory.getInputPorts(specialConfiguration).size());
	}

	@Test
	public void testGetOutputPorts() {
		Set<String> expectedOutputs = new HashSet<String>();
		expectedOutputs.add("simpleOutput");
		expectedOutputs.add("moreOutputs");

		Set<ActivityOutputPort> outputPorts = activityFactory.getOutputPorts(configuration);
		assertEquals("Unexpected outputs", expectedOutputs.size(), outputPorts.size());
		for (ActivityOutputPort outputPort : outputPorts) {
			assertTrue("Wrong output : " + outputPort.getName(),
					expectedOutputs.remove(outputPort.getName()));
		}

		ObjectNode specialConfiguration = JsonNodeFactory.instance.objectNode();
		specialConfiguration.put("exampleString", "specialCase");
		specialConfiguration.put("exampleUri", "http://localhost:8080/myEndPoint");

		assertEquals("Unexpected outputs", 3, activityFactory.getOutputPorts(specialConfiguration).size());
	}

}
