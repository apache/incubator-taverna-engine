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

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.data.DataService;

public class DataIT extends PlatformIT {

	private DataService dataService;

	private ReferenceService referenceService;

	public void testData() throws Exception {
		setup();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String xml = dataService.convertToXML(inputs, referenceService);
		System.out.println(xml);

	}

	private void setup() throws InvalidSyntaxException {
		ServiceReference dataServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.data.DataService");
		dataService = (DataService) bundleContext.getService(dataServiceReference);

		ServiceReference[] referenceServiceReferences = bundleContext.getServiceReferences(
				"net.sf.taverna.t2.reference.ReferenceService",
		"(org.springframework.osgi.bean.name=inMemoryReferenceService)");
		assertEquals(1, referenceServiceReferences.length);
		referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReferences[0]);

	}

}
