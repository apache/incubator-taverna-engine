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
package net.sf.taverna.platform.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import static net.sf.taverna.platform.spring.RavenConstants.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Pulls artifact attributes out of beans and decorates the bean definitions
 * with an artifact property, this can then be used to intervene in the
 * classloading strategy in the modified raven aware application context
 * implementation.
 * 
 * @author Tom Oinn
 * 
 */
public class ArtifactDefinitionDecorator implements BeanDefinitionDecorator {

	private Log log = LogFactory.getLog(ArtifactDefinitionDecorator.class);
	
	public BeanDefinitionHolder decorate(Node source,
			BeanDefinitionHolder holder, ParserContext context) {
		AbstractBeanDefinition definition = ((AbstractBeanDefinition) holder
				.getBeanDefinition());
		Attr attribute = (Attr) source;
		if (attribute.getLocalName().equals(ARTIFACT_XML_ATTRIBUTE_NAME)) {
			String artifactSpecifier = attribute.getValue();
			definition.setAttribute(ARTIFACT_BEAN_ATTRIBUTE_NAME,
					artifactSpecifier);
		} else if (attribute.getLocalName().equals(
				REPOSITORY_XML_ATTRIBUTE_NAME)) {
			String repositoryBeanName = attribute.getValue();
			definition.setAttribute(REPOSITORY_BEAN_ATTRIBUTE_NAME,
					repositoryBeanName);
			String[] dependsOn = definition.getDependsOn();
			if (dependsOn == null) {
				dependsOn = new String[] { repositoryBeanName };
			} else {
				List<String> dependencies = new ArrayList<String>(Arrays
						.asList(dependsOn));
				dependencies.add(repositoryBeanName);
				dependsOn = dependencies.toArray(new String[0]);
			}
			definition.setDependsOn(dependsOn);
		}
		log.debug("Decorated bean '"+holder.getBeanName()+"'");
		return holder;
	}
}
