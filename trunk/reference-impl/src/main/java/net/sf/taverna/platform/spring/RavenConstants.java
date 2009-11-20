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

/**
 * String constants used by the raven spring support package.
 * 
 * @author Tom Oinn
 * 
 */
public class RavenConstants {

	/**
	 * The name of the property defined on raven-enabled BeanDefinition
	 * instances to point to the name of the repository bean within the bean
	 * factory
	 */
	public static String REPOSITORY_BEAN_ATTRIBUTE_NAME = "ravenRepositoryBean";

	/**
	 * The name of the property defined on raven-enabled BeanDefinition
	 * instances specifying the artifact that should be used to load this bean
	 * definition's associated bean class. The artifact is specified as a
	 * group:artifact:version string.
	 */
	public static String ARTIFACT_BEAN_ATTRIBUTE_NAME = "ravenArtifact";

	/**
	 * The XML attribute name used to decorate the raven-enabled bean definition
	 * in the application context configuration to point to the repository bean
	 * id within that definition
	 */
	public static String REPOSITORY_XML_ATTRIBUTE_NAME = "repository";

	/**
	 * The XML attribute name used to specify the artifact to be used by a
	 * particular raven-enabled bean within the configuration xml.
	 */
	public static String ARTIFACT_XML_ATTRIBUTE_NAME = "artifact";

}
