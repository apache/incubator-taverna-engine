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
package net.sf.taverna.t2.annotation.annotationbeans;

import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AppliesTo;

/**
 * Generic annotation containing a pair of numeric values with precision
 * determined by the type parameter which form a bound.
 * 
 * @author Tom Oinn
 * 
 */
@AppliesTo(targetObjectType = { Object.class }, many = true)
public abstract class AbstractNumericRangeAssertion<NumericType extends Number>
		implements AnnotationBeanSPI {

	private NumericType upperNumericValue;

	private NumericType lowerNumericValue;
	
	/**
	 * Default constructor as mandated by java bean specification
	 */
	protected AbstractNumericRangeAssertion() {
		//
	}

	public NumericType getUpperNumericValue() {
		return upperNumericValue;
	}

	public void setUpperNumericValue(NumericType upperNumericValue) {
		this.upperNumericValue = upperNumericValue;
	}

	public NumericType getLowerNumericValue() {
		return lowerNumericValue;
	}

	public void setLowerNumericValue(NumericType lowerNumericValue) {
		this.lowerNumericValue = lowerNumericValue;
	}

}
