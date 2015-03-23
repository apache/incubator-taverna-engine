/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.annotation.annotationbeans;

import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.AppliesTo;

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
