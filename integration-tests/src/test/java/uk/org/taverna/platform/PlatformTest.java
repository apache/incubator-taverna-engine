package uk.org.taverna.platform;

import java.util.Map;

import net.sf.taverna.t2.reference.T2Reference;

import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

import uk.org.taverna.platform.report.WorkflowReport;

public class PlatformTest extends AbstractConfigurableBundleCreatorTests {

	protected String[] getTestBundlesNames() {
		return new String[] {
				"net.sf.taverna.t2.core, reference-api, 2.0-SNAPSHOT",
				"org.jdom, com.springsource.org.jdom, 1.1.0",
				"net.sf.taverna.t2.lang, observer, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-api, 2.0-SNAPSHOT",
				"org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",
				"javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
				"com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.2.2",
				"net.sf.taverna.t2.core, workflowmodel-impl, 2.0-SNAPSHOT",
				"org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",
				"org.apache.commons, com.springsource.org.apache.commons.discovery, 0.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
				"org.apache.commons, com.springsource.org.apache.commons.net, 1.4.1",
				"net.sf.taverna.t2.core, reference-core-extensions, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-impl, 2.0-SNAPSHOT",
				"uk.org.taverna.scufl2, scufl2-api, 0.1-SNAPSHOT",
				"uk.org.taverna.scufl2, scufl2-t2flow, 0.1-SNAPSHOT",
				"uk.org.taverna.platform, report, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, execution, 0.0.1-SNAPSHOT",
				"net.sf.taverna.t2.activities, dependency-activity, 2.0-SNAPSHOT",
				"javax.servlet, com.springsource.javax.servlet, 2.5.0",
				"org.beanshell, com.springsource.bsh, 2.0.0.b4",
				"net.sf.taverna.t2.activities, beanshell-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, stringconstant-activity, 2.0-SNAPSHOT",
				"javax.activation, com.springsource.javax.activation, 1.1.1",
				"javax.jms, com.springsource.javax.jms, 1.1.0",
				"javax.mail, com.springsource.javax.mail, 1.4.0",
				"javax.xml.soap, com.springsource.javax.xml.soap, 1.3.0",
				"javax.xml.rpc, com.springsource.javax.xml.rpc, 1.1.0",
				"org.apache.axis, com.springsource.org.apache.axis, 1.4.0",
				"javax.wsdl, com.springsource.javax.wsdl, 1.6.1",
				"net.sf.taverna, wsdl-generic, 1.7-SNAPSHOT",
				"org.apache.xml, com.springsource.org.apache.xml.resolver, 1.2.0",
				"org.apache.xmlcommons, com.springsource.org.apache.xmlcommons, 1.3.4",
				"org.apache.xerces, com.springsource.org.apache.xerces, 2.9.1",
				"org.apache.xalan, com.springsource.org.apache.xml.serializer, 2.7.1",
				"org.apache.xalan, com.springsource.org.apache.xalan, 2.7.1",
				"org.apache.xml, com.springsource.org.apache.xml.security, 1.4.2",
				"org.opensaml, com.springsource.org.opensaml, 1.1.0",
				"org.apache.ws, com.springsource.org.apache.ws.security, 1.5.8",
				"net.sf.taverna.t2.activities, wsdl-activity, 2.0-SNAPSHOT",
				"uk.org.taverna.platform, activity, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, execution-local, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, run, 0.0.1-SNAPSHOT" };
	}

	public void waitForResult(Map<String, T2Reference> results, String port, WorkflowReport report)
			throws InterruptedException {
		int wait = 0;
		while (!results.containsKey(port) && wait++ < 10) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

}
