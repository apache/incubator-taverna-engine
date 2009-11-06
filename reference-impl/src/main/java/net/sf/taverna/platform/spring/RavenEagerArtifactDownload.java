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

import static net.sf.taverna.platform.spring.RavenConstants.ARTIFACT_BEAN_ATTRIBUTE_NAME;
import static net.sf.taverna.platform.spring.RavenConstants.REPOSITORY_BEAN_ATTRIBUTE_NAME;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.raven.repository.Artifact;
import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.raven.repository.Repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Include this bean in the application context to enumerate and force download
 * of any artifacts referenced by raven-enabled beans in the same context.
 * Doesn't do much other than print out the artifact list at the moment as it
 * appears the class definitions are loaded eagerly even if the beans themselves
 * aren't.
 * 
 * @author Tom Oinn
 * 
 */
public class RavenEagerArtifactDownload implements BeanFactoryPostProcessor {

	private Log log = LogFactory.getLog(RavenEagerArtifactDownload.class);

	/**
	 * Will use this method to automatically load all artifacts used by beans in
	 * the repository prior to instantiation, more efficient than doing so on
	 * initialization but something to leave for now.
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
			throws BeansException {
		log.debug("Eager load of raven artifacts enabled, loading...");
		Map<String, Set<Artifact>> artifactsToLoad = new HashMap<String, Set<Artifact>>();
		for (String beanName : factory.getBeanDefinitionNames()) {
			BeanDefinition bd = factory.getBeanDefinition(beanName);
			if (bd.hasAttribute(REPOSITORY_BEAN_ATTRIBUTE_NAME)
					&& bd.hasAttribute(ARTIFACT_BEAN_ATTRIBUTE_NAME)) {
				String repositoryBeanName = (String) bd
						.getAttribute(REPOSITORY_BEAN_ATTRIBUTE_NAME);
				String[] s = ((String) bd
						.getAttribute(ARTIFACT_BEAN_ATTRIBUTE_NAME)).split(":");
				Artifact a = new BasicArtifact(s[0], s[1], s[2]);
				if (!artifactsToLoad.containsKey(repositoryBeanName)) {
					artifactsToLoad.put(repositoryBeanName,
							new HashSet<Artifact>());
				}
				artifactsToLoad.get(repositoryBeanName).add(a);
			}
		}
		for (String repositoryBeanName : artifactsToLoad.keySet()) {
			Repository r = (Repository) factory.getBean(repositoryBeanName);
			log.debug("Repository : " + repositoryBeanName);
			for (Artifact a : artifactsToLoad.get(repositoryBeanName)) {
				r.addArtifact(a);
				log.debug("  " + a.toString());
			}
			r.update();
		}
		log.debug("Raven artifacts loaded");
	}

}
