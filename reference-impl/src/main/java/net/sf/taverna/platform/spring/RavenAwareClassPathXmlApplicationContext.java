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

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A subclass of the ClassPathXmlApplicationContext which uses the raven aware
 * bean factory and therefore can cope with the presence of raven repository and
 * artifact attributes correctly on beans within the configuration file. Use in
 * place of ClassPathXmlApplicationContext to magically enable raven support.
 * 
 * @author Tom Oinn
 * 
 */
public class RavenAwareClassPathXmlApplicationContext extends
		ClassPathXmlApplicationContext {

	private static DefaultListableBeanFactory factory = new RavenAwareListableBeanFactory();

	public RavenAwareClassPathXmlApplicationContext(String param) {
		super(param);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return factory;
	}
}
