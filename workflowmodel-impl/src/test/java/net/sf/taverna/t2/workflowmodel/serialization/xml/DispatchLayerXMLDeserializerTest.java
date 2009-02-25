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
package net.sf.taverna.t2.workflowmodel.serialization.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ParallelizeConfig;

import org.jdom.Element;
import org.junit.Test;


public class DispatchLayerXMLDeserializerTest extends DeserializerTestsHelper {
	DispatchLayerXMLDeserializer deserializer = DispatchLayerXMLDeserializer.getInstance();
	
	@Test
	public void testDispatchLayer() throws Exception {
		Element el = loadXMLFragment("dispatchLayer.xml");
		DispatchLayer<?> layer = deserializer.deserializeDispatchLayer(el);
		assertTrue("Should be a Parallelize layer",layer instanceof Parallelize);
		Parallelize para = (Parallelize)layer;
		assertTrue("config should be ParellizeConfig",para.getConfiguration() instanceof ParallelizeConfig);
		assertEquals("max jobs should be 7",7,((ParallelizeConfig)para.getConfiguration()).getMaximumJobs());
	}
}
