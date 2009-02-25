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
package net.sf.taverna.t2.workflowmodel.processor.activity.config;

import java.util.Collections;
import java.util.List;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;

/**
 * A bean that describes properties of an Input port.
 * 
 * @author Stuart Owen
 * @author Stian Soiland-Reyes
 * 
 */
public class ActivityInputPortDefinitionBean extends ActivityPortDefinitionBean {

	private List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes;

	private Class<?> translatedElementType;

	private boolean allowsLiteralValues;

	public List<Class<? extends ExternalReferenceSPI>> getHandledReferenceSchemes() {
		if (handledReferenceSchemes == null) {
			return Collections
					.<Class<? extends ExternalReferenceSPI>> emptyList();
		}
		return handledReferenceSchemes;
	}

	public void setHandledReferenceSchemes(
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes) {
		this.handledReferenceSchemes = handledReferenceSchemes;
	}

	public Class<?> getTranslatedElementType() {
		return translatedElementType;
	}

	public void setTranslatedElementType(Class<?> translatedElementType) {
		this.translatedElementType = translatedElementType;
	}

	public boolean getAllowsLiteralValues() {
		return allowsLiteralValues;
	}

	public void setAllowsLiteralValues(boolean allowsLiteralValues) {
		this.allowsLiteralValues = allowsLiteralValues;
	}

}
