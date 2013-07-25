/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester
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
package net.sf.taverna.t2.workflowmodel.serialization.xml.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize;

import org.jdom.Element;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;


public class DispatchLayerXMLDeserializerTest extends DeserializerTestsHelper {
	DispatchLayerXMLDeserializer deserializer = DispatchLayerXMLDeserializer.getInstance();

	@Test
	@Ignore("Config type changed to JSON")
	public void testDispatchLayer() throws Exception {
		Element el = loadXMLFragment("dispatchLayer.xml");
		DispatchLayer<?> layer = deserializer.deserializeDispatchLayer(el);
		assertTrue("Should be a Parallelize layer",layer instanceof Parallelize);
		Parallelize para = (Parallelize)layer;
		assertTrue("config should be ParellizeConfig",para.getConfiguration() instanceof JsonNode);
		assertEquals("max jobs should be 7",7,((JsonNode)para.getConfiguration()).get("maximumJobs").intValue());
	}
}
