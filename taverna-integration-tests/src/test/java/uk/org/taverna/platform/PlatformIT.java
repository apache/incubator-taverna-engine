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
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.databundle.ErrorDocument;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

@Ignore
public class PlatformIT {

	public WorkflowBundle loadWorkflow(String t2FlowFile, WorkflowBundleIO workflowBundleIO)
			throws Exception {
		URL wfResource = getClass().getResource(t2FlowFile);
		assertNotNull(wfResource);
		return workflowBundleIO.readBundle(wfResource.openStream(), null);
	}

	public File loadFile(String fileName) throws IOException, FileNotFoundException {
		File file = File.createTempFile("platform-test", null);
		file.deleteOnExit();
		FileUtils.copyURLToFile(getClass().getResource(fileName), file);
		return file;
	}

	public void printErrors(Path error) {
		try {
			ErrorDocument errorDocument = DataBundles.getError(error);
			String message = errorDocument.getMessage();
			if (message != null) {
				System.out.println(message);
			}
			String trace = errorDocument.getTrace();
			if (trace != null) {
				System.out.println(trace);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean checkResult(Path result, String expectedResult) {
		if (DataBundles.isError(result)) {
			printErrors(result);
			return false;
		} else {
			String resultValue;
			if (DataBundles.isValue(result)) {
				try {
					resultValue = DataBundles.getStringValue(result);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else if (DataBundles.isReference(result)) {
				try {
					URI reference = DataBundles.getReference(result);
					resultValue = IOUtils.toString(reference);
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				System.out.println("Expected a value or reference");
				return false;
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
			if (printReport) {
				System.out.println(report);
			}
			Thread.sleep(500);
		}
		if (printReport) {
			System.out.println(report);
		}
		return report.getState().equals(state);
	}

	public void waitForResults(Path outputs, WorkflowReport report, String... ports)
			throws InterruptedException {
		int wait = 0;
		while (!resultsReady(outputs, ports) && wait++ < 20) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

	private boolean resultsReady(Path outputs, String... ports) {
		for (String port : ports) {
			try {
				if (DataBundles.isMissing(DataBundles.getPort(outputs, port))) {
					return false;
				}
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

}
