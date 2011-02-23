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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.platform.OsgiPlatform;

import uk.org.taverna.platform.report.WorkflowReport;

public class PlatformTest extends AbstractConfigurableBundleCreatorTests {

	protected OsgiPlatform createPlatform() {
		OsgiPlatform platform = super.createPlatform();
		Properties config = platform.getConfigurationProperties();
		config.setProperty("org.osgi.framework.system.packages.extra", "com.sun.org.apache.xml.internal.utils");
		return platform;
	}

	protected String[] getTestBundlesNames() {
		return new String[] {
				"com.thoughtworks.xstream, com.springsource.com.thoughtworks.xstream, 1.2.2",
				"javax.servlet, com.springsource.javax.servlet, 2.5.0",
				"javax.activation, com.springsource.javax.activation, 1.1.1",
				"javax.jms, com.springsource.javax.jms, 1.1.0",
				"javax.mail, com.springsource.javax.mail, 1.4.0",
				"javax.xml.soap, com.springsource.javax.xml.soap, 1.3.0",
				"javax.xml.rpc, com.springsource.javax.xml.rpc, 1.1.0",
				"javax.xml.stream, com.springsource.javax.xml.stream, 1.0.1",
				"javax.wsdl, com.springsource.javax.wsdl, 1.6.1",
				"org.apache.axis, com.springsource.org.apache.axis, 1.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.codec, 1.3.0",
				"org.apache.commons, com.springsource.org.apache.commons.discovery, 0.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.httpclient, 3.1.0",
				"org.apache.commons, com.springsource.org.apache.commons.io, 1.4.0",
				"org.apache.commons, com.springsource.org.apache.commons.lang, 2.5.0",
				"org.apache.commons, com.springsource.org.apache.commons.net, 1.4.1",
				"org.apache.xml, com.springsource.org.apache.xml.resolver, 1.2.0",
				"org.apache.xmlbeans, com.springsource.org.apache.xmlbeans, 2.4.0",
				"org.apache.xmlcommons, com.springsource.org.apache.xmlcommons, 1.3.4",
				"org.apache.xerces, com.springsource.org.apache.xerces, 2.9.1",
				"org.apache.xalan, com.springsource.org.apache.xml.serializer, 2.7.1",
				"org.apache.xalan, com.springsource.org.apache.xalan, 2.7.1",
				"org.apache.xml, com.springsource.org.apache.xml.security, 1.4.2",
				"org.apache.ws, com.springsource.org.apache.ws.security, 1.5.8",
				"org.beanshell, com.springsource.bsh, 2.0.0.b4",
				"org.biomart, martservice, 2.0-SNAPSHOT",
				"org.dom4j, com.springsource.org.dom4j, 1.6.1",
				"org.jdom, com.springsource.org.jdom, 1.1.0",
				"org.opensaml, com.springsource.org.opensaml, 1.1.0",
				"org.xmlpull, com.springsource.org.xmlpull, 1.1.3.4-O",
				"net.sf.taverna, wsdl-generic, 1.9-SNAPSHOT",
				"net.sf.taverna.jedit, jedit-syntax, 2.2.4-SNAPSHOT",
				"net.sf.taverna.t2.activities, beanshell-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, biomart-activity, 2.0-SNAPSHOT",
				/*"net.sf.taverna.t2.activities, biomoby-activity, 2.0-SNAPSHOT",*/
				"net.sf.taverna.t2.activities, dataflow-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, dependency-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, localworker-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, rshell-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, soaplab-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, spreadsheet-import-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, stringconstant-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.activities, wsdl-activity, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-api, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-core-extensions, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, workflowmodel-impl, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-api, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-core-extensions, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.core, reference-impl, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.lang, ui, 2.0-SNAPSHOT",
				"net.sf.taverna.t2.lang, observer, 2.0-SNAPSHOT",
				"uk.org.taverna.platform, activity, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, execution, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, execution-local, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, report, 0.0.1-SNAPSHOT",
				"uk.org.taverna.platform, run, 0.0.1-SNAPSHOT",
				// FIXME: Add the other scufl2 modules
				"uk.org.taverna.scufl2, scufl2-api, 0.1-SNAPSHOT",
				"uk.org.taverna.scufl2, scufl2-ucfpackage, 0.1-SNAPSHOT",
				"uk.org.taverna.scufl2, scufl2-t2flow, 0.1-SNAPSHOT"};
	}
	

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}


	public void printErrors(ReferenceService referenceService, T2Reference resultReference) {
		if (resultReference.getDepth() > 0) {
			IdentifiedList<T2Reference> list = referenceService.getListService().getList(
					resultReference);
			for (T2Reference t2Reference : list) {
				printErrors(referenceService, t2Reference);
			}
		} else if (resultReference.containsErrors()) {
			ErrorDocument error = referenceService.getErrorDocumentService().getError(
					resultReference);
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
			Set<T2Reference> errorReferences = error.getErrorReferences();
			for (T2Reference t2Reference : errorReferences) {
				printErrors(referenceService, t2Reference);
			}
		}
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
