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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.ErrorValue;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class PlatformIT {

	public WorkflowBundle loadWorkflow(String t2FlowFile, WorkflowBundleIO workflowBundleIO) throws Exception {
		URL wfResource = getClass().getResource(t2FlowFile);
		assertNotNull(wfResource);
		return workflowBundleIO.readBundle(wfResource.openStream(), null);
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
		ErrorValue error = (ErrorValue) data.getValue();
		String message = error.getMessage();
		if (message != null) {
			System.out.println(message);
		}
		String exceptionMessage = error.getExceptionMessage();
		if (exceptionMessage != null) {
			System.out.println(exceptionMessage);
		}
		for (StackTraceElement stackTraceElement : error.getStackTrace()) {
			System.out.println(stackTraceElement.getClassName());
			System.out.println(stackTraceElement.getMethodName());
			System.out.println(stackTraceElement.getLineNumber());
		}
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
