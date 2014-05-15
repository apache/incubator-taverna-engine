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
package net.sf.taverna.t2.workflowmodel.impl;

import java.io.IOException;

import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityAndBeanWrapper;
import net.sf.taverna.t2.workflowmodel.processor.activity.DisabledActivity;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * An Edit that is responsible for configuring a {@link Configurable} with a
 * given configuration bean.
 * 
 * @author Stuart Owen
 * @author Stian Soiland-Reyes
 */
@SuppressWarnings("unchecked")
public class ConfigureEdit<SubjectInterface extends Configurable, SubjectType extends SubjectInterface>
		extends AbstractEdit<SubjectInterface, SubjectType> {

	private static Logger logger = Logger
			.getLogger(ConfigureEdit.class);

	private final Object configurationBean;

	private Element previousBean;

	private ClassLoader cl;

	public ConfigureEdit(Class<?> subjectType,
			SubjectInterface configurable, Object configurationBean) {
		super(subjectType, configurable);
		this.configurationBean = configurationBean;
	}

	@Override
	protected void doEditAction(SubjectType subject) throws EditException {
		if (subject.getConfiguration() == null) {
			previousBean = null;
		} else {
			try {
//				previousBean = beanSerialiser.beanAsElement(subject
//						.getConfiguration());
			} catch (Exception e) {
				logger.error("Error serializing configuration bean for: "
						+ subject);
				throw new EditException(
						"Error serializing configuration bean for: " + subject,
						e);
			}
		}

		try {
			// FIXME: Should clone bean on configuration to prevent caller from
			// modifying bean afterwards
			subject.configure(configurationBean);
			if (subject instanceof DisabledActivity) {
			    cl = ((ActivityAndBeanWrapper)configurationBean).getActivity().getClass().getClassLoader();
			}
		} catch (ConfigurationException e) {
			logger.error("Error configuring :"
					+ subject.getClass().getSimpleName(), e);
			throw new EditException(e);
		}
	}

	protected Object cloneBean(Object object) throws JDOMException, IOException {
//		Element element = beanSerialiser.beanAsElement(object);
//		return beanDeSerialiser.createBean(element);
		return object;
	}

	@Override
	protected void undoEditAction(SubjectType subject) {
		try {
			if (previousBean == null) {
				logger.warn("Ignoring attempt to reconfiguring " + subject + " with a null-bean");
				// which would breaks most activities if undoing a recent 
				// dragging of activity to  dataflow
				// subject.configure(null);
			} else {
//				Object bean = beanDeSerialiser.createBean(previousBean);
//				subject.configure(bean);
			}
		} catch (/*Configuration*/Exception e) {
			logger.error("There was an error reconfiguring " + subject
					+ " during an undo");
		}
	}
}
