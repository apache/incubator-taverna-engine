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

import java.io.IOException;
import java.io.InputStream;

import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class DeserializerTestsHelper {

	protected Edits edits = new EditsImpl();
	
	protected Element loadXMLFragment(String resourceName) throws Exception {
		InputStream inStream = DeserializerImplTest.class
				.getResourceAsStream("/serialized-fragments/" + resourceName);

		if (inStream==null) throw new IOException("Unable to find resource for serialized fragment :"+resourceName);
		SAXBuilder builder = new SAXBuilder();
		return builder.build(inStream).detachRootElement();
	}
}
