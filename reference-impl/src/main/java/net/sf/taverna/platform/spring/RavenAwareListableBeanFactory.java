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
import net.sf.taverna.raven.repository.Artifact;
import net.sf.taverna.raven.repository.ArtifactNotFoundException;
import net.sf.taverna.raven.repository.ArtifactStateException;
import net.sf.taverna.raven.repository.ArtifactStatus;
import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.raven.repository.Repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.util.ClassUtils;

/**
 * A subclass of DefaultListableBeanFactory which is aware of raven. Overrides
 * the class resolution mechanism within the bean factory to use raven's
 * artifact class loading if the appropriate attributes are defined on the bean
 * definition. These attributes are set by the ArtifactDefinitionDecorator,
 * which is in turn driven by a trivial extension to spring's XML based
 * configuration schema.
 * 
 * @author Tom Oinn
 * 
 */
public class RavenAwareListableBeanFactory extends DefaultListableBeanFactory {

	private Log log = LogFactory.getLog(RavenAwareListableBeanFactory.class);

	@SuppressWarnings("unchecked")
	@Override
	protected Class resolveBeanClass(RootBeanDefinition rbd, String beanName,
			Class[] typesToMatch) throws CannotLoadBeanClassException {

		try {

			if (rbd.hasBeanClass()) {
				return rbd.getBeanClass();
			}

			if (typesToMatch != null && getTempClassLoader() != null) {
				String className = rbd.getBeanClassName();
				ClassLoader tempClassLoader = getTempClassLoader();
				if (tempClassLoader instanceof DecoratingClassLoader) {
					DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
					for (int i = 0; i < typesToMatch.length; i++) {
						dcl.excludeClass(typesToMatch[i].getName());
					}
				}
				return (className != null ? ClassUtils.forName(className,
						tempClassLoader) : null);
			}

			if (rbd.hasAttribute(REPOSITORY_BEAN_ATTRIBUTE_NAME)
					&& rbd.hasAttribute(ARTIFACT_BEAN_ATTRIBUTE_NAME)) {
				String repositoryBeanName = (String) rbd
						.getAttribute(REPOSITORY_BEAN_ATTRIBUTE_NAME);
				String artifact = (String) rbd
						.getAttribute(ARTIFACT_BEAN_ATTRIBUTE_NAME);
				Repository repository = (Repository) getBean(repositoryBeanName);
				if (repository == null) {
					log.error("No repository bean with name '"
							+ repositoryBeanName + "' in this context");
					throw new CannotLoadBeanClassException(rbd
							.getResourceDescription(), beanName, rbd
							.getBeanClassName(), new ClassNotFoundException(
							"No such repository"));
				}
				synchronized (repository) {
					String[] s = artifact.split(":");
					if (s.length != 3) {
						log.error("Artifact specifier '" + artifact
								+ "' is badly formed for bean '" + beanName
								+ "'");
						throw new CannotLoadBeanClassException(rbd
								.getResourceDescription(), beanName, rbd
								.getBeanClassName(),
								new ClassNotFoundException(
										"Badly formed artifact specifier"));
					}
					Artifact a = new BasicArtifact(s[0], s[1], s[2]);
					log.debug("Artifact " + a + " for bean name " + beanName);

					if (repository.getStatus(a) != ArtifactStatus.Ready) {
						log.debug("  has state " + repository.getStatus(a));
						repository.addArtifact(a);
						repository.update();
					}
					log.debug("  has state (2) " + repository.getStatus(a));
					try {
						// ClassLoader cl = repository.getLoader(a,
						// this.getClass()
						// .getClassLoader());
						ClassLoader cl;
						try {
						cl = repository.getLoader(a, null);
						}
						catch (ArtifactStateException ex) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								log.error(e);
							}
							cl = repository.getLoader(a, null);
						}
						log.debug("Loading class " + rbd.getBeanClassName()
								+ " from artifact " + a);

						return rbd.resolveBeanClass(cl);

					} catch (ArtifactNotFoundException ex) {
						log
								.error("No such artifact '" + a.toString()
										+ "'", ex);
						throw new CannotLoadBeanClassException(rbd
								.getResourceDescription(), beanName, rbd
								.getBeanClassName(),
								new ClassNotFoundException(ex.getMessage(), ex));
					} catch (ArtifactStateException ex) {
						log.error("Artifact in incorrect state '"
								+ a.toString() + "' is in state "
								+ ex.getState()
								+ " with reported state in repository "
								+ repository.getStatus(a) + " for bean "
								+ beanName, ex);
						throw new CannotLoadBeanClassException(rbd
								.getResourceDescription(), beanName, rbd
								.getBeanClassName(),
								new ClassNotFoundException(ex.getMessage(), ex));
					}
				}
			} else {
				return rbd.resolveBeanClass(getBeanClassLoader());
			}
		} catch (ClassNotFoundException ex) {
			throw new CannotLoadBeanClassException(
					rbd.getResourceDescription(), beanName, rbd
							.getBeanClassName(), ex);
		} catch (LinkageError err) {
			throw new CannotLoadBeanClassException(
					rbd.getResourceDescription(), beanName, rbd
							.getBeanClassName(), err);
		}

	}
}
