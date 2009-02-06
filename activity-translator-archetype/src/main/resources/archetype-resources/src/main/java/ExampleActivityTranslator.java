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
package ${packageName};

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.embl.ebi.escience.scufl.Processor;

import net.sf.taverna.t2.activities.${artifactId}Activity;
import net.sf.taverna.t2.activities.${artifactId}ActivityConfigurationBean;
import net.sf.taverna.t2.cyclone.activity.AbstractActivityTranslator;
import net.sf.taverna.t2.cyclone.activity.ActivityTranslationException;
import net.sf.taverna.t2.cyclone.activity.ActivityTranslator;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * An ActivityTranslator specifically for translating a Taverna 1 ${artifactId} Processor to an equivalent Taverna 2 ${artifactId} Activity
 * 
 * @see ActivityTranslator
 */
public class ${artifactId}ActivityTranslator extends AbstractActivityTranslator<${artifactId}ActivityConfigurationBean> {

	@Override
	protected ${artifactId}ActivityConfigurationBean createConfigType(
			Processor processor) throws ActivityTranslationException {
		${artifactId}ActivityConfigurationBean bean = new ${artifactId}ActivityConfigurationBean();
		bean.setValue(getServiceLocation(processor));
		return bean;
	}

	@Override
	protected Activity<${artifactId}ActivityConfigurationBean> createUnconfiguredActivity() {
		return new ${artifactId}Activity();
	}
	
	private String getServiceLocation(Processor processor) throws ActivityTranslationException {
		try {
			Method m=processor.getClass().getMethod("getServiceLocation");
			return (String)m.invoke(processor);
		} catch (SecurityException e) {
			throw new ActivityTranslationException("The was a Security exception whilst trying to invoke getServiceLocation through introspection",e);
		} catch (NoSuchMethodException e) {
			throw new ActivityTranslationException("The processor does not have the method getServiceLocation, an therefore does not conform to being an ${artifactId} processor",e);
		} catch (IllegalArgumentException e) {
			throw new ActivityTranslationException("The method getServiceLocation on the ${artifactId} processor had unexpected arguments",e);
		} catch (IllegalAccessException e) {
			throw new ActivityTranslationException("Unable to access the method getServiceLocation on the ${artifactId} processor",e);
		} catch (InvocationTargetException e) {
			throw new ActivityTranslationException("An error occurred invoking the method getServiceLocation on the ${artifactId} processor",e);
		}
	}
	
	public boolean canHandle(Processor processor) {
		return processor.getClass().getName().equals("org.embl.ebi.escience.scuflworkers.${artifactId.toLowerCase()}${artifactId}Processor");
	}

}
