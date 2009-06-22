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

import net.sf.taverna.raven.log.Log;
import net.sf.taverna.raven.repository.impl.LocalRepository;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.impl.Tools;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;

import org.jdom.Element;

public class DispatchLayerXMLDeserializer extends AbstractXMLDeserializer {
	private static DispatchLayerXMLDeserializer instance = new DispatchLayerXMLDeserializer();

	private static Log logger = Log.getLogger(DispatchLayerXMLDeserializer.class);

	private DispatchLayerXMLDeserializer() {

	}

	public static DispatchLayerXMLDeserializer getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public DispatchLayer<?> deserializeDispatchLayer(Element element) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Element ravenElement = element.getChild(RAVEN,T2_WORKFLOW_NAMESPACE);
		ClassLoader cl = Tools.class.getClassLoader();
		if (ravenElement != null) {
			try {
				cl = getRavenLoader(ravenElement);
			} catch (Exception ex) {
				logger.error(ex);
				// TODO - handle this properly, either by logging correctly or
				// by going back to the repository and attempting to fetch the
				// offending missing artifacts
			}
		}
		String className = element.getChild(CLASS,T2_WORKFLOW_NAMESPACE).getTextTrim();
		Class<? extends DispatchLayer> c = (Class<? extends DispatchLayer>) cl
				.loadClass(className);
		DispatchLayer<Object> layer = c.newInstance();

		// Handle the configuration of the dispatch layer
		Element configElement = element.getChild(CONFIG_BEAN,T2_WORKFLOW_NAMESPACE);
		Object configObject = createBean(configElement, cl);
		try {
			layer.configure(configObject);
		} catch (ConfigurationException e) {
			// TODO - handle this properly
			logger.error(e);
		}

		return layer;
	}
}
