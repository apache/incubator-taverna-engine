/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jndi.JNDIContextManager;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.platform.OsgiPlatform;
import org.springframework.osgi.test.platform.Platforms;

import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.configuration.database.DatabaseConfiguration;
import org.apache.taverna.platform.data.api.Data;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowReader;

public class PlatformIT extends AbstractConfigurableBundleCreatorTests {

	protected WorkflowBundleReader workflowBundleReader;
	protected CredentialManager credentialManager;
	protected MasterPasswordProvider masterPasswordProvider;
	protected DatabaseConfiguration databaseConfiguration;
	protected JNDIContextManager jndiContextManager;
	protected ApplicationConfiguration applicationConfiguration;

	protected String getPlatformName() {
		// return Platforms.FELIX;
		return Platforms.EQUINOX;
	}

	@Override
	protected OsgiPlatform createPlatform() {
		OsgiPlatform platform = super.createPlatform();
		Properties config = platform.getConfigurationProperties();
		config.setProperty("org.osgi.framework.system.packages.extra",
				"com.sun.org.apache.xml.internal.utils");
		return platform;
	}

	@Override
	protected String[] getTestBundlesNames() {
		return new String[] {
				"com.jcraft.jsch, com.springsource.com.jcraft.jsch, 0.1.41",
				"com.sun.xml, com.springsource.com.sun.xml.bind, 2.2.0",
				"com.sun.xml, com.springsource.com.sun.xml.fastinfoset, 1.2.2",
				"com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.2.2",
				"commons-dbcp, commons-dbcp, 1.4",
				"commons-pool, commons-pool, 1.5.6",
				"javax.activation, com.springsource.javax.activation, 1.1.1",
				"javax.jms, com.springsource.javax.jms, 1.1.0",
				"javax.mail, com.springsource.javax.mail, 1.4.0",
				"javax.servlet, com.springsource.javax.servlet, 2.5.0",
				"javax.transaction, com.springsource.javax.transaction, 1.1.0",// for derby client
				"javax.wsdl, com.springsource.javax.wsdl, 1.6.1",
				"javax.xml.bind, com.springsource.javax.xml.bind, 2.2.0",
				"javax.xml.rpc, com.springsource.javax.xml.rpc, 1.1.0",
				"javax.xml.soap, com.springsource.javax.xml.soap, 1.3.0",
				"javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
				"org.antlr, com.springsource.antlr, 2.7.6",
				"org.apache.aries, org.apache.aries.util, 0.3",
				"org.apache.aries.proxy, org.apache.aries.proxy.api, 0.3",
				"org.apache.aries.jndi, org.apache.aries.jndi, 0.3",
				"org.apache.axis, com.springsource.org.apache.axis, 1.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.cli, 1.2.0",
				"org.apache.commons, com.springsource.org.apache.commons.codec, 1.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.csv, 1.0.0.BUILD-20080106",
				"org.apache.commons, com.springsource.org.apache.commons.collections, 3.2.0",
				"org.apache.commons, com.springsource.org.apache.commons.discovery, 0.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
				"org.apache.commons, com.springsource.org.apache.commons.io, 1.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.lang, 2.5.0",
				"org.apache.commons, com.springsource.org.apache.commons.logging, 1.1.1",
				"org.apache.commons, com.springsource.org.apache.commons.net, 1.4.1",
				// "org.apache.derby, derby, 10.5.3.0_1",
				"org.apache.derby, com.springsource.org.apache.derby, 10.5.1000001.764942",
				"org.apache.derby, com.springsource.org.apache.derby.client, 10.5.1000001.764942",
				"org.apache.derby, com.springsource.org.apache.derby.drda, 10.5.1000001.764942",
				"org.apache.httpcomponents, com.springsource.org.apache.httpcomponents.httpclient, 4.1.1",
				"org.apache.httpcore, com.springsource.org.apache.httpcomponents.httpcore, 4.1",
				"org.apache.log4j, com.springsource.org.apache.log4j, 1.2.16",
				"org.apache.ws, com.springsource.org.apache.ws.security, 1.5.8",
				// "org.apache.ws.security, wss4j, 1.5.12",
				"org.apache.xml, com.springsource.org.apache.xml.resolver, 1.2.0",
				"org.apache.xmlbeans, com.springsource.org.apache.xmlbeans, 2.4.0",
				"org.apache.xmlcommons, com.springsource.org.apache.xmlcommons, 1.3.4",
				"org.apache.xalan, com.springsource.org.apache.xalan, 2.7.1",
				"org.apache.xalan, com.springsource.org.apache.xml.serializer, 2.7.1",
				"org.apache.xerces, com.springsource.org.apache.xerces, 2.9.1",
				"org.apache.xml, com.springsource.org.apache.xml.security, 1.4.2",
				"org.aspectj, com.springsource.org.aspectj.runtime, 1.6.0",
				"org.aspectj, com.springsource.org.aspectj.weaver, 1.6.0",
				"org.beanshell, com.springsource.bsh, 2.0.0.b4",
				"org.biomart, martservice, 2.0-SNAPSHOT",
				"org.bouncycastle, bcprov-jdk16, 1.46",
				"org.dom4j, com.springsource.org.dom4j, 1.6.1",
				"org.hibernate, com.springsource.org.hibernate, 3.2.6.ga",
				"org.jaxen, com.springsource.org.jaxen, 1.1.1",
				"org.jboss.javassist, com.springsource.javassist, 3.3.0.ga",
				"org.jdom, com.springsource.org.jdom, 1.1.0",
				"org.jvnet.staxex, com.springsource.org.jvnet.staxex, 1.0.0",
				"org.objectweb.asm, com.springsource.org.objectweb.asm, 1.5.3",
				"org.objectweb.asm, com.springsource.org.objectweb.asm.attrs, 1.5.3",
				"org.opensaml, com.springsource.org.opensaml, 1.1.0",
				"org.springframework, org.springframework.jdbc, 3.0.0.RC1",
				"org.springframework, org.springframework.orm, 3.0.0.RC1",
				"org.springframework, org.springframework.transaction, 3.0.0.RC1",
				// "org.springframework, org.springframework.beans, 3.0.5.RELEASE",
				// "org.springframework, org.springframework.core, 3.0.5.RELEASE",
				// "org.springframework, org.springframework.context, 3.0.5.RELEASE",
				// "org.springframework, org.springframework.transaction, 3.0.5.RELEASE",
				"org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",
				"net.sf.taverna, wsdl-generic, 1.10-SNAPSHOT",
				"net.sf.taverna.jedit, jedit-syntax, 2.2.4-SNAPSHOT",
				"net.sf.taverna.t2.activities, apiconsumer-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, beanshell-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, biomart-activity, 2.0-SNAPSHOT",
				// "net.sf.taverna.t2.activities, biomoby-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, dataflow-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, dependency-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, external-tool-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, localworker-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, rest-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, rshell-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, soaplab-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, spreadsheet-import-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, stringconstant-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, wsdl-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, xpath-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, provenance-derby, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, provenance-mysql, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, provenanceconnector, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-api, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-core-extensions, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-impl, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-api, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-core-extensions, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-impl, 2.0-SNAPSHOT",
				// "net.sf.taverna.t2.infrastructure, appconfig, 3.0-SNAPSHOT",
				"net.sf.taverna.t2.lang, ui, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.lang, observer, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.security, credential-manager, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.security, credential-manager-impl, 2.0-SNAPSHOT",
				"net.sourceforge.cglib, com.springsource.net.sf.cglib, 2.1.3",
				"uk.org.taverna.configuration, taverna-app-configuration-api, 0.1.0-SNAPSHOT",
				"uk.org.taverna.configuration, taverna-app-configuration-impl, 0.1.0-SNAPSHOT",
				"uk.org.taverna.configuration, taverna-configuration-api, 0.1.0-SNAPSHOT",
				"uk.org.taverna.configuration, taverna-configuration-impl, 0.1.0-SNAPSHOT",
				"uk.org.taverna.configuration, taverna-database-configuration-api, 0.1.0-SNAPSHOT",
				"uk.org.taverna.configuration, taverna-database-configuration-impl, 0.1.0-SNAPSHOT",
				"uk.org.taverna.platform, report, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, data, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, execution-local, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, execution-remote, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-capability-api, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-capability-impl, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-execution-api, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-execution-impl, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-run-api, 0.1.2-SNAPSHOT",
				"uk.org.taverna.platform, taverna-run-impl, 0.1.2-SNAPSHOT",
				"uk.org.taverna.osgi.services, xml-parser-service, 0.0.1-SNAPSHOT",
				"uk.org.taverna.osgi.services, xml-transformer-service, 0.0.1-SNAPSHOT",
				// FIXME: Add the other scufl2 modules
				"org.apache.taverna.scufl2, scufl2-api, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-rdfxml, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-ucfpackage, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-t2flow, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-validation, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-validation-correctness, 0.9.2",
				"org.apache.taverna.scufl2, scufl2-validation-structural, 0.9.2",
				"net.sf.taverna.t2, results, 2.0-SNAPSHOT",
				"net.sf.taverna.t2, baclava, 0.1-SNAPSHOT",
		// "net.sf.taverna.t2.taverna-commandline, taverna-commandline-common, 2.0-SNAPSHOT"
		};
	}

	protected void setup() throws Exception {

		if (masterPasswordProvider == null) {
			masterPasswordProvider = new MasterPasswordProvider() {
				public String getMasterPassword(boolean firstTime) {
					return "test";
				}

				public void setMasterPassword(String password) {
				}

				public int getProviderPriority() {
					return 0;
				}
			};
			bundleContext.registerService(
					"net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider",
					masterPasswordProvider, null);
		}

		if (credentialManager == null) {
			ServiceReference credentialManagerReference = bundleContext
					.getServiceReference("net.sf.taverna.t2.security.credentialmanager.CredentialManager");
			credentialManager = (CredentialManager) bundleContext
					.getService(credentialManagerReference);
		}

		if (workflowBundleReader == null) {
			ServiceReference[] workflowBundleReaderReferences = bundleContext.getServiceReferences(
					"org.apache.taverna.scufl2.api.io.WorkflowBundleReader", null);
			for (ServiceReference serviceReference : workflowBundleReaderReferences) {
				workflowBundleReader = (WorkflowBundleReader) bundleContext
						.getService(serviceReference);
				if (workflowBundleReader.getMediaTypes().contains(
						T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML)) {
					break;
				}
			}
		}

		if (databaseConfiguration == null) {
			ServiceReference databaseConfigurationReference = bundleContext
					.getServiceReference("uk.org.taverna.configuration.database.DatabaseConfiguration");
			databaseConfiguration = (DatabaseConfiguration) bundleContext
					.getService(databaseConfigurationReference);
			ServiceReference jndiContextManagerReference = bundleContext
					.getServiceReference("org.osgi.service.jndi.JNDIContextManager");
			jndiContextManager = (JNDIContextManager) bundleContext
					.getService(jndiContextManagerReference);
			ServiceReference applicationConfigurationReference = bundleContext
					.getServiceReference("uk.org.taverna.configuration.app.ApplicationConfiguration");
			applicationConfiguration = (ApplicationConfiguration) bundleContext
					.getService(applicationConfigurationReference);
		}

	}

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(Constants.FRAMEWORK_VENDOR + " = "
				+ bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(Constants.FRAMEWORK_VERSION + " = "
				+ bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(Constants.FRAMEWORK_EXECUTIONENVIRONMENT + " = "
				+ bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
		System.out.println(Constants.OSGI_IMPL_VERSION_KEY + " = "
				+ bundleContext.getProperty(Constants.OSGI_IMPL_VERSION_KEY));
	}

	public WorkflowBundle loadWorkflow(String t2FlowFile) throws Exception {
		URL wfResource = getClass().getResource(t2FlowFile);
		assertNotNull(wfResource);
		return workflowBundleReader.readBundle(wfResource.openStream(),
				T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML);
	}

	public File loadFile(String fileName) throws IOException, FileNotFoundException {
		File file = File.createTempFile("platform-test", null);
		InputStream inputStream = getClass().getResource(fileName).openStream();
		OutputStream outputStream = new FileOutputStream(file);
		byte[] buffer = new byte[64];
		int length = -1;
		while ((length = inputStream.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, length);
		}
		outputStream.flush();
		outputStream.close();
		return file;
	}

	public void printErrors(Data data) {
			ErrorDocument error = (ErrorDocument) data.getValue();
			String message = error.getMessage();
			if (message != null) {
				System.out.println(message);
			}
			String exceptionMessage = error.getExceptionMessage();
			if (exceptionMessage != null) {
				System.out.println(exceptionMessage);
			}
			for (StackTraceElementBean stackTraceElementBean : error.getStackTraceStrings()) {
				System.out.println(stackTraceElementBean.getClassName());
				System.out.println(stackTraceElementBean.getMethodName());
				System.out.println(stackTraceElementBean.getLineNumber());
			}
//			Set<T2Reference> errorReferences = error.getErrorReferences();
//			for (T2Reference t2Reference : errorReferences) {
//				printErrors(referenceService, t2Reference);
//			}
	}

	public boolean checkResult(Data result, String expectedResult) {
		if (result.isError()) {
			printErrors(result);
			return false;
		} else {
			Object resultObject = result.getValue();
			String resultValue = null;
			if (resultObject instanceof byte[]) {
				resultValue = new String((byte[]) resultObject);
			} else {
				resultValue = (String) resultObject;
			}

			if (resultValue.startsWith(expectedResult)) {
				return true;
			} else {
				System.out.println("Expected: " + expectedResult + ", Actual: " + resultValue);
				return false;
			}
		}
	}

	public boolean waitForState(WorkflowReport report, State state) throws InterruptedException {
		return waitForState(report, state, true);
	}

	public boolean waitForState(WorkflowReport report, State state, boolean printReport)
			throws InterruptedException {
		int wait = 0;
		while (!report.getState().equals(state) && wait++ < 30) {
			if (printReport)
				System.out.println(report);
			Thread.sleep(500);
		}
		return report.getState().equals(state);
	}

	public void waitForResults(Map<String, Data> results, WorkflowReport report, String... ports)
			throws InterruptedException {
		int wait = 0;
		while (!resultsReady(results, ports) && wait++ < 20) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

	private boolean resultsReady(Map<String, Data> results, String... ports) {
		for (String port : ports) {
			if (!results.containsKey(port)) {
				return false;
			}
		}
		return true;
	}

}
